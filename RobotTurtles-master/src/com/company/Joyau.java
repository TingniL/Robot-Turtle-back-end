package com.company;

public class Joyau {
    private int[] index;
    private String Name;
    Joyau(int[] index,String Name){
        this.index = index;
        this.Name = Name;
    }
    public int[] getIndex(){
        return index;
    }
    public String getName(){
        return Name;
    }
}
