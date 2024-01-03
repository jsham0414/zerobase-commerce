package com.zerobase.commerce.database.repository;

import com.zerobase.commerce.database.domain.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsById(@NonNull String id);
}
