package edu.bu.ist.apps.kualiautomation.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.entity.util.CustomJsonSerializer;


/**
 * The persistent class for the tab database table.
 * 
 */
@Entity
@Table(name="tab")
@NamedQuery(name="Tab.findAll", query="SELECT t FROM Tab t")
public class Tab extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;

	@Column(nullable=false, length=45)
	private String name;

	@Column(nullable=false)
	private int sequence;

	//bi-directional many-to-one association to LabelAndValue
	@OneToMany(cascade={CascadeType.MERGE, CascadeType.REMOVE}, fetch=FetchType.EAGER, mappedBy="tab")
	private Set<LabelAndValue> labelAndValues = new TreeSet<LabelAndValue>(new Comparator<LabelAndValue>() {
		@Override public int compare(LabelAndValue lv1, LabelAndValue lv2) {
			return lv1.getSequence() - lv2.getSequence();
		}});

	//bi-directional many-to-one association to Module
	@ManyToOne
	@JoinColumn(name="module_id", nullable=false)
	private Module module;

	public Tab() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
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

	public Set<LabelAndValue> getLabelAndValues() {
		return this.labelAndValues;
	}

	public void setLabelAndValues(Set<LabelAndValue> labelAndValues) {
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

	@JsonSerialize(using=ModuleFieldSerializer.class)
	public Module getModule() {
		return this.module;
	}

	public void setModule(Module module) {
		this.module = module;
	}
	
	public static class ModuleFieldSerializer extends JsonSerializer<Module> {
		@Override public void serialize(
				Module module, 
				JsonGenerator generator, 
				SerializerProvider provider) throws IOException, JsonProcessingException {
			
			(new CustomJsonSerializer<Module>()).serialize(module, generator, provider);
		}
	}

}