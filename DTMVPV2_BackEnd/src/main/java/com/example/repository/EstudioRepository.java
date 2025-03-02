package com.example.repository;

import com.example.model.Estudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EstudioRepository extends JpaRepository<Estudio, String> {
    Estudio findByIdentificadorEstudio(String identificadorEstudio);
    List<Estudio> findByPacienteNumeroSeguridadSocial(String numeroSeguridadSocial);
} 