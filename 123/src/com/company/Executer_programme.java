package com.company;

import java.util.ArrayDeque;

//execute la liste de carte que le joueur a

public class Executer_programme {

    //au départ la tortue est vers le sud, s'il avance elle ira en bas mais ne doit pas réinitialiser la direction à chaque execution
    private char direction = 'S';
    private int indiceLigne;
    private int indiceColonne;
    private String[][] plateau;
    private ArrayDeque<String> instructions;


    public Executer_programme(){
        System.out.println("test1");
    }
    public void executer_instruction(ArrayDeque<String> instructions,int nbr_joueurs) { //liste des instructions à executer
        System.out.println("test");
        this.instructions = instructions;
        //POUR TESTER MAIS A RETIRER
        instructions.add("violette");
        instructions.add("violette");
        instructions.add("jaune");
        instructions.add("jaune");
        //this.nbr_joueurs =nbr_joueurs;
        this.mouvement(nbr_joueurs);
    }//end executer_instruction

    //action sur la tortues à chaque carte
    private  void mouvement(int nbr_joueurs) {
        System.out.println("test mouvement");
        while (this.instructions.size() != 0) {

            //bleu fait avancer
            if (this.instructions.getFirst().equals("bleu")) {
                if (this.direction == 'E' && (this.indiceColonne + 1) < 8) {
                    this.indiceColonne++;
                } else if (this.direction == 'O' && (this.indiceColonne - 1) > 0) {
                    this.indiceColonne--;
                } else if (this.direction == 'N' && (this.indiceLigne - 1) > 0) {
                    this.indiceLigne--;
                } else if (this.direction == 'S' && (this.indiceLigne + 1) < 8) {
                    this.indiceLigne++;
                }
                else{
                    //retour au point de départ
                    retour_depart(nbr_joueurs);
                }

            }

            //	jaune pour aller à gauche
            if (this.instructions.getFirst().equals("jaune")) {
                if (this.direction == 'E') { //si on est vers l'est et qu'on va à gauche on se retrouver vers le  nord
                    this.direction = 'N';
                } else if (this.direction == 'N') {
                    this.direction = 'O';
                } else if (this.direction == 'O') {
                    this.direction = 'S';
                } else if (this.direction == 'S') {
                    this.direction = 'E';
                }
            }

            //violet pour aller à droite
            if (this.instructions.getFirst().equals("violette")) {
                if (this.direction == 'E') {
                    this.direction = 'S';
                } else if (this.direction == 'S') {
                    this.direction = 'O';
                } else if (this.direction == 'O') {
                    this.direction = 'N';
                } else if (this.direction == 'N') {
                    this.direction = 'E';
                }
            }

            //laser : indentifie s'il y a un element devant la tortue, différentes actions selon l'élément
            //les actions sont listées dans la méthode laser()
            if (this.instructions.getFirst().equals("laser")) {
                if (this.direction == 'E') {
                    for (int i = this.indiceColonne; i < 8; i++) { //on reste sur la même ligne
                        if (this.plateau[this.indiceLigne][i] != null)//si la case du plateau est non vide
                        {
                            laser(nbr_joueurs);//méthode qui indique ce qui se passe selon l'élément qui se trouve en cette case et le nombre de joueurs
                        }
                    }
                } else if (this.direction == 'S') {
                    for (int i = this.indiceLigne; i < 8; i++) { //on reste sur la même colonne
                        if (this.plateau[i][this.indiceColonne] != null) {
                            laser(nbr_joueurs);
                        }
                    }

                } else if (this.direction == 'O') {
                    for (int i = this.indiceColonne; i > 0; i--) { //on reste sur la même ligne
                        if (this.plateau[this.indiceLigne][i] != null) {
                            laser(nbr_joueurs);
                        }
                    }
                } else if (this.direction == 'N') {
                    for (int i = this.indiceLigne; i > 0; i--) { //on reste sur la même colonne
                        if (this.plateau[i][this.indiceColonne] != null) {
                            laser(nbr_joueurs);
                        }
                    }
                }
            }
            this.instructions.removeFirst();//on retire la première instruction de la liste pour pouvoir passer à la suivante
        }

        //s'il y a un élément à la nouvelle position de la tortue il y a collision, différents effets delon l'objet percuté
        //les effets sont dans la methode collision()
        if (this.plateau[this.indiceLigne][this.indiceColonne] != null) {
            collision(nbr_joueurs);
        }

    }//end mouvement

    //action du laser sur le jeu
    private  void laser(int nbr_joueurs) {
        //si le laser touche un mur de glace, il disparait
        if (this.plateau[this.indiceLigne][this.indiceColonne] == "mur glace"||this.plateau[this.indiceLigne][this.indiceColonne] == "mur bois") {
            this.plateau[this.indiceLigne][this.indiceColonne] = null;
        }

        //A CORRIGER
        //si le laser touche une autre tortue, la tortue touchée fait demi tour ou retourne à la case départ
        if (this.plateau[this.indiceLigne][this.indiceColonne] == "tortue")//tortue ou le nom des tortues?
        {
            if (nbr_joueurs == 2) {//tortue TOUCHé fait demi tour, pas la tortue en train de jouer
                if (this.direction == 'E') {
                    this.direction = 'O';
                } else if (this.direction == 'N') {
                    this.direction = 'S';
                } else if (this.direction == 'O') {
                    this.direction = 'E';
                } else if (this.direction == 'S') {
                    this.direction = 'N';
                }
            }
            if (nbr_joueurs >= 2) {
                //tortue touchée retourne à sa case de départ, voir notation pour réinitialisation d'emplacement de la tortue
                retour_depart(nbr_joueurs);
            }
        }

        //si le laser touche un joyau, la tortue fait demi tour ou retourne à la case départ
        if (this.plateau[this.indiceLigne][this.indiceColonne] == "joyau") {
            if (nbr_joueurs == 2) { //la tortue en train de jouer fait demi tour
                if (this.direction == 'E') {
                    this.direction = 'O';
                } else if (this.direction == 'N') {
                    this.direction = 'S';
                } else if (this.direction == 'O') {
                    this.direction = 'E';
                } else if (this.direction == 'S') {
                    this.direction = 'N';
                }

            }
            if (nbr_joueurs >= 2) { //tortue en train de jouer retourne à la case départ
                //tortue touchée retourne à sa case de départ, voir notation pour réinitialisation d'emplacement de la tortue
                retour_depart(nbr_joueurs);
            }
        }
        //rien ne change si le laser touche un mur en pierre
    }//end laser

    private  void collision(int nbr_joueurs) {
        //si deux tortues entrent en collision elles retournent à la case départ
        if (this.plateau[this.indiceLigne][this.indiceColonne] == "tortue")//tortue ou le nom des tortues?
        {
            //les deux tortues retournent au point de départ
            retour_depart(nbr_joueurs);

        }
        //si la tortue entre en collision avec un mur elle fait demi tour
        if (this.plateau[this.indiceLigne][this.indiceColonne] == "mur pierre"||this.plateau[this.indiceLigne][this.indiceColonne] == "mur glace") {
            if (this.direction == 'E') {
                this.direction = 'O';
            } else if (this.direction == 'N') {
                this.direction = 'S';
            } else if (this.direction == 'O') {
                this.direction = 'E';
            } else if (this.direction == 'S') {
                this.direction = 'N';
            }
        }

    }//end collision()

    public void retour_depart(int nbr_joueurs){
        if(nbr_joueurs == 2){

        }
        if(nbr_joueurs == 3){

        }
        if(nbr_joueurs == 4){

        }
    }//end retour_depart
}
