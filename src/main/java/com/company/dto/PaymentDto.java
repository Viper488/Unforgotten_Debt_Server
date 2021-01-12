package com.company.dto;

public class PaymentDto {
    private float value;
    private int id_meeting;

    public PaymentDto() {
    }

    public PaymentDto(float value, int id_meeting) {
        this.value = value;
        this.id_meeting = id_meeting;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getId_meeting() {
        return id_meeting;
    }

    public void setId_meeting(int id_meeting) {
        this.id_meeting = id_meeting;
    }
}
