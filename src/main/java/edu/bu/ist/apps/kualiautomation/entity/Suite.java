package edu.bu.ist.apps.kualiautomation.entity;

import java.io.IOException;
import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.util.CustomJsonSerializer;

import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the suite database table.
 * 
 */
@Entity
@Table(name="suite")
@NamedQuery(name="Suite.findAll", query="SELECT s FROM Suite s")
public class Suite implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private int id;

	@Column(nullable=false, length=45)
	private String name;

	//bi-directional many-to-one association to Module
	@OneToMany(mappedBy="suite")
	private List<Module> modules = new ArrayList<Module>();

	//bi-directional many-to-one association to Cycle
	@ManyToOne
	@JoinColumn(name="cycle_id", nullable=false)
	private Cycle cycle;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;

	public Suite() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Module> getModules() {
		return this.modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	public Module addModule(Module module) {
		getModules().add(module);
		module.setSuite(this);

		return module;
	}

	public Module removeModule(Module module) {
		getModules().remove(module);
		module.setSuite(null);

		return module;
	}

	@JsonSerialize(using=CycleFieldSerializer.class)
	public Cycle getCycle() {
		return this.cycle;
	}

	public void setCycle(Cycle cycle) {
		this.cycle = cycle;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public static class CycleFieldSerializer extends JsonSerializer<Cycle> {
		@Override public void serialize(
				Cycle cycle, 
				JsonGenerator generator, 
				SerializerProvider provider) throws IOException, JsonProcessingException {
			
			(new CustomJsonSerializer<Cycle>()).serialize(cycle, generator, provider);
		}
	}

}