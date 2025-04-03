package com.company;

import java.util.*;

public class Defaucartes {
    public static Scanner scanner = new Scanner(System.in);

    Defaucartes(){

    }

        public static Joueur Piochecarte(Joueur joueur){
        ArrayList<Cartes> listCartes = joueur.getListcartes();
        int ndepioche = 5;
        Map map = new HashMap();
        ArrayList<Cartes> listpioche = new ArrayList();
        while(map.size()<ndepioche){
            int carRam = (int) (Math.random() * listCartes.size());//pioche dans la liste des cartes au hasard
            if (!map.containsKey(carRam)){
                map.put(carRam,"");
                Cartes carte = listCartes.get(carRam);//prend la carte par le numero obtenu
                listpioche.add(carte);
                listCartes.remove(carte);
            }
        }
        joueur.setListcartes(listCartes);
        joueur.setListpioche(listpioche);
        return joueur;
    }
    public Joueur Defausse(Joueur joueur){
        ArrayList<Cartes> listpioche = joueur.getListpioche();
        ArrayList listdefau = new ArrayList();
        System.out.print("Vous avez ");
        for (int i =0;i<listpioche.size();i++){
            System.out.println(listpioche.get(i));
        }
        System.out.println("Voulez vous défausser vos cartes à la main?Oui entrer O. Non entrer N");
        String b = scanner.nextLine();
        while (b.equals("O")&&(listpioche.size()>=0||listpioche.size()<5)){
            System.out.println("Voulez vous défausser lequel/lesquels Entrez Bleu\n" +
                    " Entrez Jaune\n" +"Entrez Violette\n"+ " Entrez laser\n");
            String cartes = scanner.nextLine();
            if(cartes.equals("Bleu")||cartes.equals("Jaune")||cartes.equals("Violette")||cartes.equals("Laser")||listpioche.contains(cartes)){
                listpioche.remove(cartes);
                listdefau.add(cartes);
                joueur.setListpioche(listpioche);
                joueur.setListdefauss(listdefau);
                return joueur;
            }else {System.out.println("Mauvais sasition");
                continue;}
        }
        return joueur;
    }

    public Joueur Repiochecarte(Joueur joueur){
        ArrayList<Cartes> listCartes =joueur.getListcartes();
        ArrayList listpioche =joueur.getListpioche();
        int ndepioche = 5;
        Map map = new HashMap();
        while(listpioche.size()<ndepioche){
            int carRam = (int) (Math.random() * listCartes.size());
            if (!map.containsKey(carRam)){
                map.put(carRam,"");
                Cartes carte = listCartes.get(carRam);
                listpioche.add(carte);
                listCartes.remove(carte);
            }
        }
        joueur.setListpioche(listpioche);
        return joueur;
    }
    public static void Cacherleprogramme(ArrayDeque<String> instruction){
        String[] programme = copyFile(instruction);
        for (int i =0;i<programme.length;i++){
            programme[i]="*";//cacher les cartes dans interface graphique
        }

    }
    public static String[] copyFile(ArrayDeque<String> File){
        String[] ins = new String[5];
        for(int i = 0;i<5;i++){
            ins[i] = File.pollFirst();
        }
        return ins;
    }
}
