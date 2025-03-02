package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "estudios")
public class Estudio {
    
    @Id
    private String identificadorEstudio;
    private String unidadMedicaEnvia;
    private String unidadMedicaRecibe;
    private String medicoEnvia;
    private String nombreEstudio;
    private String notasEstudio;
    private LocalDate fechaEnvio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "numero_seguridad_social")
    private Paciente paciente;

    // Constructor por defecto
    public Estudio() {
    }

    // Constructor con todos los campos
    public Estudio(String identificadorEstudio, String unidadMedicaEnvia, String unidadMedicaRecibe,
                  String medicoEnvia, String nombreEstudio, String notasEstudio, LocalDate fechaEnvio,
                  Paciente paciente) {
        this.identificadorEstudio = identificadorEstudio;
        this.unidadMedicaEnvia = unidadMedicaEnvia;
        this.unidadMedicaRecibe = unidadMedicaRecibe;
        this.medicoEnvia = medicoEnvia;
        this.nombreEstudio = nombreEstudio;
        this.notasEstudio = notasEstudio;
        this.fechaEnvio = fechaEnvio;
        this.paciente = paciente;
    }

    // Getters y Setters
    public String getIdentificadorEstudio() {
        return identificadorEstudio;
    }

    public void setIdentificadorEstudio(String identificadorEstudio) {
        this.identificadorEstudio = identificadorEstudio;
    }

    public String getUnidadMedicaEnvia() {
        return unidadMedicaEnvia;
    }

    public void setUnidadMedicaEnvia(String unidadMedicaEnvia) {
        this.unidadMedicaEnvia = unidadMedicaEnvia;
    }

    public String getUnidadMedicaRecibe() {
        return unidadMedicaRecibe;
    }

    public void setUnidadMedicaRecibe(String unidadMedicaRecibe) {
        this.unidadMedicaRecibe = unidadMedicaRecibe;
    }

    public String getMedicoEnvia() {
        return medicoEnvia;
    }

    public void setMedicoEnvia(String medicoEnvia) {
        this.medicoEnvia = medicoEnvia;
    }

    public String getNombreEstudio() {
        return nombreEstudio;
    }

    public void setNombreEstudio(String nombreEstudio) {
        this.nombreEstudio = nombreEstudio;
    }

    public String getNotasEstudio() {
        return notasEstudio;
    }

    public void setNotasEstudio(String notasEstudio) {
        this.notasEstudio = notasEstudio;
    }

    public LocalDate getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDate fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
} 