package com.example.repository;

import com.example.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, String> {
    Paciente findByNumeroSeguridadSocial(String numeroSeguridadSocial);
} 