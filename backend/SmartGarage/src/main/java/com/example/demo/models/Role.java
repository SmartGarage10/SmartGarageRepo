package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"users"})
@ToString(exclude = "users")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private RoleType roleName;

    @JsonIgnore
    @OneToMany(mappedBy = "role")
    private Set<User> users;
    public enum RoleType{
        CLIENT,
        EMPLOYEE,
        ADMIN
    }
    // This method returns the role as a list of authorities
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.roleName.name()));
    }
}
