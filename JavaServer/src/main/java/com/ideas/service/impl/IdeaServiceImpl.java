package com.ideas.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ideas.Entity.Idea;
import com.ideas.Rep.IdeaRep;
import com.ideas.service.IdeaService;

@Service
public class IdeaServiceImpl implements IdeaService {

	@Autowired IdeaRep ideaRep;
	
	@Override
	public Idea get(int id) {
		return ideaRep.getOne(id);
	}

	@Override
	public Idea add(Idea idea) {
		return ideaRep.save(idea);
	}

	@Override
	public Idea modify(Idea idea) {
		return ideaRep.save(idea);
	}

	@Override
	public void delete(int id) {
		ideaRep.deleteById(id);
	}

	@Override
	public String[] getAllIndex(int uid) {
		return ideaRep.getDistinctIndex(uid);
	}

	@Override
	public List<Idea> getByUidAndIndex(int uid, String index) {
		List<Idea> res = ideaRep.findByUserIdAndIdeaIndex(uid, index);
		Collections.sort(res, new Comparator<Idea>() {
			public int compare(Idea i1, Idea i2) {
				return i2.getModDate().compareTo(i1.getModDate());
			}
		});
		return res;
	}


	/* (non-Javadoc)
	 * @see com.ideas.service.IdeaService#getLinkedIdea(int)
	 */
	@Override
	public List<Idea> getLinkedIdea(int id) {
		List<Idea> res = get(id).getLinkedIdeas();
		res.addAll(get(id).getLinkingIdeas());
		return res;
	}

	/* (non-Javadoc)
	 * @see com.ideas.service.IdeaService#deleteAll()
	 */
	@Override
	public void deleteAll(List<Idea> list) {
		for(Idea i : list) {
			ideaRep.deleteById(i.getId());
		}
	}

}
