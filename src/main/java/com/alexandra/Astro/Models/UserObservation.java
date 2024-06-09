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
public class UserObservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "observation_id")
    private Observation observation;

    public UserObservation(User user, Observation observation) {
        this.user = user;
        this.observation = observation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserObservation)) return false;
        UserObservation that = (UserObservation) o;
        return Objects.equals(user.getEmail(), that.user.getEmail()) &&
                Objects.equals(observation.getObject(), that.observation.getObject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getEmail(), observation.getObject());
    }
}
