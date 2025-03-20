package com.example.controller;

import com.example.model.Estudio;
import com.example.service.EstudioService;
import com.example.service.EncryptionService;
import com.example.service.PdfService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.core.type.TypeReference;

@RestController
@RequestMapping("/estudios")
@CrossOrigin(origins = "*")
public class EstudioController {

    @Autowired
    private EstudioService estudioService;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Estudio>> getAllEstudios() {
        List<Estudio> estudios = estudioService.getAllEstudios();
        return ResponseEntity.ok(estudios);
    }

    @GetMapping("/{identificadorEstudio}")
    public ResponseEntity<Estudio> getEstudio(@PathVariable String identificadorEstudio) {
        Estudio estudio = estudioService.getEstudioById(identificadorEstudio);
        if (estudio != null) {
            return ResponseEntity.ok(estudio);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/paciente/{numeroSeguridadSocial}")
    public ResponseEntity<List<Estudio>> getEstudiosByPaciente(@PathVariable String numeroSeguridadSocial) {
        List<Estudio> estudios = estudioService.getEstudiosByNumeroSeguridadSocial(numeroSeguridadSocial);
        if (!estudios.isEmpty()) {
            return ResponseEntity.ok(estudios);
        } else {
            return ResponseEntity.notFound().build();
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

    @PostMapping
    public ResponseEntity<Estudio> createEstudio(@RequestBody Estudio estudio) {
        Estudio savedEstudio = estudioService.saveEstudio(estudio);
        return ResponseEntity.ok(savedEstudio);
    }
} 