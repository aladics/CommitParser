package com.commitextractor.util;

import java.io.IOException;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class GitHubConnector extends SingletonBase<GitHub> {

	@Override
	protected void init() throws IOException {
		try {
			instance = GitHubBuilder.fromPropertyFile().build();
		} catch (IOException e) {
			System.out.println("Error while connecting to GitHub.");
			throw e;
		}
	}

}
