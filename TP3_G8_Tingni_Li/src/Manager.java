import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

public class Manager {
    private TreeSet<Produit> listproduits;
    private TreeSet<Alimentaires> listalim;
    private  TreeSet<Menagers> listmenag;
    private  TreeSet<Hygiene> listhyg;

    public static Scanner scanner = new Scanner(System.in);
    Manager(){

    }

    public void start(){
        TreeSet<Produit> listproduits =  new TreeSet<Produit>();
        TreeSet<Alimentaires> listalim = new TreeSet<Alimentaires>();
        TreeSet<Menagers> listmenag = new TreeSet<Menagers>();
        TreeSet<Hygiene> listhyg = new TreeSet<Hygiene>();
        Initialisation(listproduits,listalim,listmenag,listhyg);
        this.listproduits = listproduits;
        this.listalim = listalim;
        this.listmenag = listmenag;
        this.listhyg = listhyg;
        while(true){
            displayMenu();
            int a = scanner.nextInt();
            switch(a){
                case 1:
                    affiAlim();
                    break;
                case 2:
                    affiMena();
                    break;
                case 3:
                    affiHyg();
                    break;
                case 4:
                    affitous();
                    break;
                case 5:
                    modifStock();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid saisit");
                    break;
            }
            }
    }
    public static TreeSet<Produit> Initialisation(TreeSet<Produit> listproduits, TreeSet<Alimentaires> listalim, TreeSet<Menagers> listmeag, TreeSet<Hygiene> listhyg) {
        Produit evian = new Alimentaires("Evian", 1, 0.99, 3);
        Produit Volvic = new Alimentaires("Volvic", 2, 0.95, 2);
        Produit Dececco = new Alimentaires("Spaghetti De Cecco", 3, 1.65, 5);
        Produit Rummo = new Alimentaires("Spaghetti Rummo", 4, 2.15, 10);
        Produit Paic = new Menagers("Liquide vailselle Paic", 5, 1.60, 12);
        Produit Mir = new Menagers("Liauide vailselle Mir", 6, 2.57, 13);
        Produit Soupline = new Menagers("Assouplissant Soupline", 7, 2.75, 8);
        Produit Lenor = new Menagers("Assouplissant Lenor", 8, 3.49, 9);
        Produit Sensodyne = new Hygiene("Dentifrice Sensodyne", 9, 3.41, 9);
        Produit Colgate = new Hygiene("Dentifrice Colgate", 10, 1.26, 10);
        Produit Dove = new Hygiene("Gel douche Dove", 11, 3.79, 4);
        Produit Ushuia = new Hygiene("Gel douche Ushuaia ", 12, 2.75, 0);
        listalim.add((Alimentaires) evian);
        listalim.add((Alimentaires)Volvic);
        listalim.add((Alimentaires)Rummo);
        listalim.add((Alimentaires)Dececco);
        listmeag.add((Menagers) Paic);
        listmeag.add((Menagers) Mir);
        listmeag.add((Menagers) Soupline);
        listmeag.add((Menagers) Lenor);
        listhyg.add((Hygiene) Sensodyne);
        listhyg.add((Hygiene) Colgate);
        listhyg.add((Hygiene) Dove);
        listhyg.add((Hygiene) Ushuia);
        listproduits.addAll(listalim);
        listproduits.addAll(listmeag);
        listproduits.addAll(listhyg);
        return listproduits;
    }

    public void displayMenu(){
        System.out.print("\n" +
                "Bienvenue sur le logiciel de gestion de SuperIsep !\n" +"\n"+
                "Faites votre choix :\n" +
                "(1) Afficher le stock de tous les produits alimentaires.\n" +
                "(2) Afficher le stock de tous les produits ménagers.\n" +
                "(3) Afficher le stock de tous les produits d’hygiène.\n" +
                "(4) Afficher le stock de tous les produits du supermarché.\n" +
                "(5) Mettre à jour le stock d’un produit en particulier.\n"+
                "(0) Quitter le logiciel.\n");
}
    public void affiAlim(){
        System.out.println("Produits alimentaires");
        System.out.println("    Nom                     Quantité");
        for (Iterator i = listalim.iterator(); i.hasNext();){
            Alimentaires ali=(Alimentaires)i.next();
            System.out.println(ali.toString());
        }
    }

    public void affiMena(){
        System.out.println("Produits ménagers");
        System.out.println("    Nom                     Quantité");
        for (Iterator i = listmenag.iterator(); i.hasNext();){
            Menagers menagers =(Menagers) i.next();
            System.out.println(menagers.toString());
        }
    }
    public void affiHyg(){
        System.out.println("Produits d’hygiène");
        System.out.println("    Nom                     Quantité");
        for (Iterator i = listhyg.iterator(); i.hasNext();){
            Hygiene hygiene =(Hygiene) i.next();
            System.out.println(hygiene.toString());
        }
    }
    public void affitous(){
        System.out.println("Produit");
        affiAlim();
        affiMena();
        affiHyg();
    }
    public void modifStock(){
        Scanner sca = new Scanner(System.in);
        System.out.println("Veuillez saisir le code barre du produit en question");
        int cb = sca.nextInt();
        System.out.println("Veuillez entrer la nouvelle quantité");
        int nq = sca.nextInt();
        Produit pro = getProduitByCB(cb);
        if (pro!=null||nq>=0){
            pro.modisto(nq);
            System.out.println("la modification a bien été prise en compte");
        }else {System.out.println("Veuillez vérifier le code barre ou la quantité");}
    }

    private Produit getProduitByCB (int cB){
        for (Iterator i = listproduits.iterator(); i.hasNext(); ) {
            Produit produit = (Produit) i.next();
            if (produit.getCodebar()==cB) {
                return produit;
            };
        }
        return null;
    }

}

