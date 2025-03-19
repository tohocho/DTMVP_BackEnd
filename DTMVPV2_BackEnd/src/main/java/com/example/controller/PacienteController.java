package com.example.controller;

import com.example.model.Paciente;
import com.example.service.PacienteService;
import com.example.service.EncryptionService;
import com.example.service.PdfService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Paciente>> getAllPacientes() {
        List<Paciente> pacientes = pacienteService.getAllPacientes();
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/{numeroSeguridadSocial}")
    public ResponseEntity<Paciente> getPaciente(@PathVariable String numeroSeguridadSocial) {
        Paciente paciente = pacienteService.getPacienteByNumeroSeguridadSocial(numeroSeguridadSocial);
        if (paciente != null) {
            return ResponseEntity.ok(paciente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/json")
    public ResponseEntity<List<Paciente>> getPacientesFromJson() {
        try {
            List<Paciente> pacientes = pacienteService.obtenerPacientes();
            return ResponseEntity.ok(pacientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/guardar")
    public ResponseEntity<String> guardarPaciente(@RequestBody Paciente paciente) {
        try {
            // Validar campos requeridos
            if (paciente.getCurp() == null || paciente.getNombre() == null || 
                paciente.getPrimerApellido() == null || paciente.getNumeroSeguridadSocial() == null) {
                return ResponseEntity.badRequest().body("Faltan campos requeridos");
            }

            // Guardar en base de datos
            Paciente savedPaciente = pacienteService.savePaciente(paciente);

            // Guardar en archivo JSON
            pacienteService.savePacienteToJson(savedPaciente);

            return ResponseEntity.ok("Paciente guardado exitosamente");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al guardar el paciente: " + e.getMessage());
        }
    }
    @PostMapping("/pacienteGuardar")
    public ResponseEntity<String> savePacienteToJson(@RequestBody Paciente paciente) {
        try {
            // Validar que el paciente no sea nulo
            if (paciente == null) {
                return ResponseEntity
                    .badRequest()
                    .body("El paciente no puede ser nulo");
            }

            // Intentar guardar el paciente en el archivo JSON
            pacienteService.savePacienteToJson(paciente);

            return ResponseEntity
                .ok()
                .body("Paciente guardado exitosamente en el archivo JSON");

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al guardar el paciente en el archivo JSON: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Paciente> createPaciente(@RequestBody Paciente paciente) {
        Paciente savedPaciente = pacienteService.savePaciente(paciente);
        return ResponseEntity.ok(savedPaciente);
    }

    @GetMapping("/encrypted-info/{numeroSeguridadSocial}")
    public ResponseEntity<byte[]> getPacienteInfoEncrypted(@PathVariable String numeroSeguridadSocial) {
        try {
            // Obtener paciente
            Paciente paciente = pacienteService.getPacienteByNumeroSeguridadSocial(numeroSeguridadSocial);
            if (paciente == null) {
                return ResponseEntity.notFound().build();
            }

            // Convertir paciente a JSON y encriptar
            String pacienteJson = objectMapper.writeValueAsString(paciente);
            String encryptedInfo;
            try {
                encryptedInfo = encryptionService.encrypt(pacienteJson);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al encriptar los datos: " + e.getMessage()).getBytes());
            }

            // Generar PDF
            byte[] pdfContent = pdfService.generatePdf(
                "Información Encriptada del Paciente",
                "Información encriptada: " + encryptedInfo
            );

            // Configurar headers para la descarga del PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "paciente_" + numeroSeguridadSocial + "_encrypted.pdf");

            return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfContent);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error en el proceso: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/info-pdf/{numeroSeguridadSocial}")
    public ResponseEntity<byte[]> getPacienteInfoNotEncrypted(@PathVariable String numeroSeguridadSocial) {
        try {
            // Obtener paciente
            Paciente paciente = pacienteService.getPacienteByNumeroSeguridadSocial(numeroSeguridadSocial);
            if (paciente == null) {
                return ResponseEntity.notFound().build();
            }

            // Generar PDF con la información del paciente
            byte[] pdfContent = pdfService.generatePacientePdf(paciente);

            // Configurar headers para la descarga del PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "Paciente_" + paciente.getNombre() + "_" + numeroSeguridadSocial + ".pdf");

            return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfContent);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 