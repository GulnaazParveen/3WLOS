package com.example.backend.dto.request;

import jakarta.validation.constraints.*;

public class CustomerRequest {

    @NotBlank
    private String name;

    @Min(18)
    @Max(70)
    private Integer age;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$")
    private String mobile;

    @NotBlank
    private String pan;

    @NotBlank
    private String address;

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
