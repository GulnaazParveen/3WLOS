
package com.example.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import com.example.backend.enums.MandateStatus;

@Entity
@Table(
    name = "mandate",
    uniqueConstraints = {
        // Every mandate attempt must have a unique reference number.
        // application_id is intentionally NOT unique because an application
        // can have multiple mandate attempts over time.
        @UniqueConstraint(columnNames = "mandate_reference")
    }
)
public class Mandate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mandate_id")
    private Long mandateId;

    /*
     * Many mandate records can belong to one loan application.
     *
     * Example:
     * Application 26 -> Mandate 1 (EXPIRED)
     * Application 26 -> Mandate 2 (FAILED)
     * Application 26 -> Mandate 3 (COMPLETED)
     *
     * Therefore, we use @ManyToOne instead of @OneToOne.
     * Do not set unique = true on application_id.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "application_id",
        nullable = false
    )
    private LoanApplication loanApplication;

    // Unique reference generated for each mandate attempt.
    @Column(
        name = "mandate_reference",
        nullable = false,
        unique = true
    )
    private String mandateReference;

    /*
     * Store the HASH of the customer action token, not the raw token.
     * The raw token is used in the customer link, while its hash is used
     * to find and validate the mandate on the backend.
     */
    @Column(name = "customer_token_hash", nullable = false)
    private String customerTokenHash;

    /*
     * Store the OTP hash instead of the original OTP.
     * This field becomes null after successful verification or when
     * the OTP is cleared during a new OTP request.
     */
    @Column(name = "otp_hash")
    private String otpHash;

    // INITIATED, OTP_PENDING, COMPLETED, FAILED, or EXPIRED.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MandateStatus status;

    // Date and time when the current OTP expires.
    private LocalDateTime otpExpiresAt;

    // Date and time when the customer successfully verified the OTP.
    private LocalDateTime otpVerifiedAt;

    /*
     * Number of incorrect OTP submissions for this OTP attempt.
     * The service will enforce the maximum allowed attempts.
     */
    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts = 0;

    // Audit field: when this mandate attempt was created.
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Audit field: when this mandate record was last updated.
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---------------- Getters and Setters ----------------

    public Long getMandateId() {
        return mandateId;
    }

    public void setMandateId(Long mandateId) {
        this.mandateId = mandateId;
    }

    public LoanApplication getLoanApplication() {
        return loanApplication;
    }

    public void setLoanApplication(LoanApplication loanApplication) {
        this.loanApplication = loanApplication;
    }

    public String getMandateReference() {
        return mandateReference;
    }

    public void setMandateReference(String mandateReference) {
        this.mandateReference = mandateReference;
    }

    public String getCustomerTokenHash() {
        return customerTokenHash;
    }

    public void setCustomerTokenHash(String customerTokenHash) {
        this.customerTokenHash = customerTokenHash;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    public MandateStatus getStatus() {
        return status;
    }

    public void setStatus(MandateStatus status) {
        this.status = status;
    }

    public LocalDateTime getOtpExpiresAt() {
        return otpExpiresAt;
    }

    public void setOtpExpiresAt(LocalDateTime otpExpiresAt) {
        this.otpExpiresAt = otpExpiresAt;
    }

    public LocalDateTime getOtpVerifiedAt() {
        return otpVerifiedAt;
    }

    public void setOtpVerifiedAt(LocalDateTime otpVerifiedAt) {
        this.otpVerifiedAt = otpVerifiedAt;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
