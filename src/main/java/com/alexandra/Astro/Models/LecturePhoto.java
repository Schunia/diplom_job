//package com.alexandra.Astro.Models;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.persistence.*;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//public class LecturePhoto {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    @ManyToOne
//    @JsonBackReference
//    private Lecture lecture;
//    private String pathPhoto;
//}
