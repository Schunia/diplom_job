package com.alexandra.Astro.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition="TEXT")
    private String description;
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private Date date;
    private String address;
    private String status;
    private String pathPhoto;
    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private Set<UserElectedMeeting> userElectedMeetings = new HashSet<>();
    @OneToOne(mappedBy = "meeting")
    private News news;

    public Meeting(String title) {
        this.title = title;
    }
}
