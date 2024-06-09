package com.alexandra.Astro.Repositories;

import com.alexandra.Astro.Models.UserElectedMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserElectedMeetingRepository extends JpaRepository<UserElectedMeeting, Long> {
    List<UserElectedMeeting> findByUserId(Long id);

    UserElectedMeeting findByUserIdAndMeetingId(Long userId, Long meetingId);
}