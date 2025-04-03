package com.company;

import java.util.Scanner;

public class Completer_programme {
    String carte_pioche;

    public Completer_programme(String ordre_tour[]) {
        Scanner scanner = new Scanner(System.in);
        //permet d'ajouter une carte ou plusieurs(?) a la liste en piochant


        //possibilité de défausser
        System.out.println("défausser? O ou N");
        char defausse = scanner.next().charAt(0);
        if (defausse == 'O') {
            System.out.println("vous défaussez");
            //code pour défausser
            //joueur suivant
        }
        if (defausse == 'N') {
            System.out.println("joueur suivant");
            //code pour passer au joueur suivant
            //joueur_suivant = ordre_tour.get(i+1);
        }
    }//end completer_programme
}//end class
