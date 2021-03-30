package com.commitextractor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.GHCommit.File;

import com.commitextractor.model.MethodNode;
import com.commitextractor.model.Range;
import com.commitextractor.util.visitor.CallableVisitor;
import com.github.javaparser.Position;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.CallableDeclaration;

public class FileParser {
	
	private static final Logger logger = LogManager.getLogger(FileParser.class);
	
	public static String getUrlContents(URL url) throws IOException {
		try (InputStream in = url.openStream()){
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
	
	
	public static boolean isURLExist(String url) throws IOException {
		URL u = new URL (url);
		HttpURLConnection huc =  ( HttpURLConnection )u.openConnection(); 
		huc.setRequestMethod ("HEAD"); 
		huc.connect(); 
		return huc.getResponseCode() == HttpURLConnection.HTTP_OK;
	}
	
	 public static Set<MethodNode> parseFromFileMethods(URL rawFileUrl, List<Range> diffs) throws IOException{
		Set<MethodNode> methods = new HashSet<>();
		
		CompilationUnit uc = StaticJavaParser.parse(getUrlContents(rawFileUrl));
		CallableVisitor methodExtractor = new CallableVisitor(diffs, methods) {
			@Override
			public void visitCallable(CallableDeclaration<?> n, Void arg) {
				Position beginPos = n.getBegin().get(), endPos = n.getEnd().get();
				boolean isMethodChanged = diffs.stream().
				anyMatch(diff -> (diff.getStart() <= beginPos.line && diff.getEnd() >= beginPos.line) || (diff.getStart() >= beginPos.line && diff.getStart() <= endPos.line));
				if(isMethodChanged) {
					addMethod(n);
				}
			}
		};
		methodExtractor.visit(uc, null);
		
		return methods;
	}
	 
	 public static Set<MethodNode> parseToFileMethods(File file) throws IOException{
		 Set<MethodNode> methods = new HashSet<>();
		 CompilationUnit uc = parseJavaFile(file);
		 List<Range> diffs = DiffParser.parse(file.getPatch(), DiffParser.ParseMode.TO_FILE);
		 
		 CallableVisitor methodExtractor = new CallableVisitor(diffs, methods) {
			@Override
			public void visitCallable(CallableDeclaration<?> n, Void arg) {
				Position beginPos = n.getBegin().get(), endPos = n.getEnd().get();
				boolean isMethodChanged = diffs.stream().
				anyMatch(diff -> (diff.getStart() <= beginPos.line && diff.getEnd() >= beginPos.line) || (diff.getStart() >= beginPos.line && diff.getStart() <= endPos.line));
				if(isMethodChanged) {
					addMethod(n);
				}
			}
		};
		methodExtractor.visit(uc, null);
		 
		return methods;
	 }

	private static CompilationUnit parseJavaFile(File file) throws IOException {
		CompilationUnit cu = null;
		try{
			cu = StaticJavaParser.parse(getUrlContents(file.getRawUrl()));
		}catch(com.github.javaparser.ParseProblemException e) {
			logger.error("Could not parse Java file: '{}'", file.getFileName(), e);
			throw e;
		}
		
		return cu;
	}
	
}