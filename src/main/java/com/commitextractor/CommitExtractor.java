package com.commitextractor;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.commitextractor.model.MethodRecord;
import com.commitextractor.model.yaml.Commit;
import com.commitextractor.model.yaml.Vulnerability;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

@Command(name = "commit_parser", mixinStandardHelpOptions = true, version = "Commit Parser 1.0",
description = "Generates a csv of vulnerabilities based on YAML files from the project-kb repostiory.")
public class CommitExtractor implements Callable<Integer>{
	@Parameters(index = "0", description = "The path to the result csv file.")
	//public static String CSV_PATH = "F:\\work\\kutatas\\assurmos\\wp2\\database\\results\\vulnerabilities.csv";
	public static String CSV_PATH;
	@Parameters(index = "1", description = "Path to the root dir 'statements that contains the vulnerability yaml files.'")
	//public static String YAML_ROOT_PATH = "F:\\work\\kutatas\\assurmos\\wp2\\database\\eclipse_workspace\\project-kb\\statements";
	public static String YAML_ROOT_PATH;
	
	private static final Logger logger = LogManager.getLogger(CommitExtractor.class);
	
	
	@Override
	public Integer call() throws Exception {
		
		//Configurator.setRootLevel(Level.INFO);
		GitHubMiner miner = new GitHubMiner();
		List<Path> yamlFiles = listYamlFiles(YAML_ROOT_PATH);
		Path csv_path = initResultCSV();

		yamlFiles.forEach(file -> {
			miner.setCurrentCVE(file.getParent().getFileName().toString());
			Set<MethodRecord> commitMethods = processCommit(processYaml(file.toString()), miner);
			commitMethods.forEach(methodRecord -> 
			{
				try {
					Files.write(csv_path, methodRecord.toCSVRow().getBytes(), StandardOpenOption.APPEND);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		});
		
		return 0;
	}

	public static Path initResultCSV() throws IOException {
		Path path = Paths.get(CSV_PATH);
		
		//Writing the header
		Files.write(path, MethodRecord.getFields().getBytes(), StandardOpenOption.CREATE);
		
		return path;
	}
	
	public static List<Path> listYamlFiles(String rootDir) throws IOException{
		List<Path> result;
		Path path = Paths.get(rootDir);
		try (Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(file -> file.getFileName().toString().endsWith(".yaml"))
                    .collect(Collectors.toList());
        }
		return result;
	}
	
	public static Vulnerability processYaml(String path) {
		Yaml yaml = new Yaml(new Constructor(Vulnerability.class));
		InputStream input;
		Vulnerability vulnerability = null;
		try {
			input = new FileInputStream(new File(path));
			vulnerability = yaml.load(input);
		} catch (FileNotFoundException e) {
			logger.error("Yaml file not found: {}", path, e);
			e.printStackTrace();
		} catch (org.yaml.snakeyaml.error.YAMLException e) {
			logger.error("Couldn't parse YAML file: {}", path, e);
			vulnerability = null;
		}
		
		return vulnerability;
	}
	
	public static String parseRepository(String repo_url) throws IOException {
		String repo = "";
		
		repo_url = repo_url.replaceAll("\\.git", "");
		if(repo_url.contains("https://github.com/")) {
			repo = repo_url.replaceFirst("https://github.com/", "");
		}
		else if(repo_url.contains("git-wip-us.apache.org") || repo_url.contains("git.apache.org")) {
			repo =  "apache";
			repo += repo_url.substring(repo_url.lastIndexOf('/'));
		}
		else {
			throw new IOException(String.format("Unknown repository: {}", repo_url));
		}
		
		
		return repo;
	}
	
	public static Set<MethodRecord> processCommit(Vulnerability vulnerability, GitHubMiner miner) {		
		Set<MethodRecord> commitMethods = new HashSet<>();
		if(vulnerability == null) return commitMethods;
		
		String repo, hash;
		
		try {
			Commit commit = vulnerability.getFixes().get(0).getCommits().get(0);
			repo = parseRepository(commit.getRepository());
			hash = commit.getId();
			
			commitMethods.addAll(miner.getAllMethodsFromCommit(repo, hash));
		} catch(NullPointerException e) {
			logger.warn("Skipping vulnerability '{}': No fixes or commits", vulnerability.getVulnerability_id());
		} catch (IOException e) {
			logger.warn("Skipping vulnerability '{}': IOException", vulnerability.getVulnerability_id(), e);
		} catch(com.github.javaparser.ParseProblemException e){
			logger.warn("Skipping vulnerability '{}': Parse problem: %s", vulnerability.getVulnerability_id(), e.getMessage(), e);
		}
		
		return commitMethods;
	}
	
	
	public static void main(String[] args) {
		int exitCode = new CommandLine(new CommitExtractor()).execute(args);
        System.exit(exitCode);
	}
}