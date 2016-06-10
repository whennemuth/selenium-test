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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.util.CustomJsonSerializer;


/**
 * The persistent class for the config_server database table.
 * 
 */
@Entity
@Table(name="config_environment")
@NamedQuery(name="ConfigEnvironment.findAll", query="SELECT c FROM ConfigEnvironment c")
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id", scope=ConfigEnvironment.class) // Avoids infinite loop in bidirectional joins
public class ConfigEnvironment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static enum Defaults {
		TEST("https://kuali-test.bu.edu/kc/portal.do"),
		STAGING("https://kuali-stg.bu.edu/kc/portal.do");
		private String url;
		private Defaults(String url) {
			this.url = url;
		}
		public String getUrl() {
			return url;
		}
	};

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;

	@Column(nullable=false, length=45)
	private String name;

	@Column(nullable=false, length=1000)
	private String url;

	//bi-directional many-to-one association to Config
	@ManyToOne
	@JoinColumn(name="config_id", nullable=false)
	private Config parentConfig;
	
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="config_id", nullable=false, insertable=false, updatable=false)
	private Config configWhoIamCurrentFor;

	public ConfigEnvironment() {
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

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@JsonSerialize(using=ConfigFieldSerializer.class)
	public Config getParentConfig() {
		return this.parentConfig;
	}

	public void setParentConfig(Config config) {
		this.parentConfig = config;
	}
	
	public static class ConfigFieldSerializer extends JsonSerializer<Config> {
		@Override public void serialize(
				Config config, 
				JsonGenerator generator, 
				SerializerProvider provider) throws IOException, JsonProcessingException {
			
			(new CustomJsonSerializer<Config>()).serialize(config, generator, provider);
		}
	}

}