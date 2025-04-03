import java.util.Iterator;
import java.util.List;

public class Album {
    private String titre;
    private int anneedesort;
    private List<Song> chansons;
    public Album(){
    }
    public Album(String titre,int anneedesort,List<Song> chansons){
        this.titre = titre;
        this.anneedesort = anneedesort;
        this.chansons = chansons;
    }
    public String getTitre(){
        return titre;
    }
    public String toString(){
        String to = titre +'\n';
        int j=1;
        for(Iterator i = chansons.iterator();i.hasNext();){
            to += j+(" - ");
            to += i.next();
            to +='\n';
            j++;
        }

        return to;
    }
}
