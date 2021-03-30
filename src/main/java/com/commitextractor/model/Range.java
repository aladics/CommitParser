package com.commitextractor.model;

public class Range {
	private int start;
	private int end;
	
	
	public int getStart() {
		return start;
	}


	public int getEnd() {
		return end;
	}


	public Range(int start, int end) {
		this.start = start;
		this.end = end;
	}


	@Override
	public String toString() {
		return "Range [start=" + start + ", end=" + end + "]";
	}

}
