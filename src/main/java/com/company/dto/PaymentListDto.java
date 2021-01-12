package com.company.dto;

import java.util.List;

public class PaymentListDto {
    private List<PaymentGetDto> paymentDtoList;

    public PaymentListDto() {
    }

    public PaymentListDto(List<PaymentGetDto> paymentDtoList) {
        this.paymentDtoList = paymentDtoList;
    }

    public List<PaymentGetDto> getPaymentDtoList() {
        return paymentDtoList;
    }

    public void setPaymentDtoList(List<PaymentGetDto> paymentDtoList) {
        this.paymentDtoList = paymentDtoList;
    }
}
