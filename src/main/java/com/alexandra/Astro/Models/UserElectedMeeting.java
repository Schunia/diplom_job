package com.alexandra.Astro.Models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class UserElectedMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    public UserElectedMeeting(User user, Meeting meeting) {
        this.user = user;
        this.meeting = meeting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserElectedMeeting)) return false;
        UserElectedMeeting that = (UserElectedMeeting) o;
        return Objects.equals(user.getEmail(), that.user.getEmail()) &&
                Objects.equals(meeting.getTitle(), that.meeting.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getEmail(), meeting.getTitle());
    }
}
