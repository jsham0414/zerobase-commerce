package com.zerobase.commerce.database.domain;

import com.zerobase.commerce.database.constant.AuthorityStatus;
import com.zerobase.commerce.database.constant.converter.AuthorityStatusConverter;
import com.zerobase.commerce.database.security.Encrypt;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User implements UserDetails {
    @Id
    private String id;

    @Column(name = "password")
    @Encrypt
    private String password;

    @Column(name = "roles")
    @Convert(converter = AuthorityStatusConverter.class)
    private Set<AuthorityStatus> roles = new HashSet<>();

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(AuthorityStatus::toString).map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
