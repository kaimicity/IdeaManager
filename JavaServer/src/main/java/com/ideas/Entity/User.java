package com.ideas.Entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"}) 
@Entity(name="user")
public class User {
	@Id
	@GeneratedValue
	private int id;

	String username;
	
	String password;
	
	@OneToMany
	@JsonIgnore
	@JoinColumn(name = "userId")
	private List<Idea> myIdeas;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Idea> getMyIdeas() {
		return myIdeas;
	}

	public void setMyIdeas(List<Idea> myIdeas) {
		this.myIdeas = myIdeas;
	}
	
	
}
