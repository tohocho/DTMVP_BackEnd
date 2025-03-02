package com.example.controller;

import com.example.model.Estudio;
import com.example.service.EstudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/estudios")
@CrossOrigin(origins = "*")
public class EstudioController {

    @Autowired
    private EstudioService estudioService;

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

    @PostMapping
    public ResponseEntity<Estudio> createEstudio(@RequestBody Estudio estudio) {
        Estudio savedEstudio = estudioService.saveEstudio(estudio);
        return ResponseEntity.ok(savedEstudio);
    }
} 