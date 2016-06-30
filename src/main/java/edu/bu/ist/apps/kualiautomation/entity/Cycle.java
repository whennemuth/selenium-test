package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import java.util.Comparator;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.entity.Config.UserFieldSerializer;


/**
 * The persistent class for the cycle database table.
 * 
 */
@Entity
@Table(name="cycle")
@NamedQueries({
	@NamedQuery(name="Cycle.findAll", query="SELECT c FROM Cycle c"),
	@NamedQuery(name="Cycle.findByUserId", query="SELECT c FROM Cycle c WHERE c.user.id = :userid")
})
public class Cycle extends AbstractEntity implements Serializable {
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
	private String kerberosUsername;
	
	@Transient
	private String kerberosPassword;

	//bi-directional many-to-one association to Suite
	@OneToMany(cascade={CascadeType.MERGE, CascadeType.REMOVE}, fetch=FetchType.EAGER, mappedBy="cycle")
	private Set<Suite> suites = new TreeSet<Suite>(new Comparator<Suite>() {
		@Override public int compare(Suite suite1, Suite suite2) {
			return suite1.getSequence() - suite2.getSequence();
		}});
	
	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;

	public Cycle() {
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

	public String getKerberosUsername() {
		return kerberosUsername;
	}

	public void setKerberosUsername(String kerberosUsername) {
		this.kerberosUsername = kerberosUsername;
	}

	public String getKerberosPassword() {
		return kerberosPassword;
	}

	public void setKerberosPassword(String kerberosPassword) {
		this.kerberosPassword = kerberosPassword;
	}

	@JsonSerialize(using=UserFieldSerializer.class)
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<Suite> getSuites() {
		return suites;
	}

	public void setSuites(Set<Suite> suites) {
		this.suites.clear();
		this.suites.addAll(suites);
	}

	public Suite addSuite(Suite suite) {
		getSuites().add(suite);
		suite.setCycle(this);

		return suite;
	}

	public Suite removeSuite(Suite suite) {
		getSuites().remove(suite);
		suite.setCycle(null);

		return suite;
	}

}