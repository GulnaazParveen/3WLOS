
package com.example.backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.response.CreateMandateResponse;
import com.example.backend.dto.response.MandateStatusResponse;
import com.example.backend.entity.BankDetails;
import com.example.backend.entity.LoanApplication;
import com.example.backend.entity.Mandate;
import com.example.backend.enums.ApplicationStage;
import com.example.backend.enums.MandateStatus;
import com.example.backend.repository.BankDetailsRepository;
import com.example.backend.repository.LoanApplicationRepository;
import com.example.backend.repository.MandateRepository;

@Service
@Transactional
public class MandateService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final MandateRepository mandateRepository;

    // Generates secure random tokens and OTPs.
    private final SecureRandom secureRandom = new SecureRandom();

    /*
     * Customer frontend URL.
     * Configure it in application.properties.
     */
    @Value("${app.frontend-base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    public MandateService(
            LoanApplicationRepository loanApplicationRepository,
            BankDetailsRepository bankDetailsRepository,
            MandateRepository mandateRepository) {

        this.loanApplicationRepository = loanApplicationRepository;
        this.bankDetailsRepository = bankDetailsRepository;
        this.mandateRepository = mandateRepository;
    }

    // =========================================================
    // 1. INITIATE MANDATE
    // =========================================================

    public CreateMandateResponse createMandate(Long applicationId) {

        // Step 1: Find the loan application.
        LoanApplication application = loanApplicationRepository
                .findById(applicationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Application not found: " + applicationId));

        // Step 2: Mandate initiation is allowed only at this stage.
        if (application.getCurrentStage()
                != ApplicationStage.REVIEW_AND_CONSENT) {

            throw new IllegalStateException(
                    "Application must be at REVIEW_AND_CONSENT stage");
        }

        // Step 3: Bank details must be saved first.
        BankDetails bankDetails = bankDetailsRepository
                .findByLoanApplication(application)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Please save bank details before initiating mandate"));

        // The variable is used to make the prerequisite explicit.
        // Further bank validation can be added if required.
        if (bankDetails.getLoanApplication() == null) {
            throw new IllegalStateException(
                    "Valid bank details are required");
        }

        // Step 4: Never initiate a second mandate after completion.
        boolean completed = mandateRepository
                .existsByLoanApplicationAndStatus(
                        application,
                        MandateStatus.COMPLETED);

        if (completed) {
            throw new IllegalStateException(
                    "Mandate is already completed for this application");
        }

        // Step 5: Prevent duplicate active mandates.
        boolean activeMandateExists = mandateRepository
                .existsByLoanApplicationAndStatusIn(
                        application,
                        List.of(
                                MandateStatus.INITIATED,
                                MandateStatus.OTP_PENDING));

        if (activeMandateExists) {
            throw new IllegalStateException(
                    "An active mandate already exists. "
                    + "Please use the existing customer link.");
        }

        /*
         * If previous attempts are FAILED or EXPIRED,
         * neither check above blocks initiation.
         * Therefore, a fresh mandate record can be created.
         */

        // Step 6: Generate a unique customer token.
        String rawToken = generateSecureToken();

        // Store only the hash, never the raw token.
        String tokenHash = hash(rawToken);

        // Step 7: Create a new mandate attempt.
        Mandate mandate = new Mandate();

        mandate.setLoanApplication(application);

        mandate.setMandateReference(
                "MND-" + UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase());

        mandate.setCustomerTokenHash(tokenHash);
        mandate.setStatus(MandateStatus.INITIATED);
        mandate.setFailedAttempts(0);

        LocalDateTime now = LocalDateTime.now();

        mandate.setCreatedAt(now);
        mandate.setUpdatedAt(now);

        // Save the new attempt; older attempts remain in the database.
        mandateRepository.save(mandate);

        // Remove a trailing slash to avoid a double slash in the URL.
        String baseUrl = frontendBaseUrl.replaceAll("/+$", "");

        // This is the link the customer will open.
        String customerActionLink =
                baseUrl + "/mandate/" + rawToken;

        return new CreateMandateResponse(
                mandate.getMandateId(),
                applicationId,
                mandate.getMandateReference(),
                mandate.getStatus(),
                customerActionLink,
                "Mandate initiated successfully");
    }

    // =========================================================
    // 2. SEND / RESEND OTP
    // =========================================================

    public MandateStatusResponse sendOtp(String rawToken) {

        // Find the mandate using the hash of the supplied token.
        Mandate mandate = findMandateByToken(rawToken);

        // A completed mandate cannot request another OTP.
        if (mandate.getStatus() == MandateStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Mandate is already completed");
        }

        // A failed or expired attempt needs a new mandate initiation.
        if (mandate.getStatus() == MandateStatus.FAILED
                || mandate.getStatus() == MandateStatus.EXPIRED) {

            throw new IllegalStateException(
                    "This mandate attempt is no longer active. "
                    + "Please initiate a new mandate from the LOS.");
        }

        /*
         * If an existing OTP has expired, mark this attempt EXPIRED.
         *
         * Important: return a response instead of throwing a runtime
         * exception after saving. Otherwise, @Transactional could
         * roll back the EXPIRED status.
         */
        if (mandate.getStatus() == MandateStatus.OTP_PENDING
                && mandate.getOtpExpiresAt() != null
                && !mandate.getOtpExpiresAt().isAfter(
                        LocalDateTime.now())) {

            expireMandate(mandate);

            return toStatusResponse(
                    mandate,
                    "OTP has expired. Please initiate a new mandate.");
        }

        // Generate a six-digit OTP, including leading-zero-safe handling.
        String otp = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000));

        // Save only the hash of the OTP.
        mandate.setOtpHash(hash(otp));

        // OTP is valid for five minutes.
        mandate.setOtpExpiresAt(
                LocalDateTime.now().plusMinutes(5));

        mandate.setStatus(MandateStatus.OTP_PENDING);
        mandate.setUpdatedAt(LocalDateTime.now());

        /*
         * Do not reset failedAttempts on every resend.
         * This prevents unlimited retries by repeatedly requesting OTPs.
         */
        mandateRepository.save(mandate);

        /*
         * DEMO ONLY:
         * Print OTP in the console because real SMS is not integrated yet.
         * Never log OTPs in production.
         */
        System.out.println(
                "[DEMO ONLY] OTP for mandate "
                + mandate.getMandateReference()
                + " = " + otp);

        return toStatusResponse(
                mandate,
                "OTP generated successfully. Check the demo console.");
    }

    // =========================================================
    // 3. VERIFY CUSTOMER OTP
    // =========================================================

    public MandateStatusResponse verifyOtp(
            String rawToken,
            String submittedOtp) {

        Mandate mandate = findMandateByToken(rawToken);

        // Reject attempts that are no longer active.
        if (mandate.getStatus() == MandateStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Mandate is already completed");
        }

        if (mandate.getStatus() == MandateStatus.FAILED
                || mandate.getStatus() == MandateStatus.EXPIRED) {

            throw new IllegalStateException(
                    "Mandate is no longer active. "
                    + "Please initiate a new mandate.");
        }

        if (mandate.getStatus() != MandateStatus.OTP_PENDING
                || mandate.getOtpHash() == null
                || mandate.getOtpExpiresAt() == null) {

            throw new IllegalStateException(
                    "Please request an OTP before verification");
        }

        // Check expiry before comparing the OTP.
        if (!mandate.getOtpExpiresAt().isAfter(
                LocalDateTime.now())) {

            expireMandate(mandate);

            return toStatusResponse(
                    mandate,
                    "OTP expired. Please initiate a new mandate.");
        }

        // Check the maximum number of incorrect OTP submissions.
        if (mandate.getFailedAttempts() >= 3) {

            failMandate(mandate);

            return toStatusResponse(
                    mandate,
                    "Maximum OTP attempts reached. "
                    + "Please initiate a new mandate.");
        }

        // Compare hashes rather than storing or comparing plain OTPs.
        String submittedOtpHash = hash(submittedOtp);

        boolean otpMatches = MessageDigest.isEqual(
                mandate.getOtpHash().getBytes(StandardCharsets.UTF_8),
                submittedOtpHash.getBytes(StandardCharsets.UTF_8));

        if (!otpMatches) {

            // Record the incorrect attempt.
            mandate.setFailedAttempts(
                    mandate.getFailedAttempts() + 1);

            mandate.setUpdatedAt(LocalDateTime.now());

            // Third incorrect attempt permanently fails this attempt.
            if (mandate.getFailedAttempts() >= 3) {

                failMandate(mandate);

                return toStatusResponse(
                        mandate,
                        "Incorrect OTP. Maximum attempts reached. "
                        + "Please initiate a new mandate.");
            }

            // Save and return normally so the attempt count commits.
            mandateRepository.save(mandate);

            return toStatusResponse(
                    mandate,
                    "Incorrect OTP. Attempts remaining: "
                    + (3 - mandate.getFailedAttempts()));
        }

        // =====================================================
        // OTP CORRECT: COMPLETE MANDATE
        // =====================================================

        mandate.setStatus(MandateStatus.COMPLETED);
        mandate.setOtpVerifiedAt(LocalDateTime.now());

        // Clear OTP secrets after successful verification.
        mandate.setOtpHash(null);
        mandate.setOtpExpiresAt(null);

        mandate.setUpdatedAt(LocalDateTime.now());

        mandateRepository.save(mandate);

        // Move the application to the next agreed business stage.
        LoanApplication application = mandate.getLoanApplication();

        application.setCurrentStage(
                ApplicationStage.KFS_AND_AGREEMENT);

        loanApplicationRepository.save(application);

        return toStatusResponse(
                mandate,
                "Mandate completed successfully. "
                + "Application moved to KFS_AND_AGREEMENT.");
    }

    // =========================================================
    // 4. MARK A MANDATE EXPIRED
    // =========================================================

    private void expireMandate(Mandate mandate) {

        mandate.setStatus(MandateStatus.EXPIRED);

        // Remove the unusable OTP.
        mandate.setOtpHash(null);
        mandate.setOtpExpiresAt(null);

        mandate.setUpdatedAt(LocalDateTime.now());

        mandateRepository.save(mandate);
    }

    // =========================================================
    // 5. MARK A MANDATE FAILED
    // =========================================================

    private void failMandate(Mandate mandate) {

        mandate.setStatus(MandateStatus.FAILED);

        // Remove the unusable OTP.
        mandate.setOtpHash(null);
        mandate.setOtpExpiresAt(null);

        mandate.setUpdatedAt(LocalDateTime.now());

        mandateRepository.save(mandate);
    }

    // =========================================================
    // 6. FIND MANDATE USING CUSTOMER TOKEN
    // =========================================================

    private Mandate findMandateByToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer action token is required");
        }

        String tokenHash = hash(rawToken);

        return mandateRepository
                .findByCustomerTokenHash(tokenHash)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid customer action link"));
    }

    // =========================================================
    // 7. GENERATE A SECURE CUSTOMER TOKEN
    // =========================================================

    private String generateSecureToken() {

        // Generate 32 random bytes for a high-entropy token.
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);

        // URL-safe representation without padding.
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);
    }

    // =========================================================
    // 8. HASH TOKEN / OTP USING SHA-256
    // =========================================================

    private String hash(String value) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8));

            // Convert bytes to a hexadecimal string.
            StringBuilder hex = new StringBuilder();

            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is unavailable",
                    exception);
        }
    }

    // =========================================================
    // 9. BUILD A CONSISTENT STATUS RESPONSE
    // =========================================================

    private MandateStatusResponse toStatusResponse(
            Mandate mandate,
            String message) {

        return new MandateStatusResponse(
                mandate.getLoanApplication().getApplicationId(),
                mandate.getMandateReference(),
                mandate.getStatus(),
                message);
    }


/*
 * Get the latest mandate status for the LOS screen.
 * Older failed or expired attempts remain stored in the database.
 */
public MandateStatusResponse getLatestMandateStatus(Long applicationId) {

    LoanApplication application = loanApplicationRepository
            .findById(applicationId)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Application not found: " + applicationId));

    Mandate mandate = mandateRepository
            .findTopByLoanApplicationOrderByCreatedAtDesc(application)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "No mandate has been initiated for this application"));

    return toStatusResponse(
            mandate,
            "Latest mandate status retrieved successfully");
}

}
