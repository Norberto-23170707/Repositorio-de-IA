package com.mycompany.puzzle_8;

import java.util.Comparator;

public class NodePriorityComparator implements Comparator<Nodo> {

    @Override
    public int compare(Nodo x, Nodo y) {
        if (x.getCostoTotal() < y.getCostoTotal()) {
            return -1;
        }
        if (x.getCostoTotal() > y.getCostoTotal()) {
            return 1;
        }
        return 0;
    }
}