package com.ideas.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ideas.Entity.Idea;
import com.ideas.Entity.User;
import com.ideas.Rep.UserRep;
import com.ideas.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired UserRep userRep ;
	
	@Override
	public User get(int id) {
		return userRep.getOne(id) ;
	}

	@Override
	public List<User> getByUsername(String username) {
		return userRep.findByUsername(username);
	}

	@Override
	public User add(User user) {
		return userRep.save(user);
	}

	@Override
	public List<User> getAll() {
		return userRep.findAll();
	}

	@Override
	public List<Idea> getMyIdea(int id) {
		List<Idea> res = userRep.getOne(id).getMyIdeas();
		Collections.sort(res, new Comparator<Idea>() {
			public int compare(Idea i1, Idea i2) {
				return i2.getModDate().compareTo(i1.getModDate());
			}
		});
		return res;
	}
}
