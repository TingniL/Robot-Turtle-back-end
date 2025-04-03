package com.company;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class Joueur implements Comparable{
    int[] loc;
    String Name;
    private char direction;
    ArrayDeque<String> instruction;
    ArrayList<Cartes> listcartes;
    ArrayList listdefauss;
    ArrayList listpioche;
    ArrayList<Obstacles> listobstacles;
    int[] locini;
    @Override
    public int compareTo(Object o) {
        Joueur other = (Joueur) o;
        return this.getName().compareTo(other.getName());
    }
    Joueur(int[] loc, String Name, ArrayList<Cartes> listcartes,ArrayList<Obstacles> obstacles) {
        this.loc = loc;
        this.Name = Name;
        this.listcartes = listcartes;
        this.direction = 'S';
        this.listobstacles = obstacles;
        this.locini = loc;
    }

    public String getName() {
        return Name;
    }

    public int[] getLocation() {
        return loc;
    }


    public ArrayList<Cartes> getListcartes(){
        return listcartes;
    }
    public ArrayList getListpioche(){
        return listpioche;
    }
    public ArrayDeque<String> getInstruction(){
        return instruction;
    }
    public char getDirection(){
        return direction;
    }
    public int[] getLocini(){
        return locini;
    }
    public ArrayList<Obstacles> getListobstacles(){
        return listobstacles;
    }

    //pour update les cartes dans un joueur
    public void setInstruction(ArrayDeque ins){
        this.instruction = ins;
    }
    public void setListdefauss(ArrayList defau){
        this.listdefauss = defau;
    }
    public void setListcartes(ArrayList<Cartes> cartes){
        this.listcartes = cartes;
    }
    public void setListpioche(ArrayList listpio) {
        this.listpioche = listpio;
    }
    public void setDirection(char direc){
        this.direction = direc;
    }
    public void setLoc(int L,int C){
        this.loc= new int[]{L, C};
    }
    public void setListobstacles(ArrayList<Obstacles> ob){
        this.listobstacles = ob;
    }
}