package com.company;


import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    ArrayList<Cartes> Deck = new ArrayList<Cartes>();
    private ArrayList<Cartes> cartes;

    public Deck()
    {
        cartes = new ArrayList<Cartes>();

        for(int a =0; a<=18; a++)
        {
            Bleu.add(new Cartes("Bleu"));
            }
        for(int b= 0; b<8; b++)
        {
            cartes.add(new Cartes("Jaune"));
            }
        for(int c= 0; c<8; c++)
        {
            cartes.add(new Cartes("Violet"));
            }
        for(int d= 0; d<3; d++)
        {
            cartes.add(new Cartes("Laser"));
            }
        Collections.shuffle(this.Deck);
    }

    public double size() {
        return cartes.size();
    }

    public Object get(int carRam) {
        return cartes.get(carRam);
    }

    public void remove(String carte) {
        cartes.remove(carte);
    }
}
