package com.example.config;

import com.example.model.Paciente;
import com.example.model.Estudio;
import com.example.repository.PacienteRepository;
import com.example.repository.EstudioRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DataInitializer {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private EstudioRepository estudioRepository;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            
            try {
                // Cargar pacientes
                ClassPathResource pacientesResource = new ClassPathResource("pacientes.json");
                InputStream pacientesStream = pacientesResource.getInputStream();
                List<Paciente> pacientes = mapper.readValue(pacientesStream, new TypeReference<List<Paciente>>() {});
                pacienteRepository.saveAll(pacientes);
                System.out.println("Base de datos inicializada con " + pacientes.size() + " pacientes");

                // Cargar estudios
                ClassPathResource estudiosResource = new ClassPathResource("estudios.json");
                InputStream estudiosStream = estudiosResource.getInputStream();
                List<Estudio> estudios = mapper.readValue(estudiosStream, new TypeReference<List<Estudio>>() {});
                estudioRepository.saveAll(estudios);
                System.out.println("Base de datos inicializada con " + estudios.size() + " estudios");
            } catch (Exception e) {
                System.err.println("Error al cargar los datos iniciales: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
} 