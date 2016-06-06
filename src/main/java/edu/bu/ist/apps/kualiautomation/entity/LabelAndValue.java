package edu.bu.ist.apps.kualiautomation.entity;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.util.CustomJsonSerializer;


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

	@Column(nullable=false, length=45)
	private String label;

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

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	@JsonSerialize(using=TabFieldSerializer.class)
	public Tab getTab() {
		return this.tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}
	
	public static class TabFieldSerializer extends JsonSerializer<Tab> {
		@Override public void serialize(
				Tab tab, 
				JsonGenerator generator, 
				SerializerProvider provider) throws IOException, JsonProcessingException {
			
			(new CustomJsonSerializer<Tab>()).serialize(tab, generator, provider);
		}
	}

}