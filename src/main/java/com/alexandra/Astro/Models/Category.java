package com.alexandra.Astro.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Observation> observations;

    public Category(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}