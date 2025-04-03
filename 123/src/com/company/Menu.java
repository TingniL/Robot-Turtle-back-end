package com.company;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.Box;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

public class Menu extends JFrame  implements ActionListener {
    //pour que les boutons appartiennent à la classe
    JButton button1;
    JButton button2;
    JButton button3;

    public Menu() {
        this.setTitle("Robot Turtles");
        this.setSize(620, 802);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Conteneur avec gestion horizontale
        Box b1 = Box.createHorizontalBox();
        this.button1 = new JButton("2 joueurs");
        this.button1.addActionListener(this);
        b1.add(this.button1);

        b1.add(Box.createRigidArea(new Dimension(200, 500)));
        this.button2 = new JButton("3 joueurs");
        this.button2.addActionListener(this);
        b1.add(this.button2);

        Box b2 = Box.createHorizontalBox();
        this.button3 = new JButton("4 joueurs");
        this.button3.addActionListener(this);
        b2.add(this.button3);

        //Conteneur avec gestion verticale
        Box b3 = Box.createVerticalBox();
        b3.add(Box.createVerticalGlue());
        b3.add(b1);
        b3.add(b2);


        this.setContentPane(new Bg());
        this.getContentPane().add(b3);
        this.setVisible(true);

        //fermer la fenêtre une fois qu'on a cliqué sur un des boutons?
    }

    //action exécutée quand on clique sur les boutons
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();//indique sur quel bouton on a appuyé

        Scanner scanner = new Scanner(System.in);

        if (source == this.button1) {
            int nbr_joueurs = 2;
            String joueurs[] = new String[nbr_joueurs];
            System.out.println("Veuillez saisir le nom des " + nbr_joueurs + " joueurs : ");
            for (int i = 0; i < nbr_joueurs; i++) {
                System.out.println("Indiquez le nom du joueur " + (i + 1) + " : ");
                joueurs[i] = scanner.nextLine();
            }
            int joueur_debut = (int) (Math.random()*nbr_joueurs);
            System.out.println("le joueur qui commence est : " +joueurs[joueur_debut]);

            System.out.println("le jeu va commencer");
            Plateau plateau = new Plateau(nbr_joueurs); //renvoie vers le constructeur Plateau qui créé le plateau initial pour le bon nombre de joueurs

            //ordre des tours
            String[] ordre_tour = {joueurs[joueur_debut], joueurs[nbr_joueurs-1-joueur_debut] };
            System.out.println("ordre des tours : ");
            for (int i=0; i<nbr_joueurs; i++){
                System.out.print(ordre_tour[i]+" ");
            }//end for
        }

        if (source == this.button2) {
            int nbr_joueurs = 3;
            String joueurs[] = new String[nbr_joueurs];
            System.out.println("Veuillez saisir le nom des " + nbr_joueurs + " joueurs : ");
            for (int i = 0; i < nbr_joueurs; i++) {
                System.out.println("Indiquez le nom du joueur " + (i + 1) + " : ");
                joueurs[i] = scanner.nextLine();
            }
            int joueur_debut = (int) (Math.random()*nbr_joueurs);
            System.out.println("le joueur qui commence est : " +joueurs[joueur_debut]);

            System.out.println("le jeu va commencer");
            Plateau plateau = new Plateau(nbr_joueurs);

            //ordre des tours
            String[] ordre_tour = new String[3];
            System.out.println("ordre des tours : ");

            for (int i=0; i<nbr_joueurs; i++){
                int place = joueur_debut+i;
                if (place>2){
                    place = place - 3;
                }
                ordre_tour[i] = joueurs[place];
                System.out.print(ordre_tour[i]+" ");
            }//end for
        }

        if (source == this.button3) {
            int nbr_joueurs = 4;
            String joueurs[] = new String[nbr_joueurs];
            System.out.println("Veuillez saisir le nom des " + nbr_joueurs + " joueurs : ");
            for (int i = 0; i < nbr_joueurs; i++) {
                System.out.println("Indiquez le nom du joueur " + (i + 1) + " : ");
                joueurs[i] = scanner.nextLine();
            }
            int joueur_debut = (int) (Math.random()*nbr_joueurs);
            System.out.println("le joueur qui commence est : " +joueurs[joueur_debut]);

            System.out.println("le jeu va commencer");
            Plateau plateau = new Plateau(nbr_joueurs);

            //ordre des tours
            String[] ordre_tour = new String[4];
            System.out.println("ordre des tours : ");

            for (int i=0; i<nbr_joueurs; i++){
                int place = joueur_debut+i;
                if (place>3){
                    place = place - 4;
                }
                ordre_tour[i] = joueurs[place];
                System.out.print(ordre_tour[i]+" ");
            }//end for
        }

    }
}