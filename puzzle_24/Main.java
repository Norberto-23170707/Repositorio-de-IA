package com.mycompany.puzzle_24;

import java.util.Scanner;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[] tableroObjetivo = {
            1, 2, 3, 4, 5,
            6, 7, 8, 9, 10,
            11, 12, 13, 14, 15,
            16, 17, 18, 19, 20,
            21, 22, 23, 24, 0
        };

        int[] tableroInicial;

        System.out.println("=== 24 PUZZLE (5x5) ===");
        System.out.println("1. Usar tablero predeterminado");
        System.out.println("2. Generar estado aleatorio");
        System.out.print("Seleccione 1 o 2: ");
        int seleccionEstado = sc.nextInt();

        if (seleccionEstado == 1) {
            tableroInicial = new int[]{
                2,  1,  3,  4,  5,
                11, 7,  8,  9,  10,
                6,  12, 13, 14, 15,
                16, 17, 18, 19, 20,
                21, 22, 23, 24, 0
            };
        } else {
            tableroInicial = generarAleatorio(tableroObjetivo, 20);
        }

        System.out.println("\n--- ESTADO INICIAL ---");
        imprimirEstado(tableroInicial);

        System.out.println("Seleccione la Heuristica:");
        System.out.println("1. Distancia de Manhattan");
        System.out.println("2. Conflicto Lineal");
        System.out.print("Opcion: ");
        int opcionH = sc.nextInt();

        SolucionadorIDAStar solucionador = new SolucionadorIDAStar();
        solucionador.ejecutar(tableroInicial, opcionH);
    }


    public static void imprimirEstado(int[] estado) {
        for (int i = 0; i < 25; i++) {
            if (estado[i] == 0) System.out.print(" * ");
            else System.out.printf("%2d ", estado[i]);
            if ((i + 1) % 5 == 0) System.out.println();
        }
        System.out.println("----------------------------");
    }
    
    public static int[] generarAleatorio(int[] objetivo, int movimientos) {
        int[] copia = objetivo.clone();
        int vacioPos = 24;
        Random random = new Random();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        int m = 0;
        while (m < movimientos) {
            int dir = random.nextInt(4);
            int filaV = vacioPos / 5;
            int colV = vacioPos % 5;
            int nFila = filaV + dx[dir];
            int nCol = colV + dy[dir];

            if (nFila >= 0 && nFila < 5 && nCol >= 0 && nCol < 5) {
                int nuevaPos = nFila * 5 + nCol;
                copia[vacioPos] = copia[nuevaPos];
                copia[nuevaPos] = 0;
                vacioPos = nuevaPos;
                m++;
            }
        }
        return copia;
    }
}