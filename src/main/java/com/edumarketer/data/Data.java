package com.edumarketer.data;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

import java.io.Serializable;

/**
 * Created by fjcano on 16/06/2017.
 */
@CsvDataType()
public class Data implements Serializable {

    @CsvField(pos = 1)
    String n;
    @CsvField(pos = 2)
    String apellidos;
    @CsvField(pos = 3)
    String nombre;
    @CsvField(pos = 4)
    String nif;
    @CsvField(pos = 5)
    String telefono;
    @CsvField(pos = 6)
    String email;
    @CsvField(pos = 7)
    String primeraConexion;
    @CsvField(pos = 8)
    String ultimaConexion;
    @CsvField(pos = 9)
    String porcentajeTotalRealizado;
    @CsvField(pos = 10)
    String observaciones;

    public Data(){

    }

    public Data(String n, String apellidos, String nombre, String nif, String telefono, String email, String primeraConexion, String ultimaConexion, String porcentajeTotalRealizado, String observaciones) {
        this.n = n;
        this.apellidos = apellidos;
        this.nombre = nombre;
        this.nif = nif;
        this.telefono = telefono;
        this.email = email;
        this.primeraConexion = primeraConexion;
        this.ultimaConexion = ultimaConexion;
        this.porcentajeTotalRealizado = porcentajeTotalRealizado;
        this.observaciones = observaciones;
    }

    public String getN() {
        return n;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNif() {
        return nif;
    }

    public String getTelefono() {
        return telefono == null ? "" : telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getPrimeraConexion() {
        return primeraConexion;
    }

    public String getUltimaConexion() {
        return ultimaConexion;
    }

    public String getPorcentajeTotalRealizado() {
        return porcentajeTotalRealizado;
    }

    public String getObservaciones() {
        return observaciones == null ? "" : "ll";
    }

    @Override
    public String toString() {
        return "Data{" +
                "n='" + n + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", nombre='" + nombre + '\'' +
                ", nif='" + nif + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", primeraConexion='" + primeraConexion + '\'' +
                ", ultimaConexion='" + ultimaConexion + '\'' +
                ", porcentajeTotalRealizado='" + porcentajeTotalRealizado + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}