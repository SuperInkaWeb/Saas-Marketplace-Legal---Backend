package com.saas.legit.module.identity.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int idRol;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String nameRol;

    @Column(name = "description", length = 255)
    private String descriptionRol;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    public Role() {
    }

    public Role(String name, String description) {
        this.nameRol = name;
        this.descriptionRol = description;
    }
}
