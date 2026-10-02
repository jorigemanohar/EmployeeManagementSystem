package com.employee.employeemanagement.service;

import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.entity.Payroll;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PayrollPdfService {

    public byte[] generateSalarySlip(Payroll payroll) {

        try {
            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(
                    document,
                    outputStream
            );

            document.open();

            // Fonts
            Font titleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            20
                    );

            Font headingFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            13
                    );

            Font normalFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            11
                    );

            // Company title
            Paragraph title =
                    new Paragraph(
                            "EMPLOYEE MANAGEMENT SYSTEM",
                            titleFont
                    );

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            Paragraph salarySlipTitle =
                    new Paragraph(
                            "SALARY SLIP",
                            headingFont
                    );

            salarySlipTitle.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(salarySlipTitle);

            document.add(
                    new Paragraph(" ")
            );

            // Employee information
            Employee employee =
                    payroll.getEmployee();

            Paragraph employeeHeading =
                    new Paragraph(
                            "Employee Information",
                            headingFont
                    );

            document.add(employeeHeading);

            document.add(
                    new Paragraph(
                            "Employee Code: "
                                    + employee.getEmployeeCode(),
                            normalFont
                    )
            );

            document.add(
                    new Paragraph(
                            "Employee Name: "
                                    + employee.getFirstName()
                                    + " "
                                    + employee.getLastName(),
                            normalFont
                    )
            );

            document.add(
                    new Paragraph(
                            "Email: "
                                    + employee.getEmail(),
                            normalFont
                    )
            );

            document.add(
                    new Paragraph(
                            "Designation: "
                                    + employee.getDesignation(),
                            normalFont
                    )
            );

            if (employee.getDepartment() != null) {

                document.add(
                        new Paragraph(
                                "Department: "
                                        + employee
                                        .getDepartment()
                                        .getDepartmentName(),
                                normalFont
                        )
                );
            }

            document.add(
                    new Paragraph(" ")
            );

            // Payroll information
            Paragraph payrollHeading =
                    new Paragraph(
                            "Payroll Information",
                            headingFont
                    );

            document.add(payrollHeading);

            document.add(
                    new Paragraph(
                            "Month: "
                                    + payroll.getMonth(),
                            normalFont
                    )
            );

            document.add(
                    new Paragraph(" ")
            );

            // Salary table
            PdfPTable table =
                    new PdfPTable(2);

            table.setWidthPercentage(100);

            table.setWidths(
                    new float[]{3, 2}
            );

            addTableHeader(
                    table,
                    "Salary Component",
                    "Amount"
            );

            addTableRow(
                    table,
                    "Basic Salary",
                    "Rs. "
                            + formatAmount(
                            payroll.getBasicSalary()
                    )
            );

            addTableRow(
                    table,
                    "Allowances",
                    "Rs. "
                            + formatAmount(
                            payroll.getAllowances()
                    )
            );

            addTableRow(
                    table,
                    "Deductions",
                    "Rs. "
                            + formatAmount(
                            payroll.getDeductions()
                    )
            );

            addTableRow(
                    table,
                    "Net Salary",
                    "Rs. "
                            + formatAmount(
                            payroll.getNetSalary()
                    )
            );

            document.add(table);

            document.add(
                    new Paragraph(" ")
            );

            // Payment status
            document.add(
                    new Paragraph(
                            "Payment Status: "
                                    + payroll.getPaymentStatus(),
                            headingFont
                    )
            );

            document.add(
                    new Paragraph(" ")
            );

            document.add(
                    new Paragraph(
                            "This is a system-generated salary slip.",
                            normalFont
                    )
            );

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to generate salary slip PDF",
                    e
            );
        }
    }

    private void addTableHeader(
            PdfPTable table,
            String firstColumn,
            String secondColumn) {

        Font headerFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        11
                );

        PdfPCell firstCell =
                new PdfPCell(
                        new Paragraph(
                                firstColumn,
                                headerFont
                        )
                );

        PdfPCell secondCell =
                new PdfPCell(
                        new Paragraph(
                                secondColumn,
                                headerFont
                        )
                );

        firstCell.setBorder(
                Rectangle.BOX
        );

        secondCell.setBorder(
                Rectangle.BOX
        );

        table.addCell(firstCell);
        table.addCell(secondCell);
    }

    private void addTableRow(
            PdfPTable table,
            String component,
            String amount) {

        Font normalFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11
                );

        table.addCell(
                new PdfPCell(
                        new Paragraph(
                                component,
                                normalFont
                        )
                )
        );

        table.addCell(
                new PdfPCell(
                        new Paragraph(
                                amount,
                                normalFont
                        )
                )
        );
    }

    private String formatAmount(Double amount) {

        if (amount == null) {
            return "0.00";
        }

        return String.format(
                "%.2f",
                amount
        );
    }
}