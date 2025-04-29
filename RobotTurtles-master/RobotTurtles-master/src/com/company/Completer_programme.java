package com.company;

import java.util.*;

public class Completer_programme {


    public static Scanner scanner = new Scanner(System.in);
    private Joueur joueur;

    public Completer_programme(Joueur joueur) {
        this.joueur = joueur;
    }//end completer_programme

    public Joueur completer(){
        ArrayList listpioche = joueur.getListpioche();
        ArrayDeque<String> instruction = new ArrayDeque<>();
        String b = "O";//第一次默认放置手牌
        while (b.equals("O")){
            System.out.print("Vous avez ");
            for (int i =0;i<listpioche.size();i++){
                System.out.println(joueur.getpiohce(i));
            }//打出所有可用手牌
            System.out.println("Voulez vous Avancer: Entrez Bleu\n" +//蓝色移动黄色左转紫色右转 激光
                    "Un quart tour vers la gauche : Entrez Jaune\n" +
                    "Un quart tour vers la droit : Entrez Violet\n"+
                    "Mettre un Laser : Entrez laser\n");
            String a = scanner.nextLine();
            if(a.equals("Bleu")||a.equals("Jaune")||a.equals("Violet")||a.equals("Laser")||listpioche.contains(a)){
                instruction.add(a);
                for (int i=0;i<listpioche.size();i++){
                    OUT:
                    if (joueur.getpiohce(i).equals(a)){
                        listpioche.remove(i);
                        break OUT;//删除已添加在预执行列表里的手牌
                    }//在某些手牌重复的时候，有时候会正确只删除一张手牌，有时候会删除多张
                }
                joueur.setListpioche(listpioche);//将新的手牌更新到玩家中
                joueur.setInstruction(instruction);//将预执行列表更新到玩家中
                System.out.println("Voulez vous mettre les cartes dans votre programme?Oui entrer O. Non entrer N");
                b = scanner.nextLine();//是否继续将手牌放置到预执行列表中
                continue;
            }
            else {System.out.println("Mauvais sasition");
                continue;}
        }
        joueur.setListpioche(listpioche);
        joueur.setInstruction(instruction);
        Defaucartes defau = new Defaucartes();
        defau.Cacherleprogramme(instruction);//将预执行列表对玩家隐藏
        return joueur;
    }





}//end class
