package com.mycompany.puzzle_8;

public class Puzzle8 {
    

    String estadoInicial = "1238*4765";

    String estadoFinal = "12345678*";


    static String[] generarSucesores(String estadoActual) {

        String[] hijo = new String[4];

        int indice = estadoActual.indexOf("*");


        switch (indice) {

            case 0:
                /* Estado actual:
                   * 2 3
                   8 4 5
                   7 6 1
                */
                //cambio con el 2
                hijo[0] = estadoActual.substring(1, 2) + estadoActual.charAt(0) +
                          estadoActual.substring(2);
               //cambio con el 8
                hijo[1] = estadoActual.substring(3, 4) + estadoActual.substring(1, 3) +
                          estadoActual.charAt(0) + estadoActual.substring(4);
                break;

            case 1:
                /* Estado actual:
                   1 * 3
                   8 4 5
                   7 6 2
                */
                //cambio con el 1
                hijo[0] = estadoActual.substring(1, 2) + estadoActual.charAt(0) +
                          estadoActual.substring(2);
                //cambio con el 3
                hijo[1] = estadoActual.substring(0, 1) + estadoActual.charAt(2) +

                          estadoActual.charAt(1) + estadoActual.substring(3);
                //cambio con el 4
                hijo[2] = estadoActual.substring(0, 1) + estadoActual.charAt(4) +
                          estadoActual.charAt(2) + estadoActual.substring(3, 4) +
                          estadoActual.charAt(1) + estadoActual.substring(5);
                break;

            case 2:
                /* Estado actual:
                   1 2 *
                   8 4 5
                   7 6 3
                */
                //cambio con el 2
                hijo[0] = estadoActual.substring(0, 1) + estadoActual.charAt(2) +
                          estadoActual.charAt(1) + estadoActual.substring(3);
                //cambio con el 5
                hijo[1] = estadoActual.substring(0, 2) + estadoActual.charAt(5) +
                          estadoActual.substring(3, 5) + estadoActual.charAt(2) +
                          estadoActual.substring(6);
                break;

            case 3:
                /* Estado actual:
                   1 2 3
                   * 4 5
                   7 6 8
                */
                //cambio con el 1
                hijo[0] = estadoActual.substring(3, 4) + estadoActual.substring(1, 3) +
                          estadoActual.charAt(0) + estadoActual.substring(4);
                //cambio con el 4
                hijo[1] = estadoActual.substring(0, 3) + estadoActual.charAt(4) +
                          estadoActual.charAt(3) + estadoActual.substring(5);
                //cambio con el 7
                hijo[2] = estadoActual.substring(0, 3) + estadoActual.charAt(6) +
                          estadoActual.substring(4, 6) + estadoActual.charAt(3) +
                          estadoActual.substring(7);
                break;

            case 4:
                /* Estado actual:
                   1 2 3
                   8 * 4
                   7 6 5
                */
                //cambio con el 2
                hijo[0] = estadoActual.substring(0, 1) + estadoActual.charAt(4) +
                          estadoActual.substring(2, 4) + estadoActual.charAt(1) +
                          estadoActual.substring(5);
                //cambio con el 8
                hijo[1] = estadoActual.substring(0, 4) + estadoActual.charAt(5) +
                          estadoActual.charAt(4) + estadoActual.substring(6);
                //cambio con el 6
                hijo[2] = estadoActual.substring(0, 4) + estadoActual.charAt(7) +
                          estadoActual.substring(5, 7) + estadoActual.charAt(4) +
                          estadoActual.substring(8);
                //cambio con el 4
                hijo[3] = estadoActual.substring(0, 3) + estadoActual.charAt(4) +
                          estadoActual.charAt(3) + estadoActual.substring(5);
                break;

            case 5:
                /* Estado actual:
                   1 2 3
                   8 4 *
                   7 6 5
                */
                //cambio con el 3
                hijo[0] = estadoActual.substring(0, 2) + estadoActual.charAt(5) +
                          estadoActual.substring(3, 5) + estadoActual.charAt(2) +
                          estadoActual.substring(6);
                //cambio con el 4
                hijo[1] = estadoActual.substring(0, 4) + estadoActual.charAt(5) +
                          estadoActual.charAt(4) + estadoActual.substring(6);
                //cambio con el 5
                hijo[2] = estadoActual.substring(0, 5) + estadoActual.charAt(8) +
                          estadoActual.substring(6, 8) + estadoActual.charAt(5);
                break;

            case 6:
                /* Estado actual:
                   1 2 3
                   8 4 5
                   * 6 7
                */
                //cambio con el 8
                hijo[0] = estadoActual.substring(0, 3) + estadoActual.charAt(6) +
                          estadoActual.substring(4, 6) + estadoActual.charAt(3) +
                          estadoActual.substring(7);
                //cambio con el 6
                hijo[1] = estadoActual.substring(0, 6) + estadoActual.charAt(7) +
                          estadoActual.charAt(6) + estadoActual.substring(8);
                break;

            case 7:
                /* Estado actual:
                   1 2 3
                   8 4 5
                   7 * 6
                */
                //cambio con el 7
                hijo[0] = estadoActual.substring(0, 6) + estadoActual.charAt(7) +
                          estadoActual.charAt(6) + estadoActual.substring(8);
                //cambio con el 6
                hijo[1] = estadoActual.substring(0, 7) + estadoActual.charAt(8) +
                          estadoActual.charAt(7);
                //cambio con el 4
                hijo[2] = estadoActual.substring(0, 4) + estadoActual.charAt(7) +
                          estadoActual.substring(5, 7) + estadoActual.charAt(4) +
                          estadoActual.substring(8);
                break;

            case 8:
                /* Estado actual:
                   1 2 3
                   8 4 5
                   7 6 *
                */
                //cambio con el 5
                hijo[0] = estadoActual.substring(0, 5) + estadoActual.charAt(8) +
                          estadoActual.substring(6, 8) + estadoActual.charAt(5);
                //cambio con el 6
                hijo[1] = estadoActual.substring(0, 7) + estadoActual.charAt(8) +
                          estadoActual.charAt(7);
                break;
        }

        return hijo;

    }

}
