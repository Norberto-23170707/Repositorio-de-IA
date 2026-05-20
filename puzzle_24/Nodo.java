package com.mycompany.puzzle_24;

public class Nodo {
    int[] estado;
    int vacioPos;
    Nodo padre;
    int g; // Nivel o costo acumulado
    int h; // Valor de la heurística

    public Nodo(int[] estado, Nodo padre, int g, int h) {
        this.estado = estado.clone();
        this.padre = padre;
        this.g = g;
        this.h = h;
        for (int i = 0; i < estado.length; i++) {
            if (estado[i] == 0) {
                this.vacioPos = i;
                break;
            }
        }
    }

    public int getG() {
        return g;
    }

    public int getH() {
        return h;
    }

    public void imprimirCaminoRecursivo() {
        if (padre != null) {
            padre.imprimirCaminoRecursivo();
        }
        imprimirTablero();
    }

    public void imprimirTablero() {
        for (int i = 0; i < 25; i++) {
            System.out.printf("%2d ", estado[i]);
            if ((i + 1) % 5 == 0) System.out.println();
        }
        System.out.println("Costo actual (g): " + g + " | Heuristica (h): " + h);
        System.out.println("--------------------");
    }
}