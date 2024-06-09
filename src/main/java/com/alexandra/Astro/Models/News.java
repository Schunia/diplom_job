package com.alexandra.Astro.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition="TEXT")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date date;
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
//    @Column(name = "date_meeting")
//    private Date meetingDate;
    private String pathPhoto;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "news_meet",
            joinColumns =
                    { @JoinColumn(name = "news_id", referencedColumnName = "id") },
            inverseJoinColumns =
                    { @JoinColumn(name = "meeting_id", referencedColumnName = "id") })
    private Meeting meeting;

}
