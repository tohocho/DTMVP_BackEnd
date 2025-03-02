package com.example.service;

import com.example.model.Estudio;
import com.example.model.Paciente;
import com.example.repository.EstudioRepository;
import com.example.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstudioService {

    @Autowired
    private EstudioRepository estudioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Estudio> getAllEstudios() {
        return estudioRepository.findAll();
    }

    public Estudio getEstudioById(String identificadorEstudio) {
        return estudioRepository.findByIdentificadorEstudio(identificadorEstudio);
    }

    public List<Estudio> getEstudiosByNumeroSeguridadSocial(String numeroSeguridadSocial) {
        return estudioRepository.findByPacienteNumeroSeguridadSocial(numeroSeguridadSocial);
    }

    public Estudio saveEstudio(Estudio estudio) {
        // Verificar que el paciente existe
        Paciente paciente = pacienteRepository.findByNumeroSeguridadSocial(
            estudio.getPaciente().getNumeroSeguridadSocial()
        );
        if (paciente == null) {
            throw new RuntimeException("El paciente no existe");
        }
        estudio.setPaciente(paciente);
        return estudioRepository.save(estudio);
    }
} 