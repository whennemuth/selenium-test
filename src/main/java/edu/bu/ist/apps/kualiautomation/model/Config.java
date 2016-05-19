package edu.bu.ist.apps.kualiautomation.model;

import java.util.ArrayList;
import java.util.List;

public class Config {

	private List<Environment> environments = new ArrayList<Environment>();
	private String outputDir;
	public List<Environment> getEnvironments() {
		return environments;
	}
	public void setEnvironments(List<Environment> environments) {
		this.environments = environments;
	}
	public void addEnvironment(Environment environment) {
		this.environments.add(environment);
	}
	public void removeEnvironment(Environment environment) {
		this.environments.remove(environment);
	}
	public String getOutputDir() {
		return outputDir;
	}
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Config [environments=").append(environments).append(", outputDir=").append(outputDir)
				.append("]");
		return builder.toString();
	}
	
}
