package com.example.backend.repository;

import com.example.backend.entity.DrivingLicense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrivingLicenseRepository extends JpaRepository<DrivingLicense, Long> {
}