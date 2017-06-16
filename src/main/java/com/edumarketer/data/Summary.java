package com.edumarketer.data;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

import java.io.Serializable;

/**
 * Created by fjcano on 16/06/2017.
 */
@CsvDataType()
public class Summary implements Serializable {

    @CsvField(pos = 1)
    String informeCurso;
    @CsvField(pos = 2)
    String solicitante;
    @CsvField(pos = 3)
    String fechaInicio;
    @CsvField(pos = 4)
    String expediente;
    @CsvField(pos = 5)
    String fechaFinal;
    @CsvField(pos = 6)
    String accion;
    @CsvField(pos = 7)
    String alumnosIniciales;
    @CsvField(pos = 8)
    String grupo;
    @CsvField(pos = 9)
    String alumnosActuales;
    @CsvField(pos = 10)
    String formador;

    public Summary(){

    }

    public Summary(String informeCurso, String solicitante, String fechaInicio, String expediente, String fechaFinal, String accion, String alumnosIniciales, String grupo, String alumnosActuales, String formador) {
        this.informeCurso = informeCurso;
        this.solicitante = solicitante;
        this.fechaInicio = fechaInicio;
        this.expediente = expediente;
        this.fechaFinal = fechaFinal;
        this.accion = accion;
        this.alumnosIniciales = alumnosIniciales;
        this.grupo = grupo;
        this.alumnosActuales = alumnosActuales;
        this.formador = formador;
    }

    public String getInformeCurso() {
        return informeCurso;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getExpediente() {
        return expediente;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public String getAccion() {
        return accion;
    }

    public String getAlumnosIniciales() {
        return alumnosIniciales;
    }

    public String getGrupo() {
        return grupo;
    }

    public String getAlumnosActuales() {
        return alumnosActuales;
    }

    public String getFormador() {
        return formador;
    }

    @Override
    public String toString() {
        return "Summary{" +
                "informeCurso='" + informeCurso + '\'' +
                ", solicitante='" + solicitante + '\'' +
                ", fechaInicio='" + fechaInicio + '\'' +
                ", expediente='" + expediente + '\'' +
                ", fechaFinal='" + fechaFinal + '\'' +
                ", accion='" + accion + '\'' +
                ", alumnosIniciales='" + alumnosIniciales + '\'' +
                ", grupo='" + grupo + '\'' +
                ", alumnosActuales='" + alumnosActuales + '\'' +
                ", formador='" + formador + '\'' +
                '}';
    }
}