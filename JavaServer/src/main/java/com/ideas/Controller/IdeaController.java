package com.ideas.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ideas.Entity.Bean;
import com.ideas.Entity.Idea;
import com.ideas.service.IdeaService;

@RestController
@RequestMapping(value="/idea")
public class IdeaController {
	
	@Autowired IdeaService ideaService;
	
	@RequestMapping(value="/get/{id}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Idea get(@PathVariable int id) {
		return ideaService.get(id);
	}
	
	@RequestMapping(value="/add/{uId}", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Idea add(@PathVariable int uId, @RequestBody Idea idea) {
		idea.setUserId(uId);
		idea.setModDate(new Date());
		return ideaService.add(idea);
	}
	
	@RequestMapping(value="/modify/{id}", method=RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Idea modify(@PathVariable int id, @RequestBody Idea idea) {
		idea.setId(id);
		idea.setModDate(new Date());
		idea.setLinkedIdeas(ideaService.get(id).getLinkedIdeas());
		idea.setLinkingIdeas(ideaService.get(id).getLinkingIdeas());
		return ideaService.modify(idea);
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public String delete(@PathVariable int id) {
		ideaService.delete(id);
		return "DONE";
	}
	
	@RequestMapping(value="/indexes/{uid}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Map<String,Object> getAllIndex(@PathVariable int uid) {
		Map<String,Object> res = new HashMap<String,Object>();
		String[] indexes = ideaService.getAllIndex(uid);
		Map<String, List<Idea>> indexIdea = new HashMap<String, List<Idea>>();
		for(int i = 0; i< indexes.length; i++) {
			indexIdea.put(indexes[i], ideaService.getByUidAndIndex(uid, indexes[i]));
		}
		res.put("indexes", indexes);
		res.put("ideas", indexIdea);
		return res;
	}
	
	@RequestMapping(value="/deleteList", method=RequestMethod.POST )
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Map<String,String> deleteList(@RequestBody Bean list) {
		for(Integer i:list.getList()) {
			ideaService.delete(i);
		}
//		ideaService.deleteAll((List<Idea>)(list.get("list")));
		Map<String,String> res = new HashMap<>();
		res.put("result", "DONE");
		return res;
	}
	
	@RequestMapping(value="/{id}/link", method=RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Idea link(@PathVariable int id,@RequestBody Bean links) {
		Idea idea = ideaService.get(id);
		List<Integer> li = links.getList();
		List<Idea> mat = new ArrayList<>();
		for(int i:li) {
			Idea ii = ideaService.get(i);
				mat.add(ii);
		}
		idea.setLinkedIdeas(mat);
		idea.setLinkingIdeas(new ArrayList<Idea>());
		ideaService.modify(idea);
		return idea;
	}
	

	@RequestMapping(value="/{id}/link", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody List<Idea> getLinked(@PathVariable int id) {
		return ideaService.getLinkedIdea(id);
	}
	
	@RequestMapping(value="/{id}/link/{id2}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody String link(@PathVariable int id,@PathVariable int id2) {
		Idea idea = ideaService.get(id);
		Idea idea2 = ideaService.get(id2);
		if(!idea.getLinkingIdeas().contains(idea2) && !idea.getLinkedIdeas().contains(idea2)) {
			List<Idea> mat = idea.getLinkedIdeas();
			mat.add(idea2);
			idea.setLinkedIdeas(mat);
			ideaService.modify(idea);
			return "Successfully linked";
		} else {
			return "Has been linked before";
		}
	}
}
