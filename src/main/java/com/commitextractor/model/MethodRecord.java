package com.commitextractor.model;

public class MethodRecord {
	
	public MethodRecord(String repo, String commitHash, String fileName, MethodNode methodNode, String rawUrl, boolean bugged) {
		super();
		this.repo = repo;
		this.commitHash = commitHash;
		this.filePath = fileName;
		this.methodNode = methodNode;
		this.bugged = bugged;
		this.rawUrl = rawUrl;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (bugged ? 1231 : 1237);
		result = prime * result + ((commitHash == null) ? 0 : commitHash.hashCode());
		result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result + ((methodNode.getMethodName() == null) ? 0 : methodNode.getMethodName().hashCode());
		result = prime * result + ((repo == null) ? 0 : repo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodRecord other = (MethodRecord) obj;
		if (bugged != other.bugged)
			return false;
		if (commitHash == null) {
			if (other.commitHash != null)
				return false;
		} else if (!commitHash.equals(other.commitHash))
			return false;
		if (filePath == null) {
			if (other.filePath != null)
				return false;
		} else if (!filePath.equals(other.filePath))
			return false;
		if (methodNode.getMethodName() == null) {
			if (other.methodNode.getMethodName() != null)
				return false;
		} else if (!methodNode.getMethodName().equals(other.methodNode.getMethodName()))
			return false;
		if (repo == null) {
			if (other.repo != null)
				return false;
		} else if (!repo.equals(other.repo))
			return false;
		return true;
	}

	private String repo;
	private String commitHash;
	private String filePath;
	private MethodNode methodNode;
	private String rawUrl;
	private boolean bugged;
	
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getRawUrl() {
		return rawUrl;
	}

	public void setRawUrl(String rawUrl) {
		this.rawUrl = rawUrl;
	}

	public String getRepo() {
		return repo;
	}

	public void setRepo(String repo) {
		this.repo = repo;
	}

	public String getCommitHash() {
		return commitHash;
	}

	public void setCommitHash(String commitHash) {
		this.commitHash = commitHash;
	}

	public String getFileName() {
		return filePath;
	}

	public void setFileName(String fileName) {
		this.filePath = fileName;
	}

	public MethodNode getMethodNode() {
		return methodNode;
	}

	public void setMethodNode(MethodNode methodNode) {
		this.methodNode = methodNode;
	}

	public boolean getBugged() {
		return bugged;
	}

	public void setBugged(boolean bugged) {
		this.bugged = bugged;
	}
	
	

	
	public String toCSVRow() {
		String lineInfo = String.format("%d:%d", methodNode.getLineStart(), methodNode.getColStart());
		return String.format("%s,%s,%s,%s,%s,%s,%s\n", rawUrl, repo, commitHash, filePath, methodNode.getMethodName(), lineInfo ,bugged ? 1 : 0);
		
	}
	
	public static String getFields() {
		return String.format("%s,%s,%s,%s,%s,%s,%s\n", "Raw URL", "Repository", "Commit hash", "File path", "Method name", "Line:Col", "Vulnerable");
	}
	
	
}
