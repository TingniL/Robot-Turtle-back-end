package com.company;

public class Cartes {
    private String name;

    public Cartes(String name){
        this.name = name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
}
class Bleu extends Cartes {

    public Bleu(String name) {
        super(name);
    }
}
class Jaune extends Cartes{

    public Jaune(String name) {
        super(name);
    }
}
class Violet extends Cartes{

    public Violet(String name) {
        super(name);
    }
}
class Lazer extends Cartes{

    public Lazer(String name) {
        super(name);
    }
}
}
