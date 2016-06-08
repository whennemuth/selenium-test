package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.entity.ConfigEnvironment.ConfigFieldSerializer;


/**
 * The persistent class for the config_module database table.
 * 
 */
@Entity
@Table(name="config_module")
@NamedQuery(name="ConfigModule.findAll", query="SELECT c FROM ConfigModule c")
public class ConfigModule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date", nullable=false)
	private Date createdDate;

	@Column(nullable=false)
	private Byte include;

	@Column(nullable=false, length=45)
	private String label;

	//bi-directional many-to-one association to ConfigTab
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="configModule")
	private List<ConfigTab> configTabs = new ArrayList<ConfigTab>();

	//bi-directional many-to-one association to Config
	@ManyToOne
	@JoinColumn(name="config_id", nullable=false)
	private Config config;
	
	/**
	 * JPA lifecycle callback methods
	 */
	@PrePersist
	public void prePersist() {
		if(include == null) {
			include = new Byte((byte)1);
		}
		createdDate = new Date(System.currentTimeMillis());
	}
	
	public ConfigModule() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public byte getInclude() {
		if(include == null) {
			return new Byte((byte)0);
		}
		return this.include;
	}

	public void setInclude(byte include) {
		this.include = new Byte(include);
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<ConfigTab> getConfigTabs() {
		return this.configTabs;
	}

	public void setConfigTabs(List<ConfigTab> configTabs) {
		this.configTabs = configTabs;
	}

	public ConfigTab addConfigTab(ConfigTab configTab) {
		getConfigTabs().add(configTab);
		configTab.setConfigModule(this);

		return configTab;
	}

	public ConfigTab removeConfigTab(ConfigTab configTab) {
		getConfigTabs().remove(configTab);
		configTab.setConfigModule(null);

		return configTab;
	}

	@JsonSerialize(using=ConfigFieldSerializer.class)
	public Config getConfig() {
		return this.config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * This is a hack. I'm adding this property to be included into the json object created
	 * from an instance of this class for convenience in angularjs 2-way binding UI manipulation.
	 */
	@Transient
	private boolean checked;
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
}