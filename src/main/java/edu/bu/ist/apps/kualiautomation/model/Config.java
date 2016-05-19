package edu.bu.ist.apps.kualiautomation.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Config {

	private List<Environment> environments = new ArrayList<Environment>();
	private String outputDir;
	private Environment currentEnvironment;
	private String lastUpdated;
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
	public Environment getCurrentEnvironment() {
		return currentEnvironment;
	}
	public void setCurrentEnvironment(Environment currentEnvironment) {
		this.currentEnvironment = currentEnvironment;
	}
	public String getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currentEnvironment == null) ? 0 : currentEnvironment.hashCode());
		result = prime * result + ((environments == null) ? 0 : environments.hashCode());
		result = prime * result + ((outputDir == null) ? 0 : outputDir.hashCode());
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
		Config other = (Config) obj;
		if (currentEnvironment == null) {
			if (other.currentEnvironment != null)
				return false;
		} else if (!currentEnvironment.equals(other.currentEnvironment))
			return false;
		if (environments == null) {
			if (other.environments != null)
				return false;
		} else if (!environments.equals(other.environments))
			return false;
		if (outputDir == null) {
			if (other.outputDir != null)
				return false;
		} else if (!outputDir.equals(other.outputDir))
			return false;
		return true;
	}
	
}
