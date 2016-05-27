package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


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
	@ManyToOne
	@JoinColumn(name="suite_id", nullable=false)
	private Suite suite;

	//bi-directional many-to-one association to Module
	@OneToMany(mappedBy="cycle")
	private List<Module> modules;

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

	public Suite getSuite() {
		return this.suite;
	}

	public void setSuite(Suite suite) {
		this.suite = suite;
	}

	public List<Module> getModules() {
		return this.modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	public Module addModule(Module module) {
		getModules().add(module);
		module.setCycle(this);

		return module;
	}

	public Module removeModule(Module module) {
		getModules().remove(module);
		module.setCycle(null);

		return module;
	}

}