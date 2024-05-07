package com.jwt.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jwt.demo.entities.UserInfo;

@Repository
public interface UserRepository  extends JpaRepository<UserInfo, Long> {

   public UserInfo findByUsername(String username);
   UserInfo findFirstById(Long id);

}
