package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

public class Construire_mur extends JFrame implements ActionListener {
    JButton button1;
    JButton button2;

    Joueur joueur;
    Plateau plateau;
    TreeSet<Joueur> listjoueurs;
    ArrayList<Joyau> listjoyau;
    int nbr_joueurs;
    ArrayList<Obstacles> listobstacles;

    public Construire_mur(TreeSet<Joueur> listjoueurs,ArrayList<Joyau> listjoyau,int nbr){
        this.listjoueurs = listjoueurs;
        this.listjoyau = listjoyau;
        this.nbr_joueurs = nbr;
    }//end construire_mur


    public void construre(Joueur joueur,Plateau plateau){
        this.joueur = joueur;
        this.plateau = plateau;
        ArrayList<Obstacles> listobstacle = joueur.getListobstacles();
        this.listobstacles = listobstacle;
        System.out.println("Vous avez encore : ");
        for (int i=0;i<listobstacle.size();i++){
            System.out.print(listobstacle.get(i));
        }

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
    }

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
            MurPierre murPierre = new MurPierre();
            placesurplateau(murPierre,emplacement_ligne,emplacement_colonne);
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
            MurGlace murGlace = new MurGlace();
            placesurplateau(murGlace,emplacement_ligne,emplacement_colonne);

            //vérifier que l'emplacement est possible et disponible
            //vérifier que le joyaux n'est pas bloqué par mur
        }



    }//end action performed
    public void placesurplateau(Obstacles obstacle,int ligne,int colonne){
        plateau.setPlateau(ligne,colonne,obstacle.getName());
        listobstacles.remove(obstacle);
        if (bloquer(nbr_joueurs)){
            plateau.setPlateau(ligne,colonne,null);
            listobstacles.add(obstacle);
            System.err.println("Vous ne pouvez pas bloquer un joueur ou un joyau");
        }
        joueur.setListobstacles(listobstacles);
    }
    public boolean bloquer(int nbr){
        ArrayList<String> listob = new ArrayList<>();
        listob.add("Pierre");
        listob.add("Bois");//quand y a deux/trois joueurs les murs en bois sont tous en dernieres colonne,on considere
        //donc on est bloque aussi
        listob.add("Glace");
        boolean estbloque = false;
        for (Iterator i = listjoueurs.iterator();i.hasNext();){

            Joueur joueur = (Joueur) i.next();
            int[] location =joueur.getLocation();
            OUT:
            if(location[0]!=0||location[0]!=7){
                String ob1 = plateau.getobjet(location[0]-1,location[1]);
                String ob2 = plateau.getobjet(location[0]+1,location[1]);
                String ob3 = plateau.getobjet(location[0],location[1]-1);
                String ob4 = plateau.getobjet(location[0],location[1]+1);
                if (listob.contains(ob1)&&listob.contains(ob2)&&listob.contains(ob3)&&listob.contains(ob4)){
                    estbloque= true;
                    break OUT;//quand on trouve le joueur est bloque on s'arrest le boucle
                }
                else estbloque= false;

            }else if(location[1]!=0||location[1]!=7){
                String ob1 = plateau.getobjet(location[0]-1,location[1]);
                String ob2 = plateau.getobjet(location[0]+1,location[1]);
                String ob3 = plateau.getobjet(location[0],location[1]-1);
                String ob4 = plateau.getobjet(location[0],location[1]+1);
                if (listob.contains(ob1)&&listob.contains(ob2)&&listob.contains(ob3)&&listob.contains(ob4)){
                    estbloque= true;
                    break OUT;
                }
                else estbloque= false;

            }
            else if((location[0]==0) ){
                if(location[1]==0){
                    String ob1 = plateau.getobjet(location[0]+1,location[1]);
                    String ob2 = plateau.getobjet(location[0],location[1]+1);
                    if (listob.contains(ob1)&&listob.contains(ob2)){
                        estbloque= true;
                        break OUT;
                    }
                    else estbloque= false;}
                else if(location[1]==7){
                    String ob1 = plateau.getobjet(location[0]+1,location[1]);
                    String ob2 = plateau.getobjet(location[0],location[1]-1);
                    if (listob.contains(ob1)&&listob.contains(ob2)){
                        estbloque= true;
                        break OUT;
                    }
                    else estbloque= false;}
                else {
                    String ob1 = plateau.getobjet(location[0],location[1]+1);
                    String ob2 = plateau.getobjet(location[0]+1,location[1]);
                    String ob3 = plateau.getobjet(location[0],location[1]-1);
                    if (listob.contains(ob1)&&listob.contains(ob2)&&listob.contains(ob3)){
                        estbloque= true;
                        break OUT;
                    }
                    else estbloque= false;
                }
            }
            else if((location[0]==7) ){
                if(location[1]==0){
                    String ob1 = plateau.getobjet(location[0]-1,location[1]);
                    String ob2 = plateau.getobjet(location[0],location[1]+1);
                    if (listob.contains(ob1)&&listob.contains(ob2)){
                        estbloque= true;
                        break OUT;
                    }
                    else estbloque= false;}
                else if(location[1]==7){
                    String ob1 = plateau.getobjet(location[0]-1,location[1]);
                    String ob2 = plateau.getobjet(location[0],location[1]-1);
                    if (listob.contains(ob1)&&listob.contains(ob2)){
                        estbloque= true;
                        break OUT;
                    }
                    else estbloque= false;}
                else {
                    String ob1 = plateau.getobjet(location[0],location[1]+1);
                    String ob2 = plateau.getobjet(location[0]-1,location[1]);
                    String ob3 = plateau.getobjet(location[0],location[1]-1);
                    if (listob.contains(ob1)&&listob.contains(ob2)&&listob.contains(ob3)){
                        estbloque= true;
                        break OUT;
                    }
                    else estbloque= false;
                }
            }
            else if((location[1]==0) ){
                    String ob1 = plateau.getobjet(location[0],location[1]+1);
                    String ob2 = plateau.getobjet(location[0]+1,location[1]);
                    String ob3 = plateau.getobjet(location[0]-1,location[1]);
                    if (listob.contains(ob1)&&listob.contains(ob2)&&listob.contains(ob3)){
                        estbloque= true;
                        break OUT;
                    }
                    else estbloque= false;
                }
            else if((location[1]==7) ){
                String ob1 = plateau.getobjet(location[0],location[1]+1);
                String ob2 = plateau.getobjet(location[0]+1,location[1]);
                String ob3 = plateau.getobjet(location[0]-1,location[1]);
                if (listob.contains(ob1)&&listob.contains(ob2)&&listob.contains(ob3)){
                    estbloque= true;
                    break OUT;
                }
                else estbloque= false;
            }
        }
        if (nbr ==2){
            String ob1 = plateau.getobjet(7,4);
            String ob2 = plateau.getobjet(7,2);
            String ob3 = plateau.getobjet(6,3);
            if (listob.contains(ob1)&&listob.contains(ob2)&&listob.contains(ob3)){
                estbloque= true;
            }
            else estbloque= false;
        }else if (nbr ==3)
        {
            for (int i =0;i<listjoyau.size();i++){
                Joyau joyau = listjoyau.get(i);
                int[] location = joyau.getIndex();
                OUT:
                if (i==0){
                    String ob1 = plateau.getobjet(location[0]-1,location[1]);
                    String ob2 = plateau.getobjet(location[0],location[1]+1);
                    if (listob.contains(ob1)&&listob.contains(ob2)){
                        estbloque= true;
                        break OUT;
                    }
                    else estbloque= false;
                }
                else if (i==2){
                    String ob1 = plateau.getobjet(location[0]-1,location[1]);
                    String ob2 = plateau.getobjet(location[0],location[1]-1);
                    if (listob.contains(ob1)&&listob.contains(ob2)){
                        estbloque= true;
                        break OUT;
                    }
                    else estbloque= false;
                }
                else if (i==1){
                    String ob1 = plateau.getobjet(location[0]-1,location[1]);
                    String ob2 = plateau.getobjet(location[0],location[1]-1);
                    String ob3 = plateau.getobjet(location[0],location[1]+1);
                    if (listob.contains(ob1)&&listob.contains(ob2)&&listob.contains(ob3)){
                        estbloque= true;
                        break OUT;
                    }
                    else estbloque= false;
                } } }
        else if(nbr==4){
            for (int i =0;i<listjoyau.size();i++){
                Joyau joyau = listjoyau.get(i);
                int[] location = joyau.getIndex();
                    String ob1 = plateau.getobjet(location[0]-1,location[1]);
                    String ob2 = plateau.getobjet(location[0],location[1]-1);
                    String ob3 = plateau.getobjet(location[0],location[1]+1);
                    if (listob.contains(ob1)&&listob.contains(ob2)&&listob.contains(ob3)){
                        estbloque= true;
                    }
                    else estbloque= false;
                }}
        return estbloque;
    }


}//end class