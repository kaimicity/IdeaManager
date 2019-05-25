package com.ideas.service;

import java.util.List;

import com.ideas.Entity.Idea;

public interface IdeaService {
	public Idea get(int id) ;
	public Idea add(Idea idea);
	public Idea modify(Idea idea);
	public void delete(int id);
	public void deleteAll(List<Idea> list);
	public String[] getAllIndex(int uid);
	public List<Idea> getByUidAndIndex(int uid, String index);
	public List<Idea> getLinkedIdea(int id);
}
