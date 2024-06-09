package com.alexandra.Astro.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Observation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String object;
    @Column(columnDefinition="TEXT")
    private String note;

    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private Date date;

    @ManyToOne
    @JsonBackReference
    private Category category;

    private String place;
    private Status status;
    private Boolean isHomework;
    @Column(columnDefinition="TEXT")
    private String comment;

    @ManyToOne
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "observation", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ObservationPhoto> observationPhotos;

    @OneToMany(mappedBy = "observation", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ExpansionObserv> expansionObservs;
}
