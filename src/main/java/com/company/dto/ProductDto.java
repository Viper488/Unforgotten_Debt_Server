package com.company.dto;

public class ProductDto {
    private Integer id_product;
    private String name;
    private Double price;
    private Integer id_person;
    private Integer id_meeting;

    public ProductDto() {
    }

    public ProductDto(Integer id_product, String name, Double price, Integer id_person, Integer id_meeting) {
        this.id_product = id_product;
        this.name = name;
        this.price = price;
        this.id_person = id_person;
        this.id_meeting = id_meeting;
    }

    public Integer getId_product() {
        return id_product;
    }

    public void setId_product(Integer id_product) {
        this.id_product = id_product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getId_person() {
        return id_person;
    }

    public void setId_person(Integer id_person) {
        this.id_person = id_person;
    }

    public Integer getId_meeting() {
        return id_meeting;
    }

    public void setId_meeting(Integer id_meeting) {
        this.id_meeting = id_meeting;
    }
}
