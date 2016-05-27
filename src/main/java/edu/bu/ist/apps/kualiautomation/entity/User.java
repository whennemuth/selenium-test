package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@Table(name="user")
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private int id;

	@Column(name="first_name", nullable=false, length=45)
	private String firstName;

	@Column(name="last_name", nullable=false, length=45)
	private String lastName;

	//bi-directional many-to-one association to Suite
	@OneToMany(mappedBy="user")
	private List<Suite> suites;

	public User() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<Suite> getSuites() {
		return this.suites;
	}

	public void setSuites(List<Suite> suites) {
		this.suites = suites;
	}

	public Suite addSuite(Suite suite) {
		getSuites().add(suite);
		suite.setUser(this);

		return suite;
	}

	public Suite removeSuite(Suite suite) {
		getSuites().remove(suite);
		suite.setUser(null);

		return suite;
	}

}