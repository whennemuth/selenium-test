package edu.bu.ist.apps.kualiautomation.entity;

import java.io.IOException;
import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.entity.Config.UserFieldSerializer;
import edu.bu.ist.apps.kualiautomation.util.CustomJsonSerializer;

import java.util.Date;


/**
 * The persistent class for the config_tab database table.
 * 
 */
@Entity
@Table(name="config_tab")
@NamedQuery(name="ConfigTab.findAll", query="SELECT c FROM ConfigTab c")
public class ConfigTab implements Serializable {
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

	//bi-directional many-to-one association to ConfigModule
	@ManyToOne
	@JoinColumn(name="config_module_id", nullable=false)
	private ConfigModule configModule;
	
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

	public ConfigTab() {
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

	@JsonSerialize(using=ConfigModuleFieldSerializer.class)
	public ConfigModule getConfigModule() {
		return this.configModule;
	}

	public void setConfigModule(ConfigModule configModule) {
		this.configModule = configModule;
	}
	
	public static class ConfigModuleFieldSerializer extends JsonSerializer<ConfigModule> {
		@Override public void serialize(
				ConfigModule configModule, 
				JsonGenerator generator, 
				SerializerProvider provider) throws IOException, JsonProcessingException {
			
			(new CustomJsonSerializer<ConfigModule>()).serialize(configModule, generator, provider);
		}
	}

}