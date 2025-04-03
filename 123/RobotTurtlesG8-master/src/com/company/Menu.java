package com.company;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.Box;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Menu extends JFrame  implements ActionListener {
    //pour que les boutons appartiennent à la classe
    JButton button1;
    JButton button2;
    JButton button3;
    private TreeSet<Joueur> listjoueur;
    private Fin_jeu Findejeu;


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
        TreeSet<Joueur> listjoeur = new TreeSet<>();
        this.listjoueur = listjoeur;
        Defaucartes defau = new Defaucartes();
        Object source = e.getSource();//indique sur quel bouton on a appuyé
        Scanner scanner = new Scanner(System.in);
        Plateau plateau = new Plateau();

        Obstacles obstacles = new Obstacles();
        ArrayList<Obstacles> listOb = iniOb(obstacles);//initialisation list des cartes obstacles

        ArrayList<Cartes> listCartes = Deck();

        Findejeu = new Fin_jeu(false);
        if (source == this.button1) {
            int nbr_joueurs = 2;
            System.out.println("Veuillez saisir le nom des " + (nbr_joueurs + 1) + " joueurs : ");
            for (int i = 0; i < nbr_joueurs; i++) {
                System.out.println("Indiquez le nom du joueur " + (i + 1) + " : ");
                String nomjou = scanner.nextLine();

                if (i == 0) {
                    int[] loc = new int[]{0, 1};
                    Joueur joueur = new Joueur(loc, nomjou,listCartes,listOb);
                    listjoueur.add(joueur);
                } else {
                    int[] loc = new int[]{0, 6};
                    Joueur joueur = new Joueur(loc, nomjou,listCartes,listOb);
                    listjoueur.add(joueur);
                }
            }

            //int joueur_debut = (int) (Math.random()*nbr_joueurs);
            //System.out.println("le joueur qui commence est : " +listjoueur); Marche pas avec Treeset

            System.out.println("le jeu va commencer");
            //Plateau plateau = new Plateau(nbr_joueurs); //renvoie vers le constructeur Plateau qui créé le plateau initial pour le bon nombre de joueurs
            //ordre des tours
            Joyau joyau = new Joyau(new int[]{7, 3},"Joyau");
            plateau.initialisationplateau(listjoueur,joyau);
            while (Findejeu.getfin() == false) {
                for (Iterator i = listjoueur.iterator(); i.hasNext(); ) {
                    Joueur joueur = (Joueur) i.next();
                    defau.Piochecarte(joueur);
                    Action action = new Action(joueur,nbr_joueurs,plateau,listjoueur,joyau);
                    action.choix();
                    defau.Defausse(joueur);
                    defau.Repiochecarte(joueur);
                }
                Findejeu.isFinjeu(listjoueur, joyau);
            }
            /*System.out.println("ordre des tours : ");
            for (int i=0; i<nbr_joueurs; i++){
                System.out.print(ordre_tour[i]+" ");
            }//end for*/

        } else if (source == this.button2) {
            int nbr_joueurs = 3;
            System.out.println("Veuillez saisir le nom des " + (nbr_joueurs + 1) + " joueurs : ");
            for (int i = 0; i < nbr_joueurs; i++) {
                System.out.println("Indiquez le nom du joueur " + (i + 1) + " : ");
                String nomjou = scanner.nextLine();

                if (i == 0) {
                    int[] loc = new int[]{0, 0};
                    Joueur joueur = new Joueur(loc, nomjou,listCartes,listOb);
                    listjoueur.add(joueur);
                } else if (i == 1) {
                    int[] loc = new int[]{0, 3};
                    Joueur joueur = new Joueur(loc, nomjou,listCartes,listOb);
                    listjoueur.add(joueur);
                } else {
                    int[] loc = new int[]{0, 6};
                    Joueur joueur = new Joueur(loc, nomjou,listCartes,listOb);
                    listjoueur.add(joueur);
                }
            }
            //int joueur_debut = (int) (Math.random()*nbr_joueurs);
            //System.out.println("le joueur qui commence est : " +listjoueur); Marche pas avec Treeset

            System.out.println("le jeu va commencer");
            //Plateau plateau = new Plateau(nbr_joueurs); //renvoie vers le constructeur Plateau qui créé le plateau initial pour le bon nombre de joueurs
            //ordre des tours
            ArrayList<Joyau> joyaus = new ArrayList<>();
            Joyau joy1 = new Joyau(new int[]{7, 0},"Joyau");
            Joyau joy2 = new Joyau(new int[]{7, 3},"Joyau");
            Joyau joy3 = new Joyau(new int[]{7, 6},"Joyau");
            joyaus.add(joy1);
            joyaus.add(joy2);
            joyaus.add(joy3);
            while (Findejeu.getfin() == false) {
                for (Iterator i = listjoueur.iterator(); i.hasNext(); ) {
                    Joueur joueur = (Joueur) i.next();
                    defau.Piochecarte(joueur);
                    Action action = new Action(joueur,nbr_joueurs,plateau,listjoueur,joyaus);
                    action.choix();
                    defau.Defausse(joueur);
                    defau.Repiochecarte(joueur);
                }
                Findejeu.isFinjeu2(listjoueur, joyaus,nbr_joueurs);
            }
        } else if (source == this.button3) {
            int nbr_joueurs = 4;
            System.out.println("Veuillez saisir le nom des " + (nbr_joueurs + 1) + " joueurs : ");
            for (int i = 0; i < nbr_joueurs; i++) {
                System.out.println("Indiquez le nom du joueur " + (i + 1) + " : ");
                String nomjou = scanner.nextLine();
                if (i == 0) {
                    int[] loc = new int[]{0, 0};
                    Joueur joueur = new Joueur(loc, nomjou,listCartes,listOb);
                    listjoueur.add(joueur);
                } else if (i == 1) {
                    int[] loc = new int[]{0, 2};
                    Joueur joueur = new Joueur(loc, nomjou,listCartes,listOb);
                    listjoueur.add(joueur);
                } else if (i == 2) {
                    int[] loc = new int[]{0, 5};
                    Joueur joueur = new Joueur(loc, nomjou,listCartes,listOb);
                    listjoueur.add(joueur);
                } else {
                    int[] loc = new int[]{0, 7};
                    Joueur joueur = new Joueur(loc, nomjou,listCartes,listOb);
                    listjoueur.add(joueur);
                }
            }

            //int joueur_debut = (int) (Math.random()*nbr_joueurs);
            //System.out.println("le joueur qui commence est : " +listjoueur); Marche pas avec Treeset

            System.out.println("le jeu va commencer");
            //Plateau plateau = new Plateau(nbr_joueurs); //renvoie vers le constructeur Plateau qui créé le plateau initial pour le bon nombre de joueurs
            //ordre des tours
            ArrayList<Joyau> joyaus = new ArrayList<>();
            Joyau joy1 = new Joyau(new int[]{7, 1},"Joyau");
            Joyau joy2 = new Joyau(new int[]{7, 6},"Joyau");
            joyaus.add(joy1);
            joyaus.add(joy2);
            while (Findejeu.getfin() == false) {
                for (Iterator i = listjoueur.iterator(); i.hasNext(); ) {
                    Joueur joueur = (Joueur) i.next();
                    defau.Piochecarte(joueur);
                    Action action = new Action(joueur,nbr_joueurs,plateau,listjoueur,joyaus);
                    action.choix();
                    defau.Defausse(joueur);
                    defau.Repiochecarte(joueur);
                }
                Findejeu.isFinjeu2(listjoueur, joyaus,nbr_joueurs);
            }
        }
    }
    public ArrayList iniOb(Obstacles obstacles){
        ArrayList<Obstacles> listobs = new ArrayList<>();
        MurPierre pierre = new MurPierre();
        MurGlace glace = new MurGlace();
        for(int i =0;i <3;i++){
            listobs.add(pierre);
        }
        for (int i =0;i<2;i++){
            listobs.add(glace);
        }
        return listobs;
    }


    public ArrayList Deck(){ //C'est mieux mettre un methode pour initialisation des cartes

        ArrayList listcartes = new ArrayList<Cartes>();

        for(int a =0; a<=18; a++)
        {
            listcartes.add(new Bleu());
        }
        for(int b= 0; b<8; b++)
        {
            listcartes.add(new Jaune());
        }
        for(int c= 0; c<8; c++)
        {
            listcartes.add(new Violet());
        }
        for(int d= 0; d<3; d++)
        {
            listcartes.add(new Laser());
        }
        return listcartes;
    }
}