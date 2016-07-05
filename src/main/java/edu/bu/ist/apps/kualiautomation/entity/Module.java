package edu.bu.ist.apps.kualiautomation.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.entity.util.CustomJsonSerializer;
import edu.bu.ist.apps.kualiautomation.services.automate.ModuleAction;


/**
 * The persistent class for the module database table.
 * 
 */
@Entity
@Table(name="module")
@NamedQuery(name="Module.findAll", query="SELECT m FROM Module m")
public class Module extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;

	@Column(nullable=true, length=45)
	private String name;

	@Column(nullable=false)
	private int sequence;
	
	@Transient
	private int repeat = 1;
	
	@Transient 
	private String actionType;
	
	@Column(nullable=true, length=45) 
	private String customName;

	//bi-directional many-to-one association to Suite
	@ManyToOne
	@JoinColumn(name="suite_id", nullable=false)
	private Suite suite;

	//bi-directional many-to-one association to Tab
	@OrderBy("sequence ASC")
	@OneToMany(cascade={CascadeType.MERGE, CascadeType.REMOVE}, fetch=FetchType.EAGER, mappedBy="module")
	private Set<Tab> tabs = new LinkedHashSet<Tab>();

	public Module() {
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

	public boolean isBlank() {
		return getName() == null || getName().isEmpty();
	}
	
	public int getSequence() {
		if(this.sequence == 0)
			this.sequence++;
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getActionType() {
		if(actionType != null)
			return actionType;
		if(id == null)
			return ModuleAction.MODULE.name();
		if(name == null && customName == null)
			return ModuleAction.NONE.name();
		if(customName == null)
			return ModuleAction.MODULE.name();
		else
			return ModuleAction.CUSTOM.name();
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	@JsonSerialize(using=SuiteFieldSerializer.class)
	public Suite getSuite() {
		return this.suite;
	}

	public void setSuite(Suite suite) {
		this.suite = suite;
	}

	public Set<Tab> getTabs() {
		return tabs;
	}

	public void setTabs(Set<Tab> tabs) {
		this.tabs.clear();
		this.tabs.addAll(tabs);
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
	
	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}


	public static class SuiteFieldSerializer extends JsonSerializer<Suite> {
		@Override public void serialize(
				Suite suite, 
				JsonGenerator generator, 
				SerializerProvider provider) throws IOException, JsonProcessingException {
			
			(new CustomJsonSerializer<Suite>()).serialize(suite, generator, provider);
		}
	}

}