package com.commitextractor;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommit.File;

import com.commitextractor.model.MethodNode;
import com.commitextractor.model.MethodRecord;
import com.commitextractor.model.Range;
import com.commitextractor.util.GitHubConnector;

public class GitHubMiner {
	private GitHubConnector github;
	private String currentCVE;
	
	public static final Logger logger = LogManager.getLogger(GitHubMiner.class);
	
	public GitHubMiner() {
		github = new GitHubConnector();
	}
	
	public List<File> getJavaFiles(GHCommit commit) throws IOException{
		return commit.getFiles().stream().filter(file -> file.getFileName().endsWith(".java")).collect(Collectors.toList());
	}
	
	public String getParentHash(File file, String commitSha, String parentSha) {
		String parentUrl = file.getRawUrl().toString().replace(commitSha, parentSha);
		
		if(file.getPreviousFilename() != null) {
			parentUrl = parentUrl.replace(file.getFileName(), file.getPreviousFilename());
		}
		
		return parentUrl;
	}
	
	public Set<MethodRecord> getAllMethodsFromCommit(String repo, String commitSha) throws IOException {
		Set<MethodRecord> methodRecords = new HashSet<>();
		GHCommit commit;
		String correctCommitSha;
		
		try {
			commit = github.get().getRepository(repo).getCommit(commitSha);
			
			//Hash is sometimes recognized but different than the one on github, setting it to valid value
			correctCommitSha = commit.getSHA1();
		}
		catch (org.kohsuke.github.HttpException e) {
			logger.warn("Commit '{}' not found in repository '{}'", commitSha, repo, e);
			return methodRecords;
		}
		for (File file : getJavaFiles(commit)) {		
			Set<MethodNode> toFileMethods = FileParser.parseToFileMethods(file);
			
			toFileMethods.forEach(changedMethod -> methodRecords.add(new MethodRecord(repo, correctCommitSha, file.getFileName(), changedMethod, file.getRawUrl().toString(), false)));
			
			for(String parentSha : commit.getParentSHA1s()) {
				String parentUrl = getParentHash(file, correctCommitSha, parentSha);
				
				if(FileParser.isURLExist(parentUrl)){
					List<Range> diffs = DiffParser.parse(file.getPatch(), DiffParser.ParseMode.FROM_FILE);
					Set<MethodNode> fromFileMethods = FileParser.parseFromFileMethods(new URL(parentUrl), diffs);
					methodRecords.addAll(fromFileMethods.stream()
							.map(method -> new MethodRecord(repo, parentSha, file.getFileName(), method, parentUrl, true))
							.collect(Collectors.toSet()));
				}
				else {
					logger.warn("[{}]: URL for parent is not valid: {}", currentCVE, parentUrl);
					break;
				}
			}
			
		}
		
		return methodRecords;
	}

	public String getCurrentCVE() {
		return currentCVE;
	}

	public void setCurrentCVE(String currentCVE) {
		this.currentCVE = currentCVE;
	}
	
}
