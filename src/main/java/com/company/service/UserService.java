package com.company.service;

import com.company.dto.*;

public interface UserService {
    UserListDto getUsers();
    MeetingListDto getMeetings();
    boolean logIn(LoginDto loginDto);
    UserDto getLoggedUser();
    boolean registerUser(RegisterDto registerDto);
    MeetingDetailsDto getPersonMeetingListDto(Integer id_meeting);
}
