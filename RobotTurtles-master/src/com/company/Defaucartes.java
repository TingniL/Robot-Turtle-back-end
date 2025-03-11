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
            if (!map.containsKey(carRam)){//开始从每个玩家的37张手牌列表中随机抽取5张
                //如果37张手牌全部被抽完，用废弃手牌列表
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
            System.out.println(joueur.getpiohce(i));
        }
        System.out.println("Voulez vous défausser vos cartes à la main?Oui entrer O. Non entrer N");//抛弃手上抽到的部分手牌
        String b = scanner.nextLine();
        while (b.equals("O")){
            while (listpioche.size()>=0||listpioche.size()<5){
                System.out.println("Voulez vous défausser lequel/lesquels Entrez Bleu\n" +
                    " Entrez Jaune\n" +"Entrez Violet\n"+ " Entrez laser\n");
                String cartes = scanner.nextLine();
                if(cartes.equals("Bleu")||cartes.equals("Jaune")||cartes.equals("Violet")||cartes.equals("Laser")||listpioche.contains(cartes)){
                    for (int i=0;i<listpioche.size();i++){
                        OUT:
                    if (joueur.getpiohce(i).equals(cartes)){
                        listpioche.remove(i);
                        break OUT;
                        }
                    }
                    listdefau.add(cartes);
                    joueur.setListpioche(listpioche);
                    joueur.setListdefauss(listdefau);//加入废弃手牌列表
                System.out.println("Voulez vous défausser vos cartes à la main?Oui entrer O. Non entrer N");
                b = scanner.nextLine();
                return joueur;
            }else {System.out.println("Mauvais sasition");
                continue;}}
        }
        return joueur;
    }

    public Joueur Repiochecarte(Joueur joueur){
        ArrayList<Cartes> listCartes =joueur.getListcartes();//重新抽牌至手上有5张可用手牌
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
        String[] programme = copyFile(instruction); //对隐藏预执行列表
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
