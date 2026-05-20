package com.mycompany.puzzle_8;

public class Heuristica {

    public static int calcularDiferencia4Esquinas(String estadoActual, String estadoObjetivo) {
        int diferencias = 0;
        int[] esquinas = {0, 2, 6, 8};

        for (int i : esquinas) {
            char fichaActual = estadoActual.charAt(i);
            char fichaObjetivo = estadoObjetivo.charAt(i);

            // Solo contamos si la posición no está vacía y las fichas son diferentes
            if (fichaActual != '*' && fichaActual != fichaObjetivo) {
                diferencias++;
            }
        }

        return diferencias;
    }
}