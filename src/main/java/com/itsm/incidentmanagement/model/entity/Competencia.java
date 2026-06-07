package com.itsm.incidentmanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "competencia")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Competencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String nome;

    @ManyToMany(mappedBy = "competencias")
    private Set<Tecnico> tecnicos = new HashSet<>();
}