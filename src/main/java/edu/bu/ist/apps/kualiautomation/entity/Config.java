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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * The persistent class for the config database table.
 * 
 */
@Entity
@Table(name="config")
@NamedQueries({
	@NamedQuery(name="Config.findAll", query="SELECT c FROM Config c"),
	@NamedQuery(name="Config.findByUserId", query="SELECT c FROM Config c where c.user.id = :userid")
})
// NOTE: Cannot use this annotation to handle the bi-directional relationship infinite loop issue because it serializes foreign key objects
// by their primary key values as primitives and not as a field of a containing object. This causes issues with JPA merges and persists
// that expect objects where it finds primitives. There may be a way to compensate for this, but I don't have time.
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Config.class) // Avoids infinite loop in bidirectional joins
public class Config extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;

	@Column(nullable=false)
	private boolean headless;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;

	//One of the environments "owned" by this config - the currently selected environment
	@OneToOne(cascade={CascadeType.MERGE}, fetch=FetchType.EAGER, mappedBy="configWhoIamCurrentFor")
	ConfigEnvironment currentEnvironment;
	
	//bi-directional many-to-one association to configEnvironment (all the environments "owned" by this config.
	@OneToMany(cascade=CascadeType.MERGE, fetch=FetchType.EAGER, mappedBy="parentConfig")
	private Set<ConfigEnvironment> configEnvironments = new LinkedHashSet<ConfigEnvironment>();

	//bi-directional many-to-one association to ConfigModule
	@OneToMany(cascade=CascadeType.MERGE, fetch=FetchType.EAGER, mappedBy="config")
	private Set<ConfigModule> configModules = new LinkedHashSet<ConfigModule>();

	public Config() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean getHeadless() {
		return this.headless;
	}

	public void setHeadless(boolean headless) {
		this.headless = headless;
	}

	@JsonSerialize(using=UserFieldSerializer.class)
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<ConfigEnvironment> getConfigEnvironments() {
		return this.configEnvironments;
	}

	public void setConfigEnvironments(Set<ConfigEnvironment> configEnvironments) {
		this.configEnvironments = configEnvironments;
	}

	public ConfigEnvironment addConfigEnvironment(ConfigEnvironment configEnvironment) {
		getConfigEnvironments().add(configEnvironment);
		configEnvironment.setParentConfig(this);

		return configEnvironment;
	}

	public ConfigEnvironment removeConfigEnvironment(ConfigEnvironment configEnvironment) {
		getConfigEnvironments().remove(configEnvironment);
		configEnvironment.setParentConfig(null);

		return configEnvironment;
	}
	
	public ConfigEnvironment getCurrentEnvironment() {
		return currentEnvironment;
	}

	public void setCurrentEnvironment(ConfigEnvironment currentEnvironment) {
		this.currentEnvironment = currentEnvironment;
	}
	public Set<ConfigModule> getConfigModules() {
		return this.configModules;
	}

	public void setConfigModules(Set<ConfigModule> configModules) {
		this.configModules = configModules;
	}

	public ConfigModule addConfigModule(ConfigModule configModule) {
		getConfigModules().add(configModule);
		configModule.setConfig(this);

		return configModule;
	}

	public ConfigModule removeConfigModule(ConfigModule configModule) {
		getConfigModules().remove(configModule);
		configModule.setConfig(null);

		return configModule;
	}

	/**
	 * This CustomJsonSerializer would serialize the user field with its id only to avoid the familiar
	 * bi-directional relationship infinite loop issue. However, other fields that are not involved in
	 * bi-direction are also skipped. This serizalizer includes those field (firstName, lastName, etc.).
	 * 
	 * @author Warren
	 *
	 */
	public static class UserFieldSerializer extends JsonSerializer<User> {

		@Override public void serialize(
				User user, 
				JsonGenerator generator, 
				SerializerProvider provider) throws IOException, JsonProcessingException {
			
			generator.writeStartObject();
			generator.writeNumberField("id", user.getId());
			generator.writeStringField("firstName", user.getFirstName());
			generator.writeStringField("lastName", user.getLastName());
			
			generator.writeArrayFieldStart("cycles");
			for(Cycle c : user.getCycles()) {
				generator.writeObject(c);
			}			
			generator.writeEndArray();
			
			generator.writeArrayFieldStart("configs");
			for(Config c : user.getConfigs()) {
				generator.writeStartObject();
				generator.writeNumberField("id", c.getId());
				generator.writeBooleanField("transitory", true);
				generator.writeEndObject();
			}
			generator.writeEndArray();
			
			generator.writeEndObject();

			// (new CustomJsonSerializer<User>()).serialize(user, generator, provider);
		}
	}

}