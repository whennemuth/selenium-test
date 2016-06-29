package edu.bu.ist.apps.kualiautomation.entity;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.entity.util.CustomJsonSerializer;


/**
 * The persistent class for the label_and_value database table.
 * 
 */
@Entity
@Table(name="label_and_value")
@NamedQuery(name="LabelAndValue.findAll", query="SELECT l FROM LabelAndValue l")
public class LabelAndValue extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;

	@Column(nullable=false, length=45)
	private String label;

	@Column(nullable=false)
	private int sequence;

	@Column(nullable=false, length=1000)
	private String value;
	
	@Column(nullable=false, length=50)
	private String elementType;
	
	@Column(nullable=true, length=100)
	private String identifier;
	
	/**
	 * This is a hack. I'm adding this property to be included into the json object created
	 * from an instance of this class for convenience in angularjs 2-way binding UI manipulation.
	 */
	@Transient
	public String getChecked() {
		if(elementType == null)
			return "";
		if(value == null)
			return "";
		boolean checked = Boolean.valueOf(value);
		return String.valueOf(checked);
	}
	@Transient
	public void setChecked(String checked) {
		// Do nothing
	}

	
	//bi-directional many-to-one association to Tab
	@ManyToOne
	@JoinColumn(name="tab_id", nullable=false)
	private Tab tab;

	public LabelAndValue() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getSequence() {
		if(this.sequence == 0)
			this.sequence++;
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

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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