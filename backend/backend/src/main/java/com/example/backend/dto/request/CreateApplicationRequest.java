package com.example.backend.dto.request;



import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class CreateApplicationRequest {

    @NotNull
    @Valid
    private CustomerRequest customer;

    @NotNull
    @Valid
    private VehicleRequest vehicle;

    @NotNull
    @Valid
    private LoanRequest loan;

    public CustomerRequest getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerRequest customer) {
        this.customer = customer;
    }

    public VehicleRequest getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleRequest vehicle) {
        this.vehicle = vehicle;
    }

    public LoanRequest getLoan() {
        return loan;
    }

    public void setLoan(LoanRequest loan) {
        this.loan = loan;
    }
}
