package com.react.demo.entity;

import com.react.demo.constant.Role;
import com.react.demo.dto.UserFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name="users")
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @Column(name="user_id")
    private String id;

    private String name;

    @Column(nullable = false)
    private String password;

    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public static User createUser(UserFormDto dto, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        String password = passwordEncoder.encode(dto.getPassword());
        user.setPassword(password);
        user.setAddress(dto.getAddress());
        user.setRole(Role.USER);
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.role.getKey()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.id;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
