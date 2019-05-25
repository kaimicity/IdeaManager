package com.ideas.Rep;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ideas.Entity.User;
@Repository
public interface UserRep extends JpaRepository<User, Integer>{
	List<User> findByUsername(String username) ;
}
