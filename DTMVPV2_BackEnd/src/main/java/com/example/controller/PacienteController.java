package com.example.controller;

import com.example.model.Paciente;
import com.example.model.Estudio;
import com.example.service.PacienteService;
import com.example.service.EstudioService;
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
import java.util.HashMap;
import com.fasterxml.jackson.core.type.TypeReference;

@RestController
@RequestMapping("/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private EstudioService estudioService;

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

    
    @PostMapping("/pacienteGuardar")
    public ResponseEntity<Map<String, Object>> guardarPaciente(@RequestBody Paciente paciente) {
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

        Map<String, Object> response = new HashMap<>();

        try {
            // Validar campos requeridos
            if (paciente.getCurp() == null || paciente.getNombre() == null || 
                paciente.getPrimerApellido() == null || paciente.getNumeroSeguridadSocial() == null) {
                System.out.println("Error: Faltan campos requeridos");
                response.put("success", false);
                response.put("message", "Faltan campos requeridos");
                return ResponseEntity.badRequest().body(response);
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
            response.put("success", true);
            response.put("message", "Paciente guardado exitosamente");
            response.put("paciente", paciente);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error al guardar paciente: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error al guardar el paciente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Paciente> createPaciente(@RequestBody Paciente paciente) {
        System.out.println("Método createPaciente() llamado con paciente: " + paciente);
        Paciente savedPaciente = pacienteService.savePaciente(paciente);
        return ResponseEntity.ok(savedPaciente);
    }

    @GetMapping("/encryptPaciente/{numeroSeguridadSocial}")
    public ResponseEntity<Map<String, String>> getEncryptedPaciente(@PathVariable String numeroSeguridadSocial) {
        
        System.out.println("Método getEncryptedPaciente() llamado con NSS: " + numeroSeguridadSocial);
        try {
            // Obtener paciente
            Paciente paciente = pacienteService.getPacienteByNumeroSeguridadSocial(numeroSeguridadSocial);
            if (paciente == null) {
                System.out.println("No se encontró el paciente");
                return ResponseEntity.notFound().build();
            }

            // Convertir paciente a JSON y encriptar
            String pacienteJson = objectMapper.writeValueAsString(paciente);
            String encryptedInfo = encryptionService.encrypt(pacienteJson);

            Map<String, String> response = new HashMap<>();
            response.put("encrypted_data", encryptedInfo);
            System.out.println("Encrypted Data: " + encryptedInfo);

            System.out.println("Datos encriptados exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error en el proceso de encriptación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/encryptEstudios/{numeroSeguridadSocial}")
    public ResponseEntity<Map<String, String>> getEncryptedEstudios(@PathVariable String numeroSeguridadSocial) {
        
        System.out.println("Método getEncryptedEstudios() llamado con NSS: " + numeroSeguridadSocial);
        try {
            // Obtener estudios del paciente
            List<Estudio> estudios = estudioService.getEstudiosByNumeroSeguridadSocial(numeroSeguridadSocial);
            if (estudios == null || estudios.isEmpty()) {
                System.out.println("No se encontraron estudios para el paciente");
                return ResponseEntity.notFound().build();
            }

            // Convertir lista de estudios a JSON y encriptar
            String estudiosJson = objectMapper.writeValueAsString(estudios);
            String encryptedInfo = encryptionService.encrypt(estudiosJson);

            Map<String, String> response = new HashMap<>();
            response.put("encrypted_data", encryptedInfo);
            System.out.println("Encrypted Data: " + encryptedInfo);

            System.out.println("Datos de estudios encriptados exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error en el proceso de encriptación de estudios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/decrypt")
    public ResponseEntity<Void> decryptPaciente(@RequestBody Map<String, String> request) {
        System.out.println("Método decryptPaciente() llamado");
        try {
            String encryptedData = request.get("encrypted_data");
            if (encryptedData == null) {
                System.out.println("No se proporcionaron datos encriptados");
                return ResponseEntity.badRequest().build();
            }

            String decryptedJson = encryptionService.decrypt(encryptedData);
            Paciente paciente = objectMapper.readValue(decryptedJson, Paciente.class);

            System.out.println("Datos desencriptados:");
            System.out.println("Número de Seguridad Social: " + paciente.getNumeroSeguridadSocial());
            System.out.println("Nombre: " + paciente.getNombre());
            System.out.println("Primer Apellido: " + paciente.getPrimerApellido());
            System.out.println("Segundo Apellido: " + paciente.getSegundoApellido());
            System.out.println("CURP: " + paciente.getCurp());
            
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            System.out.println("Error en el proceso de desencriptación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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