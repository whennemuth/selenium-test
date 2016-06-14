package edu.bu.ist.apps.kualiautomation.entity;

import javax.persistence.Transient;

public class AbstractEntity {

	@Transient
	private boolean transitory;

	public boolean isTransitory() {
		return transitory;
	}

	public void setTransitory(boolean transitory) {
		this.transitory = transitory;
	}

	// http://stackoverflow.com/questions/1795649/jpa-persisting-a-one-to-many-relationship
}
