package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

//choix de l'action que le joueur veut réaliser (executer, compléter ou construire mur)
public class Action extends JFrame implements ActionListener {

    JButton button1;
    JButton button2;
    JButton button3;

    public Action() {
//il faut bien placer les boutons et ajouter une phrase qui indique qu'il faut choisir (il vaut peut etre meixu mettre le plateau en background)
        // indiquer quel joueur doit jouer

        //avec interphace graphique
        this.setTitle("choix action Robot Turtles");
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
        this.setVisible(true);

    }
    //action réalisée quand on clique sur un bouton
    public void actionPerformed(ActionEvent e){
        Object source = e.getSource();//indique sur quel bouton on a appuyé

        if (source == this.button1) {
            Executer_programme exec = new Executer_programme();
        }

        if (source == this.button2) {
            System.out.println("compléter le programme");
            //Completer_programme completer = new Completer_programme();
        }

        if (source == this.button3) {
            System.out.println("construire un mur");
            Construire_mur construire = new Construire_mur();
        }
    }
}
