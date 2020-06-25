package com.app.carrent.repository;

import com.app.carrent.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepositoryInterface extends JpaRepository<Token,Long> {
    Optional<Token> findByValue(String value);
}
