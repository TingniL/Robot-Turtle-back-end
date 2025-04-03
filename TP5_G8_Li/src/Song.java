public class Song {
    private String titre;
    private int duree;
    public Song(){

    }
    public Song(String tittre, int duree){
        this.titre=tittre;
        this.duree=duree;

    }

    public String getTitre(){

        return titre;
    }
    public int getDuree(){
        return duree;
    }
    public String toString(){

        return getTitre()+"("+convertDuration(getDuree())+")";
    }
    public String convertDuration(int duree){
        int mm = duree/60;
        int ss = duree-(mm*60);
        String nduree = mm + ":" +ss;
        return nduree;
    }

}


