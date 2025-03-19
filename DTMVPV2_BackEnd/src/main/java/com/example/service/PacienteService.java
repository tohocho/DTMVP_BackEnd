package com.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.example.model.Paciente;
import com.example.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    private static final String PACIENTES_FILE = "pacientes.json";
    private final ObjectMapper objectMapper;

    public PacienteService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public List<Paciente> getAllPacientes() {
        return pacienteRepository.findAll();
    }

    public Paciente getPacienteByNumeroSeguridadSocial(String numeroSeguridadSocial) {
        return pacienteRepository.findByNumeroSeguridadSocial(numeroSeguridadSocial);
    }

    public Paciente savePaciente(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public void savePacienteToJson(Paciente paciente) throws Exception {
        try {
            // Leer el archivo JSON existente
            List<Paciente> pacientes = obtenerPacientes();

            // Agregar el nuevo paciente a la lista
            pacientes.add(paciente);

            // Escribir la lista actualizada al archivo JSON
            objectMapper.writeValue(new File(PACIENTES_FILE), pacientes);

        } catch (IOException e) {
            throw new Exception("Error al guardar el paciente en el archivo JSON: " + e.getMessage());
        }
    }

    public List<Paciente> obtenerPacientes() throws IOException {
        File file = new File(PACIENTES_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(file, 
            objectMapper.getTypeFactory().constructCollectionType(List.class, Paciente.class));
    }
} 