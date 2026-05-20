package com.mycompany.puzzle_8;

import java.util.ArrayList;
import java.util.List;

public class Nodo {
    String estado;
    Nodo padre;
    int nivel;
    int costo;
    int costoTotal;

    public Nodo(String estado, Nodo padre, int nivel, int costo, int costoTotal) {
        this.estado = estado;
        this.padre = padre;
        this.nivel = nivel;
        this.costo = costo;
        this.costoTotal = costoTotal;
    }

    public int getCosto() {
        return costo;
    }

    public int getCostoTotal() {
        return costoTotal;
    }
    
    public String getEstado() {
        return estado; 
    }
    
    public void setPadre(Nodo padre) {
        this.padre = padre; 
    }
    public void setCosto(int costo) {
        this.costo = costo; 
    }
    public void setCostoTotal(int costoTotal) {
        this.costoTotal = costoTotal; 
    }

    public void imprimirCamino() {
        if (this.padre != null) {
            this.padre.imprimirCamino();
        }

        for (int i = 0; i < 9; i++) {
            System.out.print(this.estado.charAt(i) + " ");
            if ((i + 1) % 3 == 0) {
                System.out.println();
            }
        }

        System.out.println("Nivel: " + this.nivel);
        System.out.println("--------------------");
    }

    public List<Nodo> generarSucesores() {
        String[] estadosHijos = Puzzle8.generarSucesores(this.estado);
        List<Nodo> hijos = new ArrayList<Nodo>();

        if (estadosHijos != null) {
            for (String estadoHijo : estadosHijos) {
                if (estadoHijo != null) {
                    Nodo nodoHijo = new Nodo(estadoHijo, this, this.nivel + 1, 0, 0);
                    hijos.add(nodoHijo);
                }
            }
        }
        return hijos;
    }

}