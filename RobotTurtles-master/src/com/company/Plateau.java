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

    public void initialisationplateau(TreeSet<Joueur> listjoueur, Joyau joyau){//当只有两个玩家时，初始棋盘

        this.setSize(864, 858);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (Iterator i = listjoueur.iterator(); i.hasNext(); ) {
            Joueur joueur = (Joueur) i.next();
            int[] indice = joueur.getLocation();
            plateau[indice[0]][indice[1]] = joueur.getName()+joueur.getDirection();//在棋盘上放置玩家+玩家方向
        }
        for (int b =0;b<8;b++){
            Obstacles bois = new MurBois();
            plateau[b][7] =bois.getName(); }//两个玩家时，最右一列的棋盘全为木头障碍墙
        int[] index = joyau.getIndex();
        plateau[index[0]][index[1]] = joyau.getName();

        //initialisation des plateaux en fonction du nombre de joueurs
    }
    public void initialisationplateau3et4(TreeSet<Joueur> listjoueur, ArrayList<Joyau> joyaus,int nbr){//3-4个玩家时，宝石储存在宝石列表中

        this.setSize(864, 858);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (Iterator i = listjoueur.iterator(); i.hasNext(); ) {
            Joueur joueur = (Joueur) i.next();
            int[] indice = joueur.getLocation();
            plateau[indice[0]][indice[1]] = joueur.getName()+joueur.getDirection();
        }
        if (nbr==3){
            for (int b =0;b<8;b++){
                Obstacles bois = new MurBois();//三个玩家时，最右一列的棋盘全为木头障碍墙
                plateau[b][7] =bois.getName();
            }
        }
        for (int c=0;c<joyaus.size();c++) {
            Joyau joyau = joyaus.get(c);
            int[] index = joyau.getIndex();
            plateau[index[0]][index[1]] = joyau.getName();
        }
        //initialisation des plateaux en fonction du nombre de joueurs
    }

    public void afficheplateau(){
        for (int l=0;l<8;l++) {
            for(int c=0;c<8;c++){
                System.out.print(getobjet(l,c)+" ");}
            System.out.print("\n");
        }
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


