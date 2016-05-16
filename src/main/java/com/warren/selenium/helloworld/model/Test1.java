package com.warren.selenium.helloworld.model;

import java.io.File;

public class Test1 {

	private String filename;
	private String filepath;
	private String error;
	
	private File f;
	
	public Test1(File f) {
		this.f = f;
	}
	public Test1() { }
	public String getFilename() {
		return f == null ? "CANCELLED" : f.getName();
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFilepath() {
		return f == null ? "CANCELLED" : f.getParent();
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
}
