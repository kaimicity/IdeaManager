package com.ideas.service;
import java.util.List;

import com.ideas.Entity.Idea;
import com.ideas.Entity.User;

public interface UserService {
	public User get(int id) ;
	public List<User> getByUsername(String username) ;
	public User add(User user);
	public List<User> getAll();
	public List<Idea> getMyIdea(int id);
}
