package com.mycompany.puzzle_24;

public class Heuristica {

    // Método 1: Manhattan 
    public static int manhattan(int[] estado) {
        int dist = 0;
        for (int i = 0; i < 25; i++) {
            int valor = estado[i];
            if (valor != 0) {
                int filaActual = i / 5;
                int colActual = i % 5;
                int filaDestino = (valor - 1) / 5;
                int colDestino = (valor - 1) % 5;
                dist += Math.abs(filaActual - filaDestino) + Math.abs(colActual - colDestino);
            }
        }
        return dist;
    }

    // Método 2: Manhattan + Conflicto Lineal 
    public static int conflictoLineal(int[] estado) {
        int h = manhattan(estado);
        int conflicto = 0;

        // Conflicto en Filas
        for (int fila = 0; fila < 5; fila++) {
            for (int i = 0; i < 5; i++) {
                for (int j = i + 1; j < 5; j++) {
                    int posI = fila * 5 + i;
                    int posJ = fila * 5 + j;
                    int valI = estado[posI];
                    int valJ = estado[posJ];

                    if (valI != 0 && valJ != 0) {
                        // ¿Ambos pertenecen a esta misma fila?
                        if ((valI - 1) / 5 == fila && (valJ - 1) / 5 == fila) {
                            // ¿Están en el orden inverso?
                            if (valI > valJ) {
                                conflicto += 2;
                            }
                        }
                    }
                }
            }
        }

        // Conflicto en Columnas
        for (int col = 0; col < 5; col++) {
            for (int i = 0; i < 5; i++) {
                for (int j = i + 1; j < 5; j++) {
                    int posI = i * 5 + col;
                    int posJ = j * 5 + col;
                    int valI = estado[posI];
                    int valJ = estado[posJ];

                    if (valI != 0 && valJ != 0) {
                        // ¿Ambos pertenecen a esta misma columna?
                        if ((valI - 1) % 5 == col && (valJ - 1) % 5 == col) {
                            // ¿Están en el orden inverso?
                            if (valI > valJ) {
                                conflicto += 2;
                            }
                        }
                    }
                }
            }
        }

        return h + conflicto;
    }
}