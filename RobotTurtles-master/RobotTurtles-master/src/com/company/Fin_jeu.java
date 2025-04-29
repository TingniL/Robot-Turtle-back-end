package com.company;

//à lié à la fin de completer_programme, construire_mur, executer_programme

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class Fin_jeu {
    boolean finjeu = false;

    public Fin_jeu(boolean finjeu) {
        this.finjeu = finjeu;
       /* System.out.println("test fin jeu");
        //juste pour tests, à retirer
        int nbr_joyaux = 0;
        int tour = 0;

        if (nbr_joyaux == 0) {
            System.out.println("fin du jeu");
        } else {
            tour += 1; //initialiser tour à 0 dans menu
        }

        //supprimer tortue de partie si atteind un joyaux
        //si indice tortue qui joue = indice joyaux alors gagné

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
        /*String joueur;
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
        }*/
    }

    public boolean getfin() {
        return finjeu;
    }

    public boolean isFinjeu(TreeSet<Joueur> listjoueur, Joyau joyau) {
        for (Iterator i = listjoueur.iterator(); i.hasNext(); ) {//两位玩家时，其中一位获得宝石即为结束
            Joueur joueur = (Joueur) i.next();
            if (joueur.getLocation() == joyau.getIndex()) {
                return true;
            } else return false;
        }
        return false;
    }

    public boolean isFinjeu2(TreeSet<Joueur> listjoueur, ArrayList<Joyau> listjoyau,int nbr) {
        if(nbr>2){
        for (Iterator i = listjoueur.iterator(); i.hasNext();) {//大于2位玩家时，删除获得宝石的玩家
            Joueur joueur = (Joueur) i.next();
            if (listjoyau.contains(joueur.getLocation())) {
                listjoueur.remove(joueur);//此处还没测试
                nbr--;
                return false;
            } else return false; }}
        else if (nbr==2){
            for (Iterator i = listjoueur.iterator(); i.hasNext(); ) {
                Joueur joueur = (Joueur) i.next();
                for (Iterator j = listjoyau.iterator();i.hasNext();){
                    Joyau joyau = (Joyau) j.next();
                    if (joueur.getLocation() == joyau.getIndex()) {
                    return true;} else return false;
                }
            }
        }
      return false;
    }
}
