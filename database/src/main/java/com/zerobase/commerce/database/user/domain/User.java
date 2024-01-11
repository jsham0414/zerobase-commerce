package com.zerobase.commerce.database.user.domain;

import com.zerobase.commerce.database.security.Encrypt;
import com.zerobase.commerce.database.user.constant.AuthorityStatus;
import com.zerobase.commerce.database.user.constant.converter.AuthorityStatusConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
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
@DynamicUpdate
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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) return false;
        final User other = (User) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$roles = this.getRoles();
        final Object other$roles = other.getRoles();
        if (this$roles == null ? other$roles != null : !this$roles.equals(other$roles)) return false;
        final Object this$registeredAt = this.getRegisteredAt();
        final Object other$registeredAt = other.getRegisteredAt();
        if (this$registeredAt == null ? other$registeredAt != null : !this$registeredAt.equals(other$registeredAt))
            return false;
        final Object this$updatedAt = this.getUpdatedAt();
        final Object other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !this$updatedAt.equals(other$updatedAt)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof User;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $roles = this.getRoles();
        result = result * PRIME + ($roles == null ? 43 : $roles.hashCode());
        final Object $registeredAt = this.getRegisteredAt();
        result = result * PRIME + ($registeredAt == null ? 43 : $registeredAt.hashCode());
        final Object $updatedAt = this.getUpdatedAt();
        result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        return result;
    }
}
