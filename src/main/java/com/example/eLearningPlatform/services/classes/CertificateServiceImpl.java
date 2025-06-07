package com.example.eLearningPlatform.services.classes;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.example.eLearningPlatform.models.entities.Certificate;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.Student;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.eLearningPlatform.repositories.CertificateRepository;
import com.example.eLearningPlatform.repositories.CourseRepository;
import com.example.eLearningPlatform.repositories.StudentRepository;
import com.example.eLearningPlatform.services.interfaces.CertificateService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository,
                                  StudentRepository studentRepository,
                                  CourseRepository courseRepository) {
        this.certificateRepository = certificateRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Certificate issueCertificate(Long studentId, Long courseId) {
        // Check if student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Check if course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if certificate already exists

        if (certificateRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
            throw new RuntimeException("Certificate already issued for this course");
        }


        // Save certificate in DB
        Certificate certificate = new Certificate();
        certificate.setStudent(student);
        certificate.setCourse(course);
        certificate.setCertificatePdf(null);

        certificate = certificateRepository.save(certificate);

        byte[] pdfData = generateCertificatePdf(student, course, certificate);

        certificate.setCertificatePdf(pdfData);
        return certificateRepository.save(certificate);
    }

    private byte[] generateCertificatePdf(Student student, Course course, Certificate certificate) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);

            // Set the page size to A4 portrait
            pdf.setDefaultPageSize(PageSize.A4);

            Document document = new Document(pdf);
            document.setMargins(0, 0, 0, 0); // Remove default margins for full control

            // Create a light purple background
            Rectangle pageSize = new Rectangle(pdf.getDefaultPageSize());
            PdfCanvas pdfCanvas = new PdfCanvas(pdf.addNewPage());
            pdfCanvas.saveState()
                    .setFillColor(new DeviceRgb(240, 230, 255))  // Light purple
                    .rectangle(pageSize)
                    .fill()
                    .restoreState();

            // Create a centered container div
            Div centerContainer = new Div()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setHeight(pageSize.getHeight())
                    .setWidth(pageSize.getWidth())
                    .setPadding(40); // Add some padding inside the container

            // Main content div with border
            Div contentDiv = new Div()
                    .setBorder(new SolidBorder(new DeviceRgb(120, 0, 120), 1))
                    .setPadding(40)
                    .setMaxWidth(500) // Constrain width for better centering
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            // Add platform name/logo at the top
            contentDiv.add(new Paragraph("eLearning Platform")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(12)
                    .setFontColor(new DeviceRgb(80, 0, 80))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(30));

            // Title with styling
            contentDiv.add(new Paragraph("CERTIFICATE OF COMPLETION")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(24)
                    .setFontColor(new DeviceRgb(80, 0, 80))
                    .setMarginBottom(25));

            // Divider line
            LineSeparator divider = new LineSeparator(new SolidLine(1f));
            divider.setStrokeColor(new DeviceRgb(120, 0, 120))
                    .setMarginBottom(25);
            contentDiv.add(divider);

            // Body text
            contentDiv.add(new Paragraph("This is to certify that")
                    .setFontSize(14)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(10));

            // Student name
            contentDiv.add(new Paragraph(student.getFirstName() + " " + student.getLastName())
                    .setFontSize(20)
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontColor(new DeviceRgb(70, 0, 70))
                    .setMarginBottom(10));

            contentDiv.add(new Paragraph("has successfully completed the course")
                    .setFontSize(14)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(15));

            // Course name
            contentDiv.add(new Paragraph("\"" + course.getName() + "\"")
                    .setFontSize(18)
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontColor(new DeviceRgb(80, 0, 80))
                    .setMarginBottom(20));

            // Issue date
            contentDiv.add(new Paragraph("issued on: " + LocalDateTime.now().toLocalDate())
                    .setFontSize(12)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(30));

            // Signature section
            float[] columnWidths = {1, 1};
            Table signatureTable = new Table(columnWidths);
            signatureTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            signatureTable.setWidth(UnitValue.createPercentValue(80));

            // Instructor signature
            Cell instructorCell = new Cell()
                    .add(new Paragraph("Instructor Signature")
                            .setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("\n"))
                    .add(new Paragraph(course.getLecturer().getFirstName() + " " + course.getLecturer().getLastName())
                            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(Border.NO_BORDER);
            signatureTable.addCell(instructorCell);

            // Platform representative
            Cell platformCell = new Cell()
                    .add(new Paragraph("Platform Representative")
                            .setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("\n"))
                    .add(new Paragraph("eLearning Platform")
                            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(Border.NO_BORDER);
            signatureTable.addCell(platformCell);

            contentDiv.add(signatureTable);

            // Certificate ID
            contentDiv.add(new Paragraph("\nCertificate ID: " + certificate.getId().toString())
                    .setFontSize(10)
                    .setFontColor(new DeviceRgb(100, 100, 100))
                    .setMarginTop(20));

            centerContainer.add(contentDiv);
            document.add(centerContainer);
            document.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF certificate", e);
        }
    }

    @Override
    public List<Certificate> getCertificatesByStudentId(Long studentId) {
        return certificateRepository.findByStudentId(studentId);
    }

    @Override
    public Optional<Certificate> getCertificateByStudentAndCourse(Long studentId, Long courseId) {
        return certificateRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    public long countCertificatesByStudent(Long studentId) {
        return certificateRepository.countByStudentId(studentId);
    }

    @Override
    public long countCertificatesByCourse(Long courseId) {
        return certificateRepository.countByCourseId(courseId);
    }

    @Override
    public Certificate getCertificateById(UUID certificateId) {
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    @Override
    public byte[] getCertificateAsPng(UUID certificateId) {

        Certificate certificate = getCertificateById(certificateId);
        byte[] pdf = certificate.getCertificatePdf();

        return convertPdfToPng(pdf);
    }

    public byte[] convertPdfToPng(byte[] pdfData) {
        try (PDDocument document = PDDocument.load(pdfData)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 150, ImageType.RGB);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(bim, "png", baos);
                return baos.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error converting PDF to PNG", e);
        }
    }

}

