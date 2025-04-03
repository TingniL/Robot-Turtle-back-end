package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

public class Construire_mur extends JFrame implements ActionListener {
    JButton button1;
    JButton button2;
    int emplacement_ligne;
    int emplacement_colonne;
    String mur;

    public Construire_mur(){
        //choisir quel mur placer
        this.setTitle("choix du mur à placer");
        this.setSize(620, 802);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Box b1 = Box.createHorizontalBox();
        this.button1 = new JButton("mur en pierre");
        this.button1.addActionListener(this);
        b1.add(this.button1);

        Box b2 = Box.createHorizontalBox();
        this.button2 = new JButton("mur en glace");
        this.button2.addActionListener(this);
        b2.add(this.button2);

        //Conteneur avec gestion verticale
        Box b4 = Box.createVerticalBox();
        b4.add(Box.createVerticalGlue());
        b4.add(b1);
        b4.add(b2);

        this.setContentPane(new Bg());
        this.getContentPane().add(b4);
        this.setVisible(true);

    }//end construire_mur

    public void actionPerformed(ActionEvent e){
        Object source = e.getSource();

        Scanner scanner = new Scanner(System.in);

        if (source == this.button1) {
            System.out.println("où voulez vous placer ce mur en pierre ?");
            //cliquer où ils veulent placer ou noter l'emplacement dans le terminal?
            //si on clique, chaque case du plateau doit être un bouton
            System.out.println("quelle ligne ?");
            int emplacement_ligne = scanner.nextInt();
            System.out.println("quelle colonne ?");
            int emplacement_colonne = scanner.nextInt();
            //vérifier que l'emplacement est possible et disponible
            //vérifier que le joyaux n'est pas bloqué par mur
        }

        if (source == this.button2) {
            System.out.println("où voulez vous placer ce mur en glace ?");
            //cliquer où ils veulent placer ou noter l'emplacement dans le terminal?
            //si on clique, chaque case du plateau doit être un bouton
            System.out.println("quelle ligne ?");
            int emplacement_ligne = scanner.nextInt();
            System.out.println("quelle colonne ?");
            int emplacement_colonne = scanner.nextInt();

            //vérifier que l'emplacement est possible et disponible
            //vérifier que le joyaux n'est pas bloqué par mur
        }

        System.out.println("défausser? O ou N");
        char defausse = scanner.next().charAt(0);
        if (defausse == 'O') {
            System.out.println("vous défaussez");
            //code pour défausser
        }
        if (defausse == 'N'){
            System.out.println("joueur suivant");
            //code pour passer au joueur suivant
            //Fin_jeu fin_jeu = new Fin_jeu(nbr_joueurs, ordre_tours);
        }

    }//end action performed


}//end class