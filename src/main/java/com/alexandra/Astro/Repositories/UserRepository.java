package com.alexandra.Astro.Repositories;

import com.alexandra.Astro.Models.Role;
import com.alexandra.Astro.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findByRoles(Role role);
    List<User> findByTeacherId(Long teacherId);
    //User findByGuideId(Long guideId);
}