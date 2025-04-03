import java.util.Iterator;
import java.util.List;

public class Produit implements Comparable{
    private String Name;
    private int codebar;
    private double Prix;
    private int Stock;
    private static List<Alimentaires> alimentaires;
    private static  List<Menagers> menagers;
    private static List<Hygiene> hygienes;
    public Produit(String Name,int codebar,double Prix,int Stock){
        this.Name = Name;
        this.codebar = codebar;
        this.Prix = Prix;
        this.Stock = Stock;
    }
    public Produit(List<Alimentaires> alimentaires,List<Menagers> menagers,List<Hygiene> hygienes) {
        this.alimentaires = alimentaires;
        this.menagers = menagers;
        this.hygienes = hygienes;
    }
    public int getStock(){
        return Stock;
    }
    public String getName(){
        return Name;
    }
    public int getCodebar(){
        return codebar;
    }
    public void modisto(int quan){
        this.Stock = quan;
    }

    @Override
    public int compareTo(Object o) {
        Produit other = (Produit) o;
        return this.getName().compareTo(other.getName());
    }
    public String toString(){

        return (getName()+"            "+getStock()+"\n");
    }
}

class Alimentaires extends Produit {

    public Alimentaires(String Name, int codebar, double Prix, int Stock) {
        super(Name, codebar, Prix, Stock);
    }


}

    class Menagers extends Produit {
    public Menagers(String Name, int codebar, double Prix, int Stock) {
        super(Name, codebar, Prix, Stock);
    }

}
class Hygiene extends Produit {
    public Hygiene(String Name, int codebar, double Prix, int Stock) {
        super(Name, codebar, Prix, Stock);
    }

}