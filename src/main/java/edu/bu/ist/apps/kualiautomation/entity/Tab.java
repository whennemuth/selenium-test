package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the tab database table.
 * 
 */
@Entity
@Table(name="tab")
@NamedQuery(name="Tab.findAll", query="SELECT t FROM Tab t")
public class Tab implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private int id;

	@Column(nullable=false, length=45)
	private String name;

	@Column(nullable=false)
	private int sequence;

	//bi-directional many-to-one association to LabelAndValue
	@OneToMany(mappedBy="tab")
	private List<LabelAndValue> labelAndValues;

	//bi-directional many-to-one association to Module
	@ManyToOne
	@JoinColumn(name="module_id", nullable=false)
	private Module module;

	public Tab() {
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

	public List<LabelAndValue> getLabelAndValues() {
		return this.labelAndValues;
	}

	public void setLabelAndValues(List<LabelAndValue> labelAndValues) {
		this.labelAndValues = labelAndValues;
	}

	public LabelAndValue addLabelAndValue(LabelAndValue labelAndValue) {
		getLabelAndValues().add(labelAndValue);
		labelAndValue.setTab(this);

		return labelAndValue;
	}

	public LabelAndValue removeLabelAndValue(LabelAndValue labelAndValue) {
		getLabelAndValues().remove(labelAndValue);
		labelAndValue.setTab(null);

		return labelAndValue;
	}

	public Module getModule() {
		return this.module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

}