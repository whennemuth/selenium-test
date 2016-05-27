package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the module database table.
 * 
 */
@Entity
@Table(name="module")
@NamedQuery(name="Module.findAll", query="SELECT m FROM Module m")
public class Module implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private int id;

	@Column(nullable=false, length=45)
	private String name;

	@Column(nullable=false)
	private int sequence;

	//bi-directional many-to-one association to Cycle
	@ManyToOne
	@JoinColumn(name="cycle_id", nullable=false)
	private Cycle cycle;

	//bi-directional many-to-one association to Tab
	@OneToMany(mappedBy="module")
	private List<Tab> tabs;

	public Module() {
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

	public Cycle getCycle() {
		return this.cycle;
	}

	public void setCycle(Cycle cycle) {
		this.cycle = cycle;
	}

	public List<Tab> getTabs() {
		return this.tabs;
	}

	public void setTabs(List<Tab> tabs) {
		this.tabs = tabs;
	}

	public Tab addTab(Tab tab) {
		getTabs().add(tab);
		tab.setModule(this);

		return tab;
	}

	public Tab removeTab(Tab tab) {
		getTabs().remove(tab);
		tab.setModule(null);

		return tab;
	}

}