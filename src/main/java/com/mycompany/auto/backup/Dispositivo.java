/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.auto.backup;

/**
 *
 * @author Daniel
 */
public class Dispositivo {
    private String nombre;
    private String ultimaCopia;

    public Dispositivo(String nombre, String ultimaCopia) {
        this.nombre = nombre;
        this.ultimaCopia = ultimaCopia;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUltimaCopia() {
        return ultimaCopia;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUltimaCopia(String ultimaCopia) {
        this.ultimaCopia = ultimaCopia;
    }
}

