package com.example.service;

import com.example.model.Paciente;
import com.example.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Paciente> getAllPacientes() {
        return pacienteRepository.findAll();
    }

    public Paciente getPacienteByNumeroSeguridadSocial(String numeroSeguridadSocial) {
        return pacienteRepository.findByNumeroSeguridadSocial(numeroSeguridadSocial);
    }

    public Paciente savePaciente(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }
} 