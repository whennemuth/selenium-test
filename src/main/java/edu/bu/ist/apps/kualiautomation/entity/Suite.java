package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import javax.persistence.*;


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

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}