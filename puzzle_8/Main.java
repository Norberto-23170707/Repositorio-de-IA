package com.mycompany.puzzle_8;

public class Main {

    public static void main(String[] args) {
        String estadoInicial = "1234*6758"; 
        String estadoObjetivo = "12345678*";

        Nodo raiz = new Nodo(estadoInicial, null, 0, 0, 0);
        ArbolDeBusqueda arbol = new ArbolDeBusqueda(raiz);

        double tAnchura = medirTiempo(() -> arbol.busquedaPrimeroAnchura(estadoObjetivo));
        int nAnchura = ArbolDeBusqueda.nodosExpandidos;

        double tProfundidad = medirTiempo(() -> arbol.busquedaPrimeroProfundidad(estadoObjetivo));
        int nProfundidad = ArbolDeBusqueda.nodosExpandidos;

        double tCostoUniforme = medirTiempo(() -> arbol.busquedaCostoUniforme(estadoObjetivo));
        int nCostoUniforme = ArbolDeBusqueda.nodosExpandidos;

        double tAEstrella = medirTiempo(() -> arbol.busquedaAEstrella(estadoObjetivo));
        int nAEstrella = ArbolDeBusqueda.nodosExpandidos;

        // IMPRESION DE TABLA COMPARATIVA
        System.out.println("\n==========================================================================");
        System.out.println("              TABLA COMPARATIVA DE ALGORITMOS 8PUZZLE                    ");
        System.out.println("==========================================================================");
        System.out.printf("%-22s | %-15s | %-15s%n", "Algoritmo", "Tiempo (seg)", "Nodos Expandidos");
        System.out.println("--------------------------------------------------------------------------");
        
        System.out.printf("%-22s | %-15.5f | %-15d%n", "Busqueda Anchura", tAnchura, nAnchura);
        System.out.printf("%-22s | %-15.5f | %-15d%n", "Busqueda Profundidad", tProfundidad, nProfundidad);
        System.out.printf("%-22s | %-15.5f | %-15d%n", "Costo Uniforme", tCostoUniforme, nCostoUniforme);
        System.out.printf("%-22s | %-15.5f | %-15d%n", "A*", tAEstrella, nAEstrella);
        
        System.out.println("==========================================================================");
    }

    private static double medirTiempo(Runnable algoritmo) {
        long inicio = System.nanoTime();
        algoritmo.run();
        return (System.nanoTime() - inicio) / 1_000_000_000.0;
    }
}