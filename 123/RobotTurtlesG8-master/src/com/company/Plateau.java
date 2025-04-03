package com.company;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

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

    public Plateau() {

    }

    public void initialisationplateau(TreeSet<Joueur> listjoueur, Joyau joyau){

        this.setSize(864, 858);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (Iterator i = listjoueur.iterator(); i.hasNext(); ) {
            Joueur joueur = (Joueur) i.next();
            int[] indice = joueur.getLocation();
            plateau[indice[0]][indice[1]] = joueur.getName()+joueur.getDirection();
        }
        int[] index = joyau.getIndex();
        plateau[index[0]][index[1]] = joyau.getName();

        //initialisation des plateaux en fonction du nombre de joueurs
    }

    public void mettremur(int ligne,int colonne,String typemur){
        plateau[ligne][colonne]=typemur;
    }

    public String[][] getplateau(){
        return plateau;
    }

    public String getobjet(int inLigne,int inColonne){
        return plateau[inLigne][inColonne];
    }
    public void setPlateau(int indLigne,int indColonne,String ob){
        plateau[indLigne][indColonne] = ob;//metree dans un nouveau case ou nouveau direction
        //a changer dans interface graphique
    }

}
