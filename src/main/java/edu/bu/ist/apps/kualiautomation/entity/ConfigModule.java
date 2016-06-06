package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
	private byte include;

	@Column(nullable=false, length=45)
	private String label;

	//bi-directional many-to-one association to ConfigTab
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="configModule")
	private List<ConfigTab> configTabs = new ArrayList<ConfigTab>();

	//bi-directional many-to-one association to Config
	@ManyToOne
	@JoinColumn(name="config_id", nullable=false)
	private Config config;

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
		return this.include;
	}

	public void setInclude(byte include) {
		this.include = include;
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

	public Config getConfig() {
		return this.config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}