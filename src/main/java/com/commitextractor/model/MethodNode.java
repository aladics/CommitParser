package com.commitextractor.model;

public class MethodNode {
	private String methodName;
	private int lineStart;
	private int colStart;
	private int lineEnd;
	private int colEnd;
	
	
	
	
	public MethodNode(String methodName, int lineStart, int colStart, int lineEnd, int colEnd) {
		super();
		this.methodName = methodName;
		this.lineStart = lineStart;
		this.colStart = colStart;
		this.lineEnd = lineEnd;
		this.colEnd = colEnd;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public int getLineStart() {
		return lineStart;
	}
	public void setLineStart(int lineStart) {
		this.lineStart = lineStart;
	}
	public int getColStart() {
		return colStart;
	}
	public void setColStart(int colStart) {
		this.colStart = colStart;
	}
	public int getLineEnd() {
		return lineEnd;
	}
	public void setLineEnd(int lineEnd) {
		this.lineEnd = lineEnd;
	}
	public int getColEnd() {
		return colEnd;
	}
	public void setColEnd(int colEnd) {
		this.colEnd = colEnd;
	}
	
	
}
