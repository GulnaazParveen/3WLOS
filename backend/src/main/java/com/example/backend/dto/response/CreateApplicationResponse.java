package com.example.backend.dto.response;

import java.math.BigDecimal;

import com.example.backend.enums.ApplicationStage;
import com.example.backend.enums.ApplicationStatus;
import com.example.backend.enums.VehicleType;

public record CreateApplicationResponse(

        Long applicationId,

        ApplicationStatus status,

        ApplicationStage currentStage,

        CustomerResponse customer,

        VehicleResponse vehicle,

        LoanResponse loan

) {

    public record CustomerResponse(
            Long customerId,
            String name,
            Integer age,
            String mobile,
            String pan,
            String address
    ) {
    }

    public record VehicleResponse(
            Long vehicleId,
            String vehicleType,
            BigDecimal vehiclePrice
    ) {

        public VehicleResponse(Long vehicleId2, VehicleType vehicleType2, BigDecimal vehiclePrice2) {
                this(vehicleId2, vehicleType2 != null ? vehicleType2.name() : null, vehiclePrice2);
        }
    }

    public record LoanResponse(
            Long loanId,
            BigDecimal loanAmount,
            BigDecimal downPayment,
            Integer tenureMonths,
            BigDecimal interestRate,
            BigDecimal monthlyEmi
    ) {
    }
}