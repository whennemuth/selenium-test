package edu.bu.ist.apps.kualiautomation.entity;

import java.io.IOException;
import java.io.Serializable;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.entity.util.CustomJsonSerializer;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;


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

	@Column(length=45)
	private String label;

	@Column(nullable=false)
	private int sequence;

	@Column(length=1000)
	private String value;
	
	@Column(name="element_type", nullable=false, length=45)
	private String elementType;
	
	@Column(nullable=true, length=100)
	private String identifier;

	@Column(nullable=false)
	private byte navigate;
	
	// uni-directional one-to-one association to ConfigShortcut (ConfigShortcut cannot "see" LabelAndValue). 
	@OneToOne(cascade={CascadeType.MERGE, CascadeType.REMOVE}, fetch=FetchType.EAGER)
	@JoinColumn(name="shortcut_id", nullable=true)
	private ConfigShortcut configShortcut;
	
	/**
	 * This is a hack. I'm adding this property to be included into the json object created
	 * from an instance of this class for convenience in angularjs 2-way binding UI manipulation.
	 */
	@Transient
	public boolean isChecked() {
		return false;
	}
	@Transient
	public void setChecked(boolean checked) {
		/* do nothing */
	}
	
	@Transient
	public boolean isBooleanValue() {
		if(elementType == null)
			return false;
		if(value == null)
			return false;
		return value.trim().equalsIgnoreCase("true");
	}
	@Transient
	public void setBooleanValue(boolean bool) {
		/* do nothing */
	}

	
	//bi-directional many-to-one association to Suite
	@ManyToOne
	@JoinColumn(name="suite_id", nullable=false)
	private Suite suite;

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
	
	@JsonIgnore @Transient
	public ElementType getElementTypeEnum() {
		try {
			return ElementType.valueOf(elementType);
		} 
		catch (RuntimeException e) {
			return null;
		}
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

//	@JsonSerialize(using=ShortcutFieldSerializer.class)
	public ConfigShortcut getConfigShortcut() {
		return configShortcut;
	}
	
	public void setConfigShortcut(ConfigShortcut shortcut) {
		this.configShortcut = shortcut;
	}
	
	@JsonIgnore
	public byte getNavigate() {
		return navigate;
	}
	public boolean isNavigates() {
		boolean retval = (navigate != 0);
		if(configShortcut != null) {
			retval |= configShortcut.isNavigates();
		}
		
		return retval;
	}

	@JsonIgnore
	public void setNavigate(byte navigate) {
		this.navigate = navigate;
	}
	public void setNavigates(boolean navigates) {
		this.navigate = (byte) (navigates ? 1 : 0);
	}
	
	@JsonSerialize(using=SuiteFieldSerializer.class)
	public Suite getSuite() {
		return this.suite;
	}

	public void setSuite(Suite suite) {
		this.suite = suite;
	}
	
	public static class SuiteFieldSerializer extends JsonSerializer<Suite> {
		@Override public void serialize(
				Suite suite, 
				JsonGenerator generator, 
				SerializerProvider provider) throws IOException, JsonProcessingException {
			
			(new CustomJsonSerializer<Suite>()).serialize(suite, generator, provider);
		}
	}
	
//	public static class ShortcutFieldSerializer extends JsonSerializer<ConfigShortcut> {
//		@Override public void serialize(
//				ConfigShortcut shortcut, 
//				JsonGenerator generator, 
//				SerializerProvider provider) throws IOException, JsonProcessingException {
//			
//			(new CustomJsonSerializer<ConfigShortcut>()).serialize(shortcut, generator, provider);
//		}
//	}

}