package com.ideas.Rep;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ideas.Entity.Idea;

@Repository
public interface IdeaRep extends JpaRepository<Idea, Integer>{
	
	@Query("select distinct i.ideaIndex  from idea i where i.userId=?1 order by i.ideaIndex" )
	String[] getDistinctIndex(int uid);
	
	List<Idea> findByUserIdAndIdeaIndex(int userId, String ideaIndex);
}
