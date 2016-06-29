package edu.bu.ist.apps.kualiautomation.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
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
import javax.persistence.Transient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.entity.util.CustomJsonSerializer;


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

	@Column(nullable=false, length=45)
	private String name;

	@Column(nullable=false)
	private int sequence;
	
	@Transient
	private int repeat = 1;

	//bi-directional many-to-one association to Suite
	@ManyToOne
	@JoinColumn(name="suite_id", nullable=false)
	private Suite suite;

	//bi-directional many-to-one association to Tab
	@OneToMany(cascade={CascadeType.MERGE, CascadeType.REMOVE}, fetch=FetchType.EAGER, mappedBy="module")
	private Set<Tab> tabs = new HashSet<Tab>();

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

	public int getSequence() {
		if(this.sequence == 0)
			this.sequence++;
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	@JsonSerialize(using=SuiteFieldSerializer.class)
	public Suite getSuite() {
		return this.suite;
	}

	public void setSuite(Suite suite) {
		this.suite = suite;
	}

	public Set<Tab> getTabs() {
		TreeSet<Tab> sorted = new TreeSet<Tab>(new Comparator<Tab>() {
			@Override public int compare(Tab tab1, Tab tab2) {
				return tab1.getSequence() - tab2.getSequence();
			}});
		sorted.addAll(tabs);
		return sorted;
	}

	public void setTabs(Set<Tab> tabs) {
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