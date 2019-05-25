package com.ideas.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ideas.Entity.Idea;
import com.ideas.Entity.User;
import com.ideas.service.UserService;


@RestController
@RequestMapping(value="/user")
public class UserController {
	
	@Autowired UserService userService ;
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody User get(@PathVariable int id) {
		return userService.get(id) ;
	}
	
	@RequestMapping(value="/login/{username}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody String login(@PathVariable String username, @RequestParam(value="password") String password) {
		List<User> res = userService.getByUsername(username) ;
		if(res.size() == 0)
			return "NO_USER" ;
		else {
			User u = res.get(0) ;
			if(u.getPassword().equals(password))
				return "" + u.getId() ;
			else
				return "DENIED" ;
		}
	}
	
	@RequestMapping(value="/check/{username}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody boolean hasUser(@PathVariable String username) {
		List<User> res = userService.getByUsername(username) ;
		if(res.size() == 0)
			return false ;
		else {
			return true ;
		}
	}
	

	@RequestMapping(value="/signup", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody User signUp(@RequestBody User user) {
		return userService.add(user);
	}
	
	@RequestMapping(value="/{id}/ideas", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody List<Idea> getMyIdeas(@PathVariable int id) {
		return userService.getMyIdea(id);
	}
	
	@RequestMapping(value="/findAll", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody List<User> getAll() {
		return userService.getAll();
	}
}
