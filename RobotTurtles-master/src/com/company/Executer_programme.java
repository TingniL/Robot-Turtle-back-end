package com.company;

import java.util.*;

//execute la liste de carte que le joueur a

public class Executer_programme {

    //au départ la tortue est vers le sud, s'il avance elle ira en bas mais ne doit pas réinitialiser la direction à chaque execution

    private Plateau plateau;
    private Joueur joueur;
    private TreeSet<Joueur> listjoueurs;
    private ArrayDeque<String> instruction;

     Executer_programme(Plateau plat,Joueur joueur,TreeSet<Joueur> listjoueurs){
        this.plateau = plat;
        this.joueur = joueur;
        this.listjoueurs = listjoueurs;

    }
    /*public void executer_instruction(ArrayDeque<String> instructions,int nbr_joueurs) { //liste des instructions à executer
        System.out.println("test");
        //POUR TESTER MAIS A RETIRER
        instructions.add("violette");
        instructions.add("violette");
        instructions.add("jaune");
        instructions.add("jaune");
        //this.nbr_joueurs =nbr_joueurs;
        this.mouvement(nbr_joueurs);
    }//end executer_instruction*/

    //action sur la tortues à chaque carte
    void mouvement(Joueur joueur,int nbr_joueurs) {
        System.out.println("test mouvement");
        char direction =joueur.getDirection();
        ArrayDeque<String> instruction = new ArrayDeque<>();
        int[] loc = joueur.getLocation();
        int indiceLigne = loc[0];//玩家行坐标
        int indiceColonne = loc[1];//玩家列坐标
        int newLigne = indiceLigne;
        int newcolonne =indiceColonne;

        while (instruction.size() != 0) {
            //bleu fait avancer
            if (instruction.getFirst().equals("Bleu")) {//蓝色前进
                //面朝东
                if (direction == 'E' && (indiceColonne + 1) < 8) { //此处如果棋盘该坐标objet为空直接抛出错误
                    String objet = plateau.getobjet(indiceLigne,indiceColonne++);
                    if (objet.equals("Bois")||objet.equals("Glace")||objet.equals("Pierre")){//如果碰到障碍物就180°转向
                        //虽然木头箱子可移动，但是2-3玩家占了一整列，所以此处认为木头箱子不可移动
                        if (indiceColonne-->=0){
                            Demitour(joueur);}
                        else{retour_depart(joueur);}//当反转后又碰到棋盘边界时返回起点
                    }
                    else if(objet.equals(null)){joueur.setLoc(indiceLigne,indiceColonne++);
                        newcolonne=indiceColonne++;}//y a rien, on avance
                    else if (objet!=null && objet!="Joyau"&&objet!="Bois"&&objet!="Glace"&&objet!="Pierre"){//2 tortue rencontre
                        departjoueur2(indiceLigne,indiceColonne++);//两个玩家相撞时，同时返回起点
                        retour_depart(joueur); } }
                else if(direction == 'E' && (indiceColonne + 1) > 8){
                    retour_depart(joueur); }//碰到棋盘边界时返回起点
                //面朝西
                if (direction == 'O' && (indiceColonne - 1) >= 0) {
                    String objet = plateau.getobjet(indiceLigne,indiceColonne--);
                    if (objet.equals("Bois")||objet.equals("Glace")||objet.equals("Pierre")){
                        if (indiceColonne--<8){
                            Demitour(joueur);}
                        else{retour_depart(joueur);}}
                    else if(objet.equals(null)){joueur.setLoc(indiceLigne,indiceColonne--);
                        newcolonne=indiceColonne--;}//y a rien, on avance
                    else if (objet!=null && objet!="Joyau"){//2 tortue rencontre
                        departjoueur2(indiceLigne,indiceColonne--);
                        retour_depart(joueur); } }
                else if(direction == 'O' && (indiceColonne + 1) <0){
                    retour_depart(joueur); }

                //面朝北
                if (direction == 'N' && (indiceLigne - 1) >= 0) {
                    String objet = plateau.getobjet(indiceLigne--,indiceColonne);
                    if (objet.equals("Bois")||objet.equals("Glace")||objet.equals("Pierre")){
                        if (indiceLigne--<8){
                            Demitour(joueur);}
                        else{retour_depart(joueur);}
                    }
                    else if(objet.equals(null)){joueur.setLoc(indiceLigne--,indiceColonne);
                        newLigne=indiceLigne--;}//y a rien, on avance
                    else if (objet!=null && objet!="Joyau"){//2 tortue rencontre
                        departjoueur2(indiceLigne--,indiceColonne);
                        retour_depart(joueur); }
                }
                else if(direction == 'N' && (indiceLigne + 1) <0){
                    retour_depart(joueur); }

                //面朝南
                if (direction == 'S' && (indiceLigne + 1) < 8) {
                    String objet = plateau.getobjet(indiceLigne+1,indiceColonne);
                    if(objet.equals(null)){joueur.setLoc(indiceLigne++,indiceColonne);
                        newLigne=indiceLigne+1;}//y a rien, on avance
                    else if (objet.equals("Bois")||objet.equals("Glace")||objet.equals("Pierre")){
                        if (indiceLigne++>=0){
                            Demitour(joueur);}
                        else{retour_depart(joueur);}
                    }

                    else if (objet!=null && objet!="Joyau"){//2 tortue rencontre
                        departjoueur2(indiceLigne++,indiceColonne);
                        retour_depart(joueur); } }
            }

            //	jaune pour aller à gauche
            if (instruction.getFirst().equals("Jaune")) {//黄色左转
                //东换北，北换西，西换南，南换东
                if (direction == 'E') { //si on est vers l'est et qu'on va à gauche on se retrouver vers le  nord
                    direction = 'N';
                    joueur.setDirection(direction);
                } else if (direction == 'N') {
                    direction = 'O';
                    joueur.setDirection(direction);
                } else if (direction == 'O') {
                    direction = 'S';
                    joueur.setDirection(direction);
                } else if (direction == 'S') {
                    direction = 'E';
                    joueur.setDirection(direction);
                }
            }

            //violet pour aller à droite
            if (instruction.getFirst().equals("Violette")) {//紫色右转
                if (direction == 'E') {
                    direction = 'S';
                    joueur.setDirection(direction);
                } else if (direction == 'S') {
                    direction = 'O';
                    joueur.setDirection(direction);
                } else if (direction == 'O') {
                    direction = 'N';
                    joueur.setDirection(direction);
                } else if (direction == 'N') {
                    direction = 'E';
                    joueur.setDirection(direction);
                }
            }

            //laser : indentifie s'il y a un element devant la tortue, différentes actions selon l'élément
            //les actions sont listées dans la méthode laser()
            if (instruction.getFirst().equals("Laser")) {//激光
                if (direction == 'E') {//面朝东时，检查所有大于玩家列的objet
                    for(int i =indiceColonne+1;i<8;i++){
                        String objet = plateau.getobjet(indiceLigne,i);
                        if (objet.equals("Glace")){
                            plateau.setPlateau(indiceLigne,i,null);//激光可破坏冰块墙
                        }
                        else if (objet.equals("Joyau")){//碰到宝石反射
                            if (nbr_joueurs==2){
                                Demitour(joueur);//2个玩家时使玩家180°反转
                            }
                            else if (nbr_joueurs>2){
                                retour_depart(joueur);//大于2个玩家时，该玩家回到起点
                            }
                        }
                        else if(objet.contains("E")||objet.contains("O")||objet.contains("N")||objet.contains("S")){//si objet a un char de direction
                            //on considere c'est un joueur
                            //检查激光是否碰到玩家
                            if (nbr_joueurs==2){
                                demiTourrouchee(indiceLigne,i);//两位玩家时，被激光击中的玩家180°反转
                            }else if (nbr_joueurs>2){
                                departjoueur2(indiceLigne,i);//大于2时，被激光击中的玩家返回起点
                            }
                        }

                    }
                } else if (direction == 'S') {
                    for(int i =indiceLigne-1;i>=0;i--){
                        String objet = plateau.getobjet(i,indiceColonne);
                        if (objet.equals("Glace")){
                            plateau.setPlateau(indiceLigne,i,null);
                            break;
                        }
                        else if (objet.equals("Joyau")){
                            if (nbr_joueurs==2){
                                Demitour(joueur);
                            }
                            else if (nbr_joueurs>2){
                                retour_depart(joueur);
                            }
                            break;
                        }
                        else if(objet.contains("E")||objet.contains("O")||objet.contains("N")||objet.contains("S")){//si objet a un char de direction
                            //on considere c'est un joueur
                            if (nbr_joueurs==2){
                                demiTourrouchee(indiceLigne,i);
                            }else if (nbr_joueurs>2){
                                departjoueur2(indiceLigne,i);
                            }break;
                        }

                    }

                } else if (direction == 'O') {
                    for(int i =indiceColonne-1;i>=0;i--){
                        String objet = plateau.getobjet(indiceLigne,i);
                        if (objet.equals("Glace")){
                            plateau.setPlateau(indiceLigne,i,null);
                            break;
                        }
                        else if (objet.equals("Joyau")){
                            if (nbr_joueurs==2){
                                Demitour(joueur);
                            }
                            else if (nbr_joueurs>2){
                                retour_depart(joueur);
                            }
                            break;
                        }
                        else if(objet.contains("E")||objet.contains("O")||objet.contains("N")||objet.contains("S")){//si objet a un char de direction
                            //on considere c'est un joueur
                            if (nbr_joueurs==2){
                                demiTourrouchee(indiceLigne,i);
                            }else if (nbr_joueurs>2){
                                departjoueur2(indiceLigne,i);
                            }break;
                        }

                    }
                } else if (direction == 'N') {
                    for(int i =indiceLigne+1;i<8;i++){
                        String objet = plateau.getobjet(i,indiceColonne);
                        if (objet.equals("Glace")){
                            plateau.setPlateau(indiceLigne,i,null);
                            break;
                        }
                        else if (objet.equals("Joyau")){
                            if (nbr_joueurs==2){
                                Demitour(joueur);
                            }
                            else if (nbr_joueurs>2){
                                retour_depart(joueur);
                            }
                            break;
                        }
                        else if(objet.contains("E")||objet.contains("O")||objet.contains("N")||objet.contains("S")){//si objet a un char de direction
                            //on considere c'est un joueur
                            if (nbr_joueurs==2){
                                demiTourrouchee(indiceLigne,i);
                            }else if (nbr_joueurs>2){
                                departjoueur2(indiceLigne,i);
                            }break;
                        }

                    }
                }
            }
            instruction.removeFirst();//on retire la première instruction de la liste pour pouvoir passer à la suivante
            //joueur.setInstruction(instruction);
            Defaucartes defau = new Defaucartes();
            defau.Cacherleprogramme(instruction);//montre moins de cartes cachees
            plateau.setPlateau(indiceLigne,indiceColonne,null);
            plateau.setPlateau(newLigne,newcolonne,joueur.getName()+direction);//update le plateau
            plateau.afficheplateau();
        }
        //s'il y a un élément à la nouvelle position de la tortue il y a collision, différents effets delon l'objet percuté
        //les effets sont dans la methode collision()


    }//end mouvement

    //action du laser sur le jeu
//end laser


    public void retour_depart(Joueur joueur){
        int[] locinitiale = joueur.getLocini();
        joueur.setLoc(locinitiale[0],locinitiale[1]);
        plateau.setPlateau(locinitiale[0],locinitiale[1],joueur.getName()+joueur.getDirection());
    }//end retour_depart

    public void Demitour(Joueur joueur){
        char direction = joueur.getDirection();
        if (direction =='E'){
            joueur.setDirection('O');
        }
        if (direction =='O'){
            joueur.setDirection('E');
        }
        if (direction =='N'){
            joueur.setDirection('S');
        }
        if (direction =='S'){
            joueur.setDirection('N');
        }
        plateau.setPlateau(joueur.getLocation()[0],joueur.getLocation()[1],joueur.getName()+joueur.getDirection());

    }
    public void demiTourrouchee(int Ligne,int Colonne){
        for (Iterator i = listjoueurs.iterator();i.hasNext();) {
            Joueur joueur2 = (Joueur)i.next();
            if (joueur2.getLocation() == new int[]{Ligne, Colonne}) {
                Demitour(joueur2);
            } }
    }

    public void departjoueur2(int Ligne,int Colonne){
        for (Iterator i = listjoueurs.iterator();i.hasNext();) {
            Joueur joueur2 = (Joueur)i.next();
        if (joueur2.getLocation() == new int[]{Ligne, Colonne}) {
            retour_depart(joueur2);
        } }}

}
