package com.app.carrent.repository;

import com.app.carrent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryInterface extends JpaRepository<User, Long> {
}
