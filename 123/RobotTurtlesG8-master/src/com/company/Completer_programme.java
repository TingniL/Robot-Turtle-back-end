package com.company;

import java.util.*;

public class Completer_programme {
    private static ArrayDeque<String> instruction;

    public static Scanner scanner = new Scanner(System.in);

    public Completer_programme() {

    }//end completer_programme

    public static Joueur completer(Joueur joueur){
        ArrayList listpioche = joueur.getListpioche();
        System.out.println("Voulez vous mettre les cartes dans votre programme?Oui entrer O. Non entrer N");
        String b = scanner.next();
        while (b.equals("O")){
            System.out.print("Vous avez ");
            for (int i =0;i<listpioche.size();i++){
                System.out.println(listpioche.get(i));
            }
            System.out.println("Voulez vous Avancer: Entrez Bleu\n" +
                    "Un quart tour vers la gauche : Entrez Jaune\n" +
                    "Un quart tour vers la droit : Entrez Violette\n"+
                    "Mettre un Laser : Entrez laser\n");
            String a = scanner.nextLine();
            if(a.equals("Bleu")||a.equals("Jaune")||a.equals("Violette")||a.equals("Laser")||listpioche.contains(a)){
                instruction.add(a);
                listpioche.remove(a);
                joueur.setListpioche(listpioche);
                joueur.setInstruction(instruction);
                return joueur;
            }
            else {System.out.println("Mauvais sasition");
                continue;}
        }
        joueur.setListpioche(listpioche);
        joueur.setInstruction(instruction);
        Defaucartes defau = new Defaucartes();
        defau.Cacherleprogramme(instruction);
        return joueur;
    }





}//end class
