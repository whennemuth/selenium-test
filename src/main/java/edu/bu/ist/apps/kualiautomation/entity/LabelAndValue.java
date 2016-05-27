package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import javax.persistence.*;

import edu.bu.ist.apps.kualiautomation.model.InputElement;


/**
 * The persistent class for the label_and_value database table.
 * 
 */
@Entity
@Table(name="label_and_value")
@NamedQuery(name="LabelAndValue.findAll", query="SELECT l FROM LabelAndValue l")
public class LabelAndValue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private int id;

	@Column(name="input_element", nullable=false, length=45)
	@Enumerated(EnumType.STRING)
	private InputElement inputElement;

	@Column(nullable=false)
	private int sequence;

	@Column(nullable=false, length=1000)
	private String value;

	//bi-directional many-to-one association to Tab
	@ManyToOne
	@JoinColumn(name="tab_id", nullable=false)
	private Tab tab;

	public LabelAndValue() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public InputElement getInputElement() {
		return this.inputElement;
	}

	public void setInputElement(InputElement inputElement) {
		this.inputElement = inputElement;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Tab getTab() {
		return this.tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

}