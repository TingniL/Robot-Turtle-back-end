package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

//choix de l'action que le joueur veut réaliser (executer, compléter ou construire mur)
public class Action extends JFrame implements ActionListener {

    JButton button1;
    JButton button2;
    JButton button3;
    private Joueur joueur;
    private int nbr_joueurs;
    public static Scanner scanner = new Scanner(System.in);
    private Plateau plateau;
    private TreeSet<Joueur> listjoueurs;
    private ArrayList<Joyau> listjoyaus;
    private Joyau joyau;

    public Action(Joueur joueur,int nbr_joueurs,Plateau plateau,TreeSet<Joueur> listjoueurs,ArrayList<Joyau> listjoyaus) {
//il faut bien placer les boutons et ajouter une phrase qui indique qu'il faut choisir (il vaut peut etre meixu mettre le plateau en background)
        // indiquer quel joueur doit jouer
        //avec interphace graphique
        this.joueur = joueur;//construction par defaut
        this.nbr_joueurs = nbr_joueurs;
        this.plateau = plateau;
        this.listjoueurs = listjoueurs;
        this.listjoyaus = listjoyaus;

    }
    public Action(Joueur joueur,int nbr_joueurs,Plateau plateau,TreeSet<Joueur> listjoueurs,Joyau joyau) {
        this.joueur = joueur;//construction par defaut
        this.nbr_joueurs = nbr_joueurs;
        this.plateau = plateau;
        this.listjoueurs = listjoueurs;
        this.joyau = joyau;
    }
    public void choix(){
       /* this.setTitle("choix action Robot Turtles");
        this.setSize(620, 802);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //interface graphique avec 3 buttons avec les trois possibilités
        Box b1 = Box.createHorizontalBox();
        this.button1 = new JButton("executer programme");
        this.button1.addActionListener(this);
        b1.add(this.button1);

        Box b2 = Box.createHorizontalBox();
        this.button2 = new JButton("compléter programme");
        this.button2.addActionListener(this);
        b2.add(this.button2);

        Box b3 = Box.createHorizontalBox();
        this.button3 = new JButton("construire mur");
        this.button3.addActionListener(this);
        b3.add(this.button3);

        //Conteneur avec gestion verticale
        Box b4 = Box.createVerticalBox();
        b4.add(Box.createVerticalGlue());
        b4.add(b1);
        b4.add(b2);
        b4.add(b3);

        this.setContentPane(new Bg());
        this.getContentPane().add(b4);
        this.setVisible(true);*/

        System.out.println("Voulez vous :1executer programme\n"+"2: compléter programme\n"+"3:construire mur");
        int choix = scanner.nextInt();
        commence(choix);

    }
    //action réalisée quand on clique sur un bouton
   public void actionPerformed(ActionEvent e){


        Object source = e.getSource();//indique sur quel bouton on a appuyé

        if (source == this.button2) {
            System.out.println("compléter le programme");
            Completer_programme completer_programme = new Completer_programme();
            joueur = completer_programme.completer(joueur);

        }
        if (source == this.button1) {
            Executer_programme exec = new Executer_programme(plateau,joueur,listjoueurs);
            //Executer_programme exec = new Executer_programme();
            exec.mouvement(joueur,nbr_joueurs);
        }


        else if (source == this.button3) {
            System.out.println("construire un mur");
            Construire_mur cons = new Construire_mur(listjoueurs,listjoyaus,nbr_joueurs);
            cons.construre(joueur,plateau);
        }
    }
    public void commence(int i){
        if (i ==2) {
            System.out.println("compléter le programme");
            Completer_programme completer_programme = new Completer_programme();
            joueur = completer_programme.completer(joueur);

        }
        if (i==1) {
            Executer_programme exec = new Executer_programme(plateau,joueur,listjoueurs);
            //Executer_programme exec = new Executer_programme();
            exec.mouvement(joueur,nbr_joueurs);
        }


        else if (i==3) {
            System.out.println("construire un mur");
            Construire_mur cons = new Construire_mur(listjoueurs,listjoyaus,nbr_joueurs);
            cons.construre(joueur,plateau);
        }
    }


}
