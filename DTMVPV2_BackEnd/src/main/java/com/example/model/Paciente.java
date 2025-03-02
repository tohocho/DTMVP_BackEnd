package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "pacientes")
public class Paciente {
    
    @Id
    private String numeroSeguridadSocial;
    private String numeroExpediente;
    private String curp;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String escolaridad;
    private String estadoCivil;
    private String sexo;
    private LocalDate fechaNacimiento;
    @Transient
    private Integer edad;
    private String tipoSangre;

    // Constructor por defecto
    public Paciente() {
        calcularEdad();
    }

    // Constructor con todos los campos
    public Paciente(String numeroSeguridadSocial, String numeroExpediente, String curp, 
                   String nombre, String primerApellido, String segundoApellido,
                   String escolaridad, String estadoCivil, String sexo, 
                   LocalDate fechaNacimiento, String tipoSangre) {
        this.numeroSeguridadSocial = numeroSeguridadSocial;
        this.numeroExpediente = numeroExpediente;
        this.curp = curp;
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.escolaridad = escolaridad;
        this.estadoCivil = estadoCivil;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.tipoSangre = tipoSangre;
        calcularEdad();
    }

    // Getters y Setters
    public String getNumeroSeguridadSocial() {
        return numeroSeguridadSocial;
    }

    public void setNumeroSeguridadSocial(String numeroSeguridadSocial) {
        this.numeroSeguridadSocial = numeroSeguridadSocial;
    }

    public String getNumeroExpediente() {
        return numeroExpediente;
    }

    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getEscolaridad() {
        return escolaridad;
    }

    public void setEscolaridad(String escolaridad) {
        this.escolaridad = escolaridad;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
        calcularEdad();
    }

    public Integer getEdad() {
        calcularEdad();
        return edad;
    }

    public String getTipoSangre() {
        return tipoSangre;
    }

    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre = tipoSangre;
    }

    // Método para calcular la edad
    private void calcularEdad() {
        if (fechaNacimiento != null) {
            this.edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        }
    }
} 