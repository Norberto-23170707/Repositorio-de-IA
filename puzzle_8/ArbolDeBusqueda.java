package com.mycompany.puzzle_8;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class ArbolDeBusqueda {

    Nodo raiz;
    public static int nodosExpandidos;

    public ArbolDeBusqueda(Nodo raiz) {
        this.raiz = raiz;
    }

    public Nodo busquedaPrimeroAnchura(String estadoObjetivo) {
        if (raiz == null) {
            return null;
        }
        nodosExpandidos = 0;
        HashSet<String> visitados = new HashSet<String>();
        Queue<Nodo> cola = new LinkedList<Nodo>();
        Nodo actual = null;
        cola.add(raiz);
        while (!cola.isEmpty()) {
            actual = cola.poll();
            nodosExpandidos++;
            actual.imprimirCamino();

            //Test Objetivo
            if (actual.estado.equals(estadoObjetivo)) {
                return actual;
            } else {
                visitados.add(actual.estado);
                List<Nodo> succesores = actual.generarSucesores();
                for (Nodo nodoHijo : succesores) {
                    if (!visitados.contains(nodoHijo.estado)) {
                        cola.add(nodoHijo);
                        visitados.add(nodoHijo.estado);
                    }
                }
            }
        }

        return null;

    }

    public Nodo busquedaPrimeroProfundidad(String estadoObjetivo) {
        if (raiz == null) {
            return null;
        }
        nodosExpandidos = 0;
        Stack<String> visitados = new Stack<String>();
        Stack<Nodo> cola = new Stack<Nodo>();
        Nodo actual = null;
        cola.add(raiz);
        while (!cola.isEmpty()) {
            actual = cola.pop();
            nodosExpandidos++;
            actual.imprimirCamino();

            //Test Objetivo
            if (actual.estado.equals(estadoObjetivo)) {
                return actual;
            } else {
                visitados.add(actual.estado);
                List<Nodo> succesores = actual.generarSucesores();
                for (Nodo nodoHijo : succesores) {
                    if (!visitados.contains(nodoHijo.estado)) {
                        cola.add(nodoHijo);
                        visitados.add(nodoHijo.estado);
                    }
                }
            }
        }

        return null;

    }

    public Nodo busquedaCostoUniforme(String estadoObjetivo) {
        nodosExpandidos = 0;
        Set<String> conjuntosEstados = new HashSet<String>();
        int tiempo = 0;

        Nodo nodo = new Nodo(raiz.estado, null, 0, 0, 0);
        nodo.setCosto(0);

        NodePriorityComparator comparadorPrioridad = new NodePriorityComparator();
        PriorityQueue<Nodo> colaPrioridad = new PriorityQueue<Nodo>(10, comparadorPrioridad);

        Nodo nodoActual = nodo;

        while (!nodoActual.estado.equals(estadoObjetivo)) {
            nodosExpandidos++;
            conjuntosEstados.add(nodoActual.estado);
            List<Nodo> sucesoresHijos = nodoActual.generarSucesores();

            for (Nodo hijo : sucesoresHijos) {
                if (conjuntosEstados.contains(hijo.estado)) {
                    continue;
                }

                conjuntosEstados.add(hijo.estado);
                hijo.setPadre(nodoActual);

                int costoFicha = Character.getNumericValue(hijo.estado.charAt(nodoActual.estado.indexOf('*')));
                hijo.setCostoTotal(nodoActual.costoTotal + costoFicha);

                colaPrioridad.add(hijo);
            }

            nodoActual = colaPrioridad.poll();
            tiempo += 1;

            if (nodoActual == null) {
                return null; // No se encontró solucion
            }
        }

        System.out.println("Nodos visitados: " + tiempo);
        System.out.println("Costo total: " + nodoActual.costoTotal);

        return nodoActual; // Retornamos el nodo objetivo encontrado
    }

    public Nodo busquedaAEstrella(String estadoObjetivo) {
        if (raiz == null) return null;
        nodosExpandidos = 0;
        PriorityQueue<Nodo> openSet = new PriorityQueue<>(new NodePriorityComparator());
        HashSet<String> visitados = new HashSet<>();

        // g(n) = nivel, h(n) = heuristica. f(n) = g + h
        raiz.setCostoTotal(raiz.nivel + Heuristica.calcularDiferencia4Esquinas(raiz.estado, estadoObjetivo));
        openSet.add(raiz);

        while (!openSet.isEmpty()) {
            Nodo actual = openSet.poll();
            nodosExpandidos++;

            if (actual.estado.equals(estadoObjetivo)) {
                return actual;
            }

            visitados.add(actual.estado);
            List<Nodo> sucesores = actual.generarSucesores();

            for (Nodo hijo : sucesores) {
                if (visitados.contains(hijo.estado)) continue;

                // f(n) = g(n) + h(n)
                int g = hijo.nivel;
                int h = Heuristica.calcularDiferencia4Esquinas(hijo.estado, estadoObjetivo);
                hijo.setCostoTotal(g + h);

                openSet.add(hijo);
                visitados.add(hijo.estado); // Marcamos como visitado al añadir a la cola
            }
        }
        return null;
    }
}