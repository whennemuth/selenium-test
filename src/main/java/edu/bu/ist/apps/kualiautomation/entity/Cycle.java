package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the cycle database table.
 * 
 */
@Entity
@Table(name="cycle")
@NamedQuery(name="Cycle.findAll", query="SELECT c FROM Cycle c")
public class Cycle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private int id;

	@Column(nullable=false, length=45)
	private String name;

	@Column(nullable=false)
	private int sequence;

	//bi-directional many-to-one association to Suite
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="cycle")
	private List<Suite> suites = new ArrayList<Suite>();

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;

	public Cycle() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Suite> getSuites() {
		return this.suites;
	}

	public void setSuites(List<Suite> suites) {
		this.suites = suites;
	}

	public Suite addSuite(Suite suite) {
		getSuites().add(suite);
		suite.setCycle(this);

		return suite;
	}

	public Suite removeSuite(Suite suite) {
		getSuites().remove(suite);
		suite.setCycle(null);

		return suite;
	}

}