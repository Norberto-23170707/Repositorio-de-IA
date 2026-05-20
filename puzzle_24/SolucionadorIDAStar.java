package com.mycompany.puzzle_24;

public class SolucionadorIDAStar {
    private long nodosExpandidos = 0;
    private int[] dx = {-1, 1, 0, 0};
    private int[] dy = {0, 0, -1, 1};
    private int tipoHeuristicaSeleccionada; 

    public void ejecutar(int[] tableroInicial, int tipoHeuristica) {
        this.tipoHeuristicaSeleccionada = tipoHeuristica;
        this.nodosExpandidos = 0; 
        
        int hInicial = calcularH(tableroInicial);
        int umbral = hInicial;
        Nodo raiz = new Nodo(tableroInicial, null, 0, hInicial);
        
        long tiempoInicio = System.currentTimeMillis();
        System.out.println("Iniciando busqueda con IDA*...");
        
        while (true) {
            int resultado = buscar(raiz, umbral);
            if (resultado == -1) {
                long tiempoFin = System.currentTimeMillis();
                System.out.println("Nodos expandidos: " + nodosExpandidos);
                System.out.println("Tiempo de ejecucion: " + (tiempoFin - tiempoInicio) + " ms");
                return;
            }
            if (resultado == Integer.MAX_VALUE) {
                System.out.println("No se encontro solucion.");
                return;
            }
            umbral = resultado;
        }
    }

    private int calcularH(int[] estado) {
        if (tipoHeuristicaSeleccionada == 2) {
            return Heuristica.conflictoLineal(estado);
        }
        return Heuristica.manhattan(estado);
    }

    private int buscar(Nodo actual, int umbral) {
        int f = actual.getG() + actual.getH(); //nivel y valor de la heuristica
        if (f > umbral) return f;
        
        if (actual.getH() == 0) {
            System.out.println("Meta ubicada");
            actual.imprimirCaminoRecursivo();
            System.out.println("RESULTADOS:");
            System.out.println("Longitud de la solucion: " + actual.getG() + " movimientos");
            return -1; 
        }

        int minimo = Integer.MAX_VALUE;
        nodosExpandidos++;

        int filaV = actual.vacioPos / 5;
        int colV = actual.vacioPos % 5;

        for (int i = 0; i < 4; i++) {
            int nFila = filaV + dx[i];
            int nCol = colV + dy[i];

            if (nFila >= 0 && nFila < 5 && nCol >= 0 && nCol < 5) {
                int nuevaPos = nFila * 5 + nCol;
                if (actual.padre != null && nuevaPos == actual.padre.vacioPos) continue;

                int[] nuevoEstado = actual.estado.clone();
                nuevoEstado[actual.vacioPos] = nuevoEstado[nuevaPos];
                nuevoEstado[nuevaPos] = 0;

                Nodo hijo = new Nodo(nuevoEstado, actual, actual.getG() + 1, calcularH(nuevoEstado));
                
                int res = buscar(hijo, umbral);
                if (res == -1) return -1;
                if (res < minimo) minimo = res;
            }
        }
        return minimo;
    }
}