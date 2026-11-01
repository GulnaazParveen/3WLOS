
package com.example.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Mandate;
import com.example.backend.entity.LoanApplication;
import com.example.backend.enums.MandateStatus;

public interface MandateRepository extends JpaRepository<Mandate, Long> {

    /*
     * Find all mandate attempts for an application,
     * with the newest attempt first.
     *
     * Example:
     * Mandate 3 -> EXPIRED
     * Mandate 2 -> FAILED
     * Mandate 1 -> COMPLETED
     *
     * Useful for viewing the mandate history.
     */
    List<Mandate> findByLoanApplicationOrderByCreatedAtDesc(
        LoanApplication loanApplication
    );

    /*
     * Find the latest mandate attempt for an application.
     *
     * Useful for showing the current mandate status in the LOS.
     */
    Optional<Mandate> findTopByLoanApplicationOrderByCreatedAtDesc(
        LoanApplication loanApplication
    );

    /*
     * Check whether this application already has a completed mandate.
     *
     * If true, the service must not allow another mandate initiation.
     */
    boolean existsByLoanApplicationAndStatus(
        LoanApplication loanApplication,
        MandateStatus status
    );

    /*
     * Check whether an active mandate already exists.
     *
     * The service will pass INITIATED and OTP_PENDING as the statuses.
     * If an active attempt exists, the service must prevent a duplicate.
     */
    boolean existsByLoanApplicationAndStatusIn(
        LoanApplication loanApplication,
        Collection<MandateStatus> statuses
    );

    /*
     * Find a mandate using its unique reference number.
     */
    Optional<Mandate> findByMandateReference(
        String mandateReference
    );

    /*
     * Find a mandate using the hash of the customer action token.
     * The raw customer token should not be stored in the database.
     */
    Optional<Mandate> findByCustomerTokenHash(
        String customerTokenHash
    );
}
