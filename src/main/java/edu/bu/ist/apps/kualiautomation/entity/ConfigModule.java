package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.entity.ConfigEnvironment.ConfigFieldSerializer;


/**
 * The persistent class for the config_module database table.
 * 
 */
@Entity
@Table(name="config_module")
@NamedQuery(name="ConfigModule.findAll", query="SELECT c FROM ConfigModule c")
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id", scope=ConfigModule.class) // Avoids infinite loop in bidirectional joins
public class ConfigModule extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date", nullable=false)
	private Date createdDate;

	@Column(nullable=false)
	private Byte include;

	@Column(nullable=false, length=45)
	private String label;

	/**
	 * NOTE: This is a class whose counterpart on the other side of the @OneToMany relationship itself also
	 * has an eagerly fetched collection. For some reason, JPA imposes a restriction in filling up the "bag"
	 * to one level of eager fetching - nested fetching is restricted unless the bag is based on a Set collection, not a list.
	 * If this collection were a list you would see a MultipleBagFetchException thrown when fetching is triggered.
	 */
	//bi-directional many-to-one association to ConfigTab
	@OneToMany(cascade={CascadeType.MERGE, CascadeType.REMOVE}, fetch=FetchType.EAGER, mappedBy="configModule")
	private Set<ConfigTab> configTabs = new LinkedHashSet<ConfigTab>();

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

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
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

	public Set<ConfigTab> getConfigTabs() {
		return this.configTabs;
	}

	public void setConfigTabs(Set<ConfigTab> configTabs) {
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