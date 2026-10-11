package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Vehicle;

public interface VehicleRepository
        extends JpaRepository<Vehicle, Long> {
}
