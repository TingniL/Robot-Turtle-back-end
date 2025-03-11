package com.company;

public class Cartes {
    public String name;

    public Cartes(){

    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

}
class Bleu extends Cartes {

    public Bleu(){
        this.name = "Bleu";
    }

}
class Jaune extends Cartes{

    public Jaune() {
        this.name = "Jaune";
    }
}
class Violet extends Cartes{

    public Violet() {
        this.name = "Violet";
    }
}
class Laser extends Cartes{

    public Laser() {
        this.name = "Laser";
    }
}
