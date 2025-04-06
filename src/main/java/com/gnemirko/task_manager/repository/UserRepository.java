package com.gnemirko.task_manager.repository;

import com.gnemirko.task_manager.entity.User;
import com.gnemirko.task_manager.enums.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  List<User> findByRole(Role role);
}
