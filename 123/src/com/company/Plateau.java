package com.company;

import javax.swing.*;

public class Plateau extends JFrame {

    //private String plateau[][] = new String[8][8];
    //pour faire test sans interface graphique
    private String plateau[][] =
                                        {{null, null, null, null, null, null, null, null},
                                        {null, null, null, null, null, null, null, null},
                                        {null, null, null, null, null, null, null, null},
                                        {null, null, null, null, null, null, null, null},
                                        {null, null, null, null, null, null, null, null},
                                        {null, null, null, null, null, null, null, null},
                                        {null, null, null, null, null, null, null, null},
                                        {null, null, null, null, null, null, null, null}};

    public Plateau(int nbr_joueurs) {
        this.setSize(864, 858);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //initialisation des plateaux en fonction du nombre de joueurs
        if (nbr_joueurs == 2) {
            plateau[7][3] = "joyaux 1";

            for (int i = 0; i < 8; i++)
                plateau[i][7] = "mur bois";//j'imagine que les tortues ne doivent pas pouvoir le casser?

            plateau[0][1] = "tortue 1";
            plateau[0][5] = "tortue 2";

            //pour imprimer plateau sans interface graphiques (pour les tests)
            for (int m=0; m<8; m++){
                for (int n=0; n<8; n++){
                    System.out.print(plateau[m][n]);
                }
                System.out.println();
            }
        }
        if (nbr_joueurs == 3) {
            plateau[7][0] = "joyaux 2";
            plateau[7][3] = "joyaux 1";
            plateau[7][6] = "joyaux 3";

            for (int i = 0; i < 8; i++)
                plateau[i][7] = "mur bois";//ligne de mur en bois à droite

            plateau[0][0] = "tortue 1";
            plateau[0][3] = "tortue 2";
            plateau[0][6] = "tortue 3";

            //pour imprimer plateau sans interface graphiques (pour les tests)
            for (int m=0; m<8; m++){
                for (int n=0; n<8; n++){
                    System.out.print(plateau[m][n]);
                }
                System.out.println();
            }

        }
        if (nbr_joueurs == 4) {
            plateau[7][1] = "joyaux 2";
            plateau[7][6] = "joyaux 3";

            plateau[0][0] = "tortue 1";
            plateau[0][2] = "tortue 2";
            plateau[0][5] = "tortue 3";
            plateau[0][7] = "tortue 4";

            //pour imprimer plateau sans interface graphiques (pour les tests)
            for (int m=0; m<8; m++){
                for (int n=0; n<8; n++){
                    System.out.print(plateau[m][n]);
                }
                System.out.println();
            }
        }
        Action action = new Action();
    }
}
