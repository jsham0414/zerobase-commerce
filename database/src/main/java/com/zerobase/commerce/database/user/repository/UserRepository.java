package com.zerobase.commerce.database.user.repository;

import com.zerobase.commerce.database.user.domain.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsById(@NonNull String id);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdAndDecryptPassword(@Param("id") String id);
}
