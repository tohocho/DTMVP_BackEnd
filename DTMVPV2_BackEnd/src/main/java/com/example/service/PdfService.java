package com.example.service;

import com.example.model.Paciente;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {
    
    public byte[] generatePdf(String title, String content) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Agregar título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titleParagraph = new Paragraph(title, titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(titleParagraph);
            
            // Agregar espacio
            document.add(new Paragraph(" "));
            
            // Agregar contenido
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph contentParagraph = new Paragraph(content, contentFont);
            document.add(contentParagraph);
            
        } finally {
            document.close();
        }
        
        return outputStream.toByteArray();
    }

    public byte[] generatePacientePdf(Paciente paciente) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Información del Paciente", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Espacio
            document.add(new Paragraph(" "));
            
            // Contenido
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            
            addField(document, "Número de Seguridad Social: ", paciente.getNumeroSeguridadSocial(), labelFont, contentFont);
            addField(document, "Número de Expediente: ", paciente.getNumeroExpediente(), labelFont, contentFont);
            addField(document, "CURP: ", paciente.getCurp(), labelFont, contentFont);
            addField(document, "Nombre Completo: ", 
                    paciente.getNombre() + " " + paciente.getPrimerApellido() + " " + paciente.getSegundoApellido(), 
                    labelFont, contentFont);
            addField(document, "Escolaridad: ", paciente.getEscolaridad(), labelFont, contentFont);
            addField(document, "Estado Civil: ", paciente.getEstadoCivil(), labelFont, contentFont);
            addField(document, "Sexo: ", paciente.getSexo(), labelFont, contentFont);
            addField(document, "Fecha de Nacimiento: ", 
                    paciente.getFechaNacimiento().format(DateTimeFormatter.ISO_LOCAL_DATE), 
                    labelFont, contentFont);
            addField(document, "Edad: ", paciente.getEdad().toString() + " años", labelFont, contentFont);
            addField(document, "Tipo de Sangre: ", paciente.getTipoSangre(), labelFont, contentFont);
            
        } finally {
            document.close();
        }
        
        return outputStream.toByteArray();
    }
    
    private void addField(Document document, String label, String value, Font labelFont, Font valueFont) 
            throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label, labelFont));
        p.add(new Chunk(value, valueFont));
        document.add(p);
    }
} 