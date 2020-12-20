package com.company.dto;

public class MeetingDto {
    private Integer id_meeting;
    private String name;
    private String code;

    public MeetingDto() {
    }

    public MeetingDto(Integer id_meeting, String name, String code) {
        this.id_meeting = id_meeting;
        this.name = name;
        this.code = code;
    }

    public Integer getId_meeting() {
        return id_meeting;
    }

    public void setId_meeting(Integer id_meeting) {
        this.id_meeting = id_meeting;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
