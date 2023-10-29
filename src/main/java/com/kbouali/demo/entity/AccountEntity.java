package com.kbouali.demo.entity;

import com.kbouali.demo.util.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountEntity implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    private String firstname;
    @NonNull
    private String lastname;
    @NonNull
    @Column(length = 10)
    private String phoneNumber;
    @NonNull
    @Column(unique = true, length = 8)
    private String cin;
    @NonNull
    private String password;
    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "account")
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public @NonNull String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return cin;
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
