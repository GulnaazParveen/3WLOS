package com.example.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EligibilityResponse {

    // ------------------------------------------------
    // Age
    // ------------------------------------------------

    private int age;
    private boolean ageCheckPassed;

    // ------------------------------------------------
    // Driving Experience
    // ------------------------------------------------

    private int experienceYears;
    private boolean experienceCheckPassed;

    // ------------------------------------------------
    // LTV
    // ------------------------------------------------

    private BigDecimal loanAmount;
    private BigDecimal vehiclePrice;
    private BigDecimal maxEligibleLoanAmount;
    private BigDecimal ltvPercentage;
    private boolean ltvCheckPassed;

    // ------------------------------------------------
    // Licence Validity
    // ------------------------------------------------

    private LocalDate licenseExpiryDate;
    private int licenseValidityRemainingMonths;
    private boolean validityCheckPassed;

    // ------------------------------------------------
    // Overall Result
    // ------------------------------------------------

    private String overallStatus;
    private String message;


    // ------------------------------------------------
    // Getters and Setters
    // ------------------------------------------------

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isAgeCheckPassed() {
        return ageCheckPassed;
    }

    public void setAgeCheckPassed(boolean ageCheckPassed) {
        this.ageCheckPassed = ageCheckPassed;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public boolean isExperienceCheckPassed() {
        return experienceCheckPassed;
    }

    public void setExperienceCheckPassed(boolean experienceCheckPassed) {
        this.experienceCheckPassed = experienceCheckPassed;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getVehiclePrice() {
        return vehiclePrice;
    }

    public void setVehiclePrice(BigDecimal vehiclePrice) {
        this.vehiclePrice = vehiclePrice;
    }

    public BigDecimal getMaxEligibleLoanAmount() {
        return maxEligibleLoanAmount;
    }

    public void setMaxEligibleLoanAmount(BigDecimal maxEligibleLoanAmount) {
        this.maxEligibleLoanAmount = maxEligibleLoanAmount;
    }

    public BigDecimal getLtvPercentage() {
        return ltvPercentage;
    }

    public void setLtvPercentage(BigDecimal ltvPercentage) {
        this.ltvPercentage = ltvPercentage;
    }

    public boolean isLtvCheckPassed() {
        return ltvCheckPassed;
    }

    public void setLtvCheckPassed(boolean ltvCheckPassed) {
        this.ltvCheckPassed = ltvCheckPassed;
    }

    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public int getLicenseValidityRemainingMonths() {
        return licenseValidityRemainingMonths;
    }

    public void setLicenseValidityRemainingMonths(
            int licenseValidityRemainingMonths) {
        this.licenseValidityRemainingMonths =
                licenseValidityRemainingMonths;
    }

    public boolean isValidityCheckPassed() {
        return validityCheckPassed;
    }

    public void setValidityCheckPassed(boolean validityCheckPassed) {
        this.validityCheckPassed = validityCheckPassed;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}