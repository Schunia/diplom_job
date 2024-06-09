//package com.alexandra.Astro.Models;
//
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.springframework.format.annotation.DateTimeFormat;
//
//import javax.persistence.*;
//import java.util.Date;
//import java.util.List;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//public class Lecture {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String title;
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    private Date date;
//    private String author;
//    private String note;
//    private String type;
//    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<LecturePhoto> lecturePhotos;
//}
