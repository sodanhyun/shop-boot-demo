package com.react.demo.entity;

import com.react.demo.constant.Role;
import com.react.demo.constant.SocialType;
import com.react.demo.dto.UserFormDto;
import jakarta.persistence.*;
import lombok.*;
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
@NoArgsConstructor
@ToString
public class User implements UserDetails {

    @Id
    @Column(name="user_id")
    private String id;

    @Column(nullable = false)
    private String email;

    private String nickname;

    private String name;

    private String password;

    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType;

    @Builder
    public User(String email, String password, String nickname, Role role, SocialType socialType) {
        this.id = email + "_" + socialType.getKey();
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.socialType = socialType;
    }

    public User update(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public static User createUser(UserFormDto dto, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setId(dto.getEmail() + "_" + SocialType.OWN.getKey());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        String password = passwordEncoder.encode(dto.getPassword());
        user.setPassword(password);
        user.setAddress(dto.getAddress());
        user.setRole(Role.ADMIN);
        user.setSocialType(SocialType.OWN);
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
