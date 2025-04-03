package com.company;

import javax.naming.Name;

public class Obstacles {
    private int[] location;
    public String Name;

    public void setLocation(int[] location){
        this.location = location;
    }
    public int[] getLoc(){
        return location;
    }
    public String getName(){
        return Name;
    }
}
class MurBois extends Obstacles
{
    MurBois(){
        this.Name = "Bois";
    }
}
class MurGlace extends Obstacles{
    MurGlace(){
        this.Name = "Glace";
    }

}
class MurPierre extends Obstacles{
    MurPierre(){
        this.Name = "Pierre";
    }

}
