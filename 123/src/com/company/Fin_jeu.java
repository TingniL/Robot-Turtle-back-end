package com.company;

//à lié à la fin de completer_programme, construire_mur, executer_programme

public class Fin_jeu {
    public Fin_jeu(int nbr_joueurs, String[] ordre_tours){
        System.out.println("test fin jeu");
        //juste pour tests, à retirer
        int nbr_joyaux = 0;
        int tour = 0;

        if (nbr_joyaux == 0) {
            System.out.println("fin du jeu");
        }else {
            tour += 1; //initialiser tour à 0 dans menu
        }

        //supprimer tortue de partie si atteind un joyaux
        //si indice tortue qui joue = indice joyaux alors gagné
        /*
        var tortue = plateau.indexOf("tortue");//mettre le nom de la tortue concernée, importer plateau
        var joy = plateau.indexOf("joyau");
        if (tortue == joy){
            System.out.println("la tortue a gagné");
            //retirer la tortue et le joyau
        }
        else{
            tour+=1;
        }
         */

        //joueur suivant selon nombre de joueurs
        String joueur;
        if (nbr_joueurs ==2){
            joueur = ordre_tours[tour%2];
            System.out.println("joueur suivant : "+joueur);
        }
        if (nbr_joueurs == 3){
            joueur = ordre_tours[tour%3];
            System.out.println("joueur suivant : "+joueur);
        }
        if (nbr_joueurs == 4){
            joueur = ordre_tours[tour%4];
            System.out.println("joueur suivant : "+joueur);
        }


    }
}
