package com.example.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.request.CreateApplicationRequest;
import com.example.backend.dto.response.CreateApplicationResponse;
import com.example.backend.entity.Customer;
import com.example.backend.entity.Loan;
import com.example.backend.entity.LoanApplication;
import com.example.backend.entity.Vehicle;
import com.example.backend.enums.ApplicationStage;
import com.example.backend.enums.ApplicationStatus;
import com.example.backend.repository.LoanApplicationRepository;

@Service
@Transactional
public class LoanApplicationService {

    private final LoanApplicationRepository applicationRepository;

    public LoanApplicationService(
            LoanApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public CreateApplicationResponse createApplication(
            CreateApplicationRequest request) {

        // 1. Read frontend data
        BigDecimal vehiclePrice =
                request.getVehicle().getVehiclePrice();

        BigDecimal loanAmount =
                request.getLoan().getLoanAmount();

        Integer tenure =
                request.getLoan().getTenureMonths();


        // 2. Validate loan amount
        if (loanAmount.compareTo(vehiclePrice) > 0) {
            throw new IllegalArgumentException(
                    "Loan amount cannot be greater than vehicle price");
        }


        // 3. Calculate down payment
        BigDecimal downPayment =
                vehiclePrice.subtract(loanAmount);


        // 4. Backend decides demo ROI
        BigDecimal interestRate =
                new BigDecimal("12.00");


        // 5. Calculate EMI
        BigDecimal monthlyEmi =
                calculateEmi(
                        loanAmount,
                        interestRate,
                        tenure
                );


        // 6. Create Customer entity
        Customer customer = new Customer();

        customer.setName(request.getCustomer().getName());
        customer.setAge(request.getCustomer().getAge());
        customer.setMobile(request.getCustomer().getMobile());
        customer.setPan(request.getCustomer().getPan());
        customer.setAddress(request.getCustomer().getAddress());


        // 7. Create Vehicle entity
        Vehicle vehicle = new Vehicle();

        vehicle.setVehicleType(
                request.getVehicle().getVehicleType());

        vehicle.setVehiclePrice(vehiclePrice);


        // 8. Create Loan entity
        Loan loan = new Loan();

        loan.setLoanAmount(loanAmount);
        loan.setDownPayment(downPayment);
        loan.setTenureMonths(tenure);
        loan.setInterestRate(interestRate);
        loan.setMonthlyEmi(monthlyEmi);


        // 9. Create application
        LoanApplication application =
                new LoanApplication();

        application.setCustomer(customer);
        application.setVehicle(vehicle);
        application.setLoan(loan);

        application.setStatus(
                ApplicationStatus.DRAFT);

        application.setCurrentStage(
                ApplicationStage.CUSTOMER_AND_LOAN_DETAILS);

        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        LoanApplication saved =
                applicationRepository.save(application);

        return mapToResponse(saved);
    }
   public LoanApplication submitApplication(Long applicationId) {

    LoanApplication application =
            applicationRepository.findById(applicationId)
                    .orElseThrow(() ->
                            new RuntimeException("Application not found"));

    // Module 1 must be completed before moving to Module 2
    if (application.getCurrentStage()
            != ApplicationStage.CUSTOMER_AND_LOAN_DETAILS) {

        throw new IllegalStateException(
                "Application is not in Customer & Loan Details stage");
    }

    // Move to Module 2
    application.setCurrentStage(
            ApplicationStage.KYC_AND_ELIGIBILITY
    );

    // Application is now submitted
    application.setStatus(
            ApplicationStatus.SUBMITTED
    );

    application.setUpdatedAt(LocalDateTime.now());

    return applicationRepository.save(application);
}

    
    private BigDecimal calculateEmi(
        BigDecimal principal,
        BigDecimal annualRate,
        int tenureMonths) {

    double p = principal.doubleValue();

    double r =
            annualRate.doubleValue() / 12 / 100;

    double n = tenureMonths;

    double emi =
            p * r * Math.pow(1 + r, n)
            / (Math.pow(1 + r, n) - 1);

    return BigDecimal
            .valueOf(emi)
            .setScale(2, RoundingMode.HALF_UP);
    }

    private CreateApplicationResponse mapToResponse(
            LoanApplication application) {

        return new CreateApplicationResponse(
                application.getApplicationId(),
                application.getStatus(),
                application.getCurrentStage(),
                new CreateApplicationResponse.CustomerResponse(
                        application.getCustomer().getCustomerId(),
                        application.getCustomer().getName(),
                        application.getCustomer().getAge(),
                        application.getCustomer().getMobile(),
                        application.getCustomer().getPan(),
                        application.getCustomer().getAddress()
                ),
                new CreateApplicationResponse.VehicleResponse(
                        application.getVehicle().getVehicleId(),
                        application.getVehicle().getVehicleType(),
                        application.getVehicle().getVehiclePrice()
                ),
                new CreateApplicationResponse.LoanResponse(
                        application.getLoan().getLoanId(),
                        application.getLoan().getLoanAmount(),
                        application.getLoan().getDownPayment(),
                        application.getLoan().getTenureMonths(),
                        application.getLoan().getInterestRate(),
                        application.getLoan().getMonthlyEmi()
                )
        );
    }

}
