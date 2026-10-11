package com.example.backend.dto.request;

import com.example.backend.enums.VehicleType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class VehicleRequest {

    @NotNull
    private VehicleType vehicleType;

    @NotNull
    @DecimalMin("1.00")
    private BigDecimal vehiclePrice;

    // Getters and Setters

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public BigDecimal getVehiclePrice() {
        return vehiclePrice;
    }

    public void setVehiclePrice(BigDecimal vehiclePrice) {
        this.vehiclePrice = vehiclePrice;
    }
}
