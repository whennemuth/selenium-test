package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.ArrayList;
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
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="user", orphanRemoval=true)
	private List<Cycle> cycles = new ArrayList<Cycle>();

	//bi-directional many-to-one association to Config
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="user", orphanRemoval=true)
	private List<Config> configs = new ArrayList<Config>();

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

	public List<Cycle> getCycles() {
		return this.cycles;
	}

	public void setCycles(List<Cycle> cycles) {
		this.cycles = cycles;
	}

	public Cycle addCycle(Cycle cycle) {
		getCycles().add(cycle);
		cycle.setUser(this);

		return cycle;
	}

	public Cycle removeCycle(Cycle cycle) {
		getCycles().remove(cycle);
		cycle.setUser(null);

		return cycle;
	}
	public List<Config> getConfigs() {
		return this.configs;
	}

	public void setConfigs(List<Config> configs) {
		this.configs = configs;
	}

	public Config addConfig(Config config) {
		getConfigs().add(config);
		config.setUser(this);

		return config;
	}

	public Config removeConfig(Config config) {
		getConfigs().remove(config);
		config.setUser(null);

		return config;
	}

}