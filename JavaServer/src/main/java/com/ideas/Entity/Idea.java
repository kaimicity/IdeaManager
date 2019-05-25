package com.ideas.Entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"}) 
@Entity(name="idea")
public class Idea {
	@Id
	@GeneratedValue
	private int id;
	String ideaIndex;
	String topic;
	String content;
	Date modDate;
	int userId;
	

	@JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "idea_idea", joinColumns = {
            @JoinColumn(name = "idea1_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "idea2_id", referencedColumnName = "id")})
    private List<Idea> linkedIdeas;
	

	@JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "idea_idea", joinColumns = {
            @JoinColumn(name = "idea2_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "idea1_id", referencedColumnName = "id")})
    private List<Idea> linkingIdeas;
	
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getIdeaIndex() {
		return ideaIndex;
	}
	public void setIdeaIndex(String ideaIndex) {
		this.ideaIndex = ideaIndex;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getModDate() {
		return modDate;
	}
	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	/**
	 * @return the linkedIdeas
	 */
	public List<Idea> getLinkedIdeas() {
		return linkedIdeas;
	}
	/**
	 * @param linkedIdeas the linkedIdeas to set
	 */
	public void setLinkedIdeas(List<Idea> linkedIdeas) {
		this.linkedIdeas = linkedIdeas;
	}
	public List<Idea> getLinkingIdeas() {
		return linkingIdeas;
	}
	public void setLinkingIdeas(List<Idea> linkingIdeas) {
		this.linkingIdeas = linkingIdeas;
	}
	
	
}
