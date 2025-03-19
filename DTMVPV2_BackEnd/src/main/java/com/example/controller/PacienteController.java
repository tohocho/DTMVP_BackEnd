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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;

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
        System.out.println("Método getAllPacientes() llamado");
        List<Paciente> pacientes = pacienteService.getAllPacientes();
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/{numeroSeguridadSocial}")
    public ResponseEntity<Paciente> getPaciente(@PathVariable String numeroSeguridadSocial) {
        System.out.println("Método getPaciente() llamado con NSS: " + numeroSeguridadSocial);
        Paciente paciente = pacienteService.getPacienteByNumeroSeguridadSocial(numeroSeguridadSocial);
        if (paciente != null) {
            System.out.println("Paciente encontrado: " + paciente);
            return ResponseEntity.ok(paciente);
        } else {
            System.out.println("No se encontró paciente con NSS: " + numeroSeguridadSocial);
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/json")
    public ResponseEntity<List<Paciente>> getPacientesFromJson() {
        System.out.println("Método getPacientesFromJson() llamado");
        try {
            List<Paciente> pacientes = pacienteService.obtenerPacientes();
            return ResponseEntity.ok(pacientes);
        } catch (Exception e) {
            System.out.println("Error en getPacientesFromJson(): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/guardar")
    public ResponseEntity<String> guardarPaciente(@RequestBody Paciente paciente) {
        // Imprimir información del paciente recibido
        System.out.println("Datos del paciente recibido:");
        System.out.println("Número de Seguridad Social: " + paciente.getNumeroSeguridadSocial());
        System.out.println("CURP: " + paciente.getCurp());
        System.out.println("Nombre: " + paciente.getNombre());
        System.out.println("Primer Apellido: " + paciente.getPrimerApellido());
        System.out.println("Segundo Apellido: " + paciente.getSegundoApellido());
        System.out.println("Escolaridad: " + paciente.getEscolaridad());
        System.out.println("Estado Civil: " + paciente.getEstadoCivil());
        System.out.println("Sexo: " + paciente.getSexo());
        System.out.println("Fecha de Nacimiento: " + paciente.getFechaNacimiento());
        System.out.println("Tipo de Sangre: " + paciente.getTipoSangre());
        System.out.println("Método guardarPaciente() llamado con paciente: " + paciente);
        try {
            // Validar campos requeridos
            if (paciente.getCurp() == null || paciente.getNombre() == null || 
                paciente.getPrimerApellido() == null || paciente.getNumeroSeguridadSocial() == null) {
                System.out.println("Error: Faltan campos requeridos");
                return ResponseEntity.badRequest().body("Faltan campos requeridos");
            }

            // Leer pacientes existentes del archivo
            List<Paciente> pacientes = new ArrayList<>();
            File file = new File("src/main/resources/pacientes.json");
            if (file.exists()) {
                pacientes = objectMapper.readValue(file, new TypeReference<List<Paciente>>() {});
            }

            // Agregar nuevo paciente
            pacientes.add(paciente);

            // Guardar lista actualizada en el archivo
            objectMapper.writeValue(file, pacientes);

            System.out.println("Paciente guardado exitosamente");
            return ResponseEntity.ok("Paciente guardado exitosamente");

        } catch (Exception e) {
            System.out.println("Error al guardar paciente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al guardar el paciente: " + e.getMessage());
        }
    }
    @PostMapping("/pacienteGuardar")
    public ResponseEntity<String> savePacienteToJson(@RequestBody Paciente paciente) {
        System.out.println("Método savePacienteToJson() llamado con paciente: " + paciente);
        try {
            // Validar que el paciente no sea nulo
            if (paciente == null) {
                System.out.println("Error: El paciente es nulo");
                return ResponseEntity
                    .badRequest()
                    .body("El paciente no puede ser nulo");
            }

            // Intentar guardar el paciente en el archivo JSON
            pacienteService.savePacienteToJson(paciente);

            System.out.println("Paciente guardado exitosamente en JSON");
            return ResponseEntity
                .ok()
                .body("Paciente guardado exitosamente en el archivo JSON");

        } catch (Exception e) {
            System.out.println("Error al guardar paciente en JSON: " + e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al guardar el paciente en el archivo JSON: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Paciente> createPaciente(@RequestBody Paciente paciente) {
        System.out.println("Método createPaciente() llamado con paciente: " + paciente);
        Paciente savedPaciente = pacienteService.savePaciente(paciente);
        return ResponseEntity.ok(savedPaciente);
    }

    @GetMapping("/encrypted-info/{numeroSeguridadSocial}")
    public ResponseEntity<byte[]> getPacienteInfoEncrypted(@PathVariable String numeroSeguridadSocial) {
        System.out.println("Método getPacienteInfoEncrypted() llamado con NSS: " + numeroSeguridadSocial);
        try {
            // Obtener paciente
            Paciente paciente = pacienteService.getPacienteByNumeroSeguridadSocial(numeroSeguridadSocial);
            if (paciente == null) {
                System.out.println("No se encontró el paciente");
                return ResponseEntity.notFound().build();
            }

            // Convertir paciente a JSON y encriptar
            String pacienteJson = objectMapper.writeValueAsString(paciente);
            String encryptedInfo;
            try {
                encryptedInfo = encryptionService.encrypt(pacienteJson);
            } catch (Exception e) {
                System.out.println("Error al encriptar datos: " + e.getMessage());
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

            System.out.println("PDF generado exitosamente");
            return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfContent);

        } catch (Exception e) {
            System.out.println("Error en el proceso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error en el proceso: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/info-pdf/{numeroSeguridadSocial}")
    public ResponseEntity<byte[]> getPacienteInfoNotEncrypted(@PathVariable String numeroSeguridadSocial) {
        System.out.println("Método getPacienteInfoNotEncrypted() llamado con NSS: " + numeroSeguridadSocial);
        try {
            // Obtener paciente
            Paciente paciente = pacienteService.getPacienteByNumeroSeguridadSocial(numeroSeguridadSocial);
            if (paciente == null) {
                System.out.println("No se encontró el paciente");
                return ResponseEntity.notFound().build();
            }

            // Generar PDF con la información del paciente
            byte[] pdfContent = pdfService.generatePacientePdf(paciente);

            // Configurar headers para la descarga del PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "Paciente_" + paciente.getNombre() + "_" + numeroSeguridadSocial + ".pdf");

            System.out.println("PDF generado exitosamente");
            return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfContent);

        } catch (Exception e) {
            System.out.println("Error al generar PDF: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/json")
    public ResponseEntity<Map<String, String>> guardarPacienteEnJson(@RequestBody Paciente paciente) {
        try {
            // Leer el archivo JSON existente
            File jsonFile = new File("pacientes.json");
            List<Paciente> pacientes = new ArrayList<>();

            if (jsonFile.exists()) {
                // Si el archivo existe, lee la lista actual de pacientes
                pacientes = objectMapper.readValue(jsonFile, 
                    new TypeReference<List<Paciente>>() {});
            }

            // Agregar el nuevo paciente a la lista
            pacientes.add(paciente);

            // Escribir la lista actualizada al archivo JSON
            objectMapper.writeValue(jsonFile, pacientes);

            return ResponseEntity.ok(Map.of(
                "estado", "exitoso",
                "mensaje", "Paciente guardado correctamente en el archivo JSON"
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "estado", "error",
                "mensaje", "Error al guardar el paciente en el archivo JSON: " + e.getMessage()
            ));
        }
    }
} 