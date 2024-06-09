package com.alexandra.Astro.Services;

import com.alexandra.Astro.Models.Meeting;
import com.alexandra.Astro.Repositories.MeetingRepository;
import com.alexandra.Astro.Repositories.UserElectedMeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MeetingService {
    private MeetingRepository meetingRepository;
    private UserElectedMeetingRepository userElectedMeetingRepository;

    @Autowired
    public void setRepositories(MeetingRepository meetingRepository,
                                UserElectedMeetingRepository userElectedMeetingRepository) {
        this.meetingRepository = meetingRepository;
        //this.guideRepository = guideRepository;
        this.userElectedMeetingRepository = userElectedMeetingRepository;
    }

    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    public List<Meeting> getActualMeetings() {
        return meetingRepository.findByStatusNotContaining("Архив");
    }

    public List<Meeting> getElectedMeetings(Long id) {
        List<Meeting> meetings = new ArrayList<>();

        for (var userMeeting : userElectedMeetingRepository.findByUserId(id)){
            try {
                meetings.add(userMeeting.getMeeting());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return meetings;
    }

    public Meeting getMeetingById(Long id) {
        return meetingRepository.findById(id).get();
    }

    public void insertMeeting(Meeting meeting) {
        meetingRepository.save(meeting);
    }

    public void deleteMeetingById(Long id) {
        meetingRepository.deleteById(id);
    }

    /*
     * TODO: Get Excursion By keyword and Pagination
     */
    public Page<Meeting> listAll(int pageNum, int size, String filter, String keyword, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

        Page<Meeting> pageMeetings;
        if (keyword == null && filter.equals("Все")) {
            pageMeetings = meetingRepository.findAll(pageable);
        } else if(keyword != null && filter.equals("Все")){
            pageMeetings = meetingRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else if(keyword == null){
            pageMeetings = meetingRepository.findByStatusContainingIgnoreCase(filter, pageable);
        } else
            pageMeetings = meetingRepository.findByTitleContainingIgnoreCaseAndStatusContainingIgnoreCase(keyword, filter, pageable);

        return pageMeetings;
    }
}
