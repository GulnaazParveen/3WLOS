package com.example.backend.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Customer;
import com.example.backend.entity.Loan;
import com.example.backend.entity.LoanApplication;
import com.example.backend.entity.Vehicle;

@Service
public class PdfGenerationService {

    // Folder where generated KFS and Agreement PDFs will be stored.
    // We are using the same local-storage approach as your existing
    // DocumentService.
    private final String GENERATED_DIR =
            "C:/uploads/loan_documents/generated/";

    /**
     * Generates the KFS PDF for an application.
     *
     * @param application Loan application containing customer, vehicle and loan details
     * @return File object representing the generated KFS PDF
     */
    public File generateKfsPdf(LoanApplication application) throws IOException {

        // Create the generated document directory if it does not exist.
        File directory = new File(GENERATED_DIR);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create a meaningful PDF file name using the application ID.
        String fileName =
                "KFS_" + application.getApplicationId() + ".pdf";

        String filePath = GENERATED_DIR + fileName;

        // Create a new PDF document.
        try (PDDocument document = new PDDocument()) {

            // Add one blank page to the PDF.
            PDPage page = new PDPage();
            document.addPage(page);

            // ContentStream is used to write text inside the PDF.
            try (PDPageContentStream content =
                         new PDPageContentStream(document, page)) {

                // Write the title.
                writeText(
                        content,
                        "KEY FACT STATEMENT (KFS)",
                        50,
                        750,
                        18
                );

                // Write application information.
                writeText(
                        content,
                        "Application ID: "
                                + application.getApplicationId(),
                        50,
                        710,
                        11
                );

                writeText(
                        content,
                        "Date: "
                                + LocalDate.now()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                        50,
                        690,
                        11
                );

                // Customer section
                writeText(
                        content,
                        "CUSTOMER DETAILS",
                        50,
                        650,
                        14
                );

                Customer customer = application.getCustomer();

                writeText(
                        content,
                        "Name: " + customer.getName(),
                        50,
                        625,
                        11
                );

                writeText(
                        content,
                        "Mobile: " + customer.getMobile(),
                        50,
                        605,
                        11
                );

                writeText(
                        content,
                        "PAN: " + customer.getPan(),
                        50,
                        585,
                        11
                );

                writeText(
                        content,
                        "Address: " + customer.getAddress(),
                        50,
                        565,
                        11
                );

                // Vehicle section
                Vehicle vehicle = application.getVehicle();

                writeText(
                        content,
                        "VEHICLE DETAILS",
                        50,
                        525,
                        14
                );

                writeText(
                        content,
                        "Vehicle Type: "
                                + vehicle.getVehicleType(),
                        50,
                        500,
                        11
                );

                writeText(
                        content,
                        "Vehicle Price:Rs."
                                + formatAmount(vehicle.getVehiclePrice()),
                        50,
                        480,
                        11
                );

                // Loan section
                Loan loan = application.getLoan();

                writeText(
                        content,
                        "LOAN DETAILS",
                        50,
                        440,
                        14
                );

                writeText(
                        content,
                        "Loan Amount:Rs."
                                + formatAmount(loan.getLoanAmount()),
                        50,
                        415,
                        11
                );

                writeText(
                        content,
                        "Down Payment:Rs."
                                + formatAmount(loan.getDownPayment()),
                        50,
                        395,
                        11
                );

                writeText(
                        content,
                        "Interest Rate: "
                                + loan.getInterestRate()
                                + "%",
                        50,
                        375,
                        11
                );

                writeText(
                        content,
                        "Tenure: "
                                + loan.getTenureMonths()
                                + " months",
                        50,
                        355,
                        11
                );

                writeText(
                        content,
                        "Monthly EMI:Rs."
                                + formatAmount(loan.getMonthlyEmi()),
                        50,
                        335,
                        11
                );

                // Important demo disclaimer.
                writeText(
                        content,
                        "This KFS is generated for demonstration purposes "
                                + "as part of a portfolio project.",
                        50,
                        280,
                        9
                );
            }

            // Save the PDF to our local folder.
            document.save(filePath);
        }

        // Return the generated PDF file.
        return new File(filePath);
    }

    /**
     * Generates the Loan Agreement PDF.
     */
    public File generateAgreementPdf(
            LoanApplication application) throws IOException {

        // Create the directory if required.
        File directory = new File(GENERATED_DIR);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName =
                "Loan_Agreement_"
                        + application.getApplicationId()
                        + ".pdf";

        String filePath = GENERATED_DIR + fileName;

        // Create PDF document.
        try (PDDocument document = new PDDocument()) {

            // Add a page.
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content =
                         new PDPageContentStream(document, page)) {

                // Agreement title.
                writeText(
                        content,
                        "LOAN AGREEMENT",
                        50,
                        750,
                        18
                );

                writeText(
                        content,
                        "Application ID: "
                                + application.getApplicationId(),
                        50,
                        710,
                        11
                );

                // Customer information.
                Customer customer = application.getCustomer();

                writeText(
                        content,
                        "BORROWER DETAILS",
                        50,
                        660,
                        14
                );

                writeText(
                        content,
                        "Borrower Name: "
                                + customer.getName(),
                        50,
                        635,
                        11
                );

                writeText(
                        content,
                        "Mobile: "
                                + customer.getMobile(),
                        50,
                        615,
                        11
                );

                writeText(
                        content,
                        "PAN: "
                                + customer.getPan(),
                        50,
                        595,
                        11
                );

                // Loan information.
                Loan loan = application.getLoan();

                writeText(
                        content,
                        "LOAN TERMS",
                        50,
                        555,
                        14
                );

                writeText(
                        content,
                        "Loan Amount:Rs."
                                + formatAmount(loan.getLoanAmount()),
                        50,
                        530,
                        11
                );

                writeText(
                        content,
                        "Down Payment:Rs."
                                + formatAmount(loan.getDownPayment()),
                        50,
                        510,
                        11
                );

                writeText(
                        content,
                        "Interest Rate: "
                                + loan.getInterestRate()
                                + "%",
                        50,
                        490,
                        11
                );

                writeText(
                        content,
                        "Tenure: "
                                + loan.getTenureMonths()
                                + " months",
                        50,
                        470,
                        11
                );

                writeText(
                        content,
                        "Monthly EMI:Rs."
                                + formatAmount(loan.getMonthlyEmi()),
                        50,
                        450,
                        11
                );

                // Vehicle information.
                Vehicle vehicle = application.getVehicle();

                writeText(
                        content,
                        "VEHICLE DETAILS",
                        50,
                        410,
                        14
                );

                writeText(
                        content,
                        "Vehicle Type: "
                                + vehicle.getVehicleType(),
                        50,
                        385,
                        11
                );

                writeText(
                        content,
                        "Vehicle Price:Rs."
                                + formatAmount(vehicle.getVehiclePrice()),
                        50,
                        365,
                        11
                );

                // Simple agreement text.
                writeText(
                        content,
                        "The borrower agrees to the loan terms "
                                + "mentioned above.",
                        50,
                        315,
                        11
                );

                writeText(
                        content,
                        "This agreement is a demonstration document "
                                + "created for a portfolio project.",
                        50,
                        285,
                        9
                );
            }

            // Save the PDF.
            document.save(filePath);
        }

        return new File(filePath);
    }

    /**
     * Writes one line of text into the PDF.
     */
private void writeText(
        PDPageContentStream content,
        String text,
        float x,
        float y,
        int fontSize) throws IOException {

    // Start writing text inside the PDF.
    content.beginText();

    // Select the Helvetica font.
    content.setFont(PDType1Font.HELVETICA, fontSize);

    // Set the position where the text should appear.
    content.newLineAtOffset(x, y);

    // Write the actual text.
    content.showText(text);

    // Finish writing text.
    content.endText();
}

    /**
     * Converts BigDecimal amount into a simple display format.
     */
    private String formatAmount(BigDecimal amount) {

        // If amount is null, return 0.00 instead of causing NullPointerException.
        if (amount == null) {
            return "0.00";
        }

        return amount.setScale(2).toPlainString();
    }
}