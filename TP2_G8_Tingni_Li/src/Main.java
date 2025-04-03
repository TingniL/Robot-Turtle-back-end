import java.util.*;

public class Main {
    public static Scanner scanner = new Scanner(System.in);
    static HashMap<String, ArrayList<String>> bibliotheque = new
            HashMap<>();
    public static void main(String[]args){
        System.out.println("Veuillez entrer un mot");
        String mot = scanner.nextLine();
        mot = mot.toLowerCase();
        System.out.println("Ce mot est palindrome （iterative) " + isPalindrome(mot));
        System.out.println("Ce mot est palindrome （recursive) " + isPalindromeRec(mot));
        System.out.println("Entrez la longue de votre liste");
        int a = scanner.nextInt();
        ArrayList<Integer> liste = new ArrayList<>();
        while (a!=0)
        {
            System.out.println("Entrez un nombre de votre liste ");
            liste.add(scanner.nextInt());
            a--;
        }
        triListe(liste);
        int c = menu();
        while (c!=0){
        switch (c){
            case 1:
                ajoutArtiste();
                c = menu();
                break;
            case 2:
                ajoutAlbum();
                c = menu();
                break;
            case 3:
                listerArtistes();
                c = menu();
                break;
            case 4:
                listerAlbums();
                c = menu();
                break;
            case 5:
                listerAlbumsPourArtiste();
                c = menu();
                break;
            case 6:
                retirerArtiste();
                c = menu();
                break;
            case 7:
                retirerAlbum();
                c = menu();
                break;
            case 0:
                break;
        }}


    }

    public static boolean isPalindrome(String s)
    {
        boolean isPalin = false;
            while(s.length()>1){
                int premier = 0;
                int dernier = s.length()-1;
                if (s.charAt(premier)==s.charAt(dernier)){
                    s = s.substring(premier+1,dernier);
                    isPalin = true;
                }else {
                    isPalin = false;
                    s = s.substring(premier+1,dernier);
                    break;}
        }
        return isPalin;
    }


    public static boolean isPalindromeRec(String s){
        int longue = s.length();
        int indice = (longue-1)/2;
        boolean isPalin = false;
        for( int i =0;i<=indice;i++){
            if(s.charAt(i) != s.charAt(longue-1-i)){
                isPalin = false;
                break;
            }else{
                isPalin = true;
            }
        }
        return isPalin;
    }
    public static void triListe(ArrayList<Integer> liste){
        Collections.sort(liste);
        int j= liste.size();
        for(int i=1;i<=(j/2);i++){
            Collections.swap(liste,i,j-1);
        }
        System.out.println("La liste Triee est "+liste);
    }
    public static int menu(){
        System.out.print("\n" +
                "Bienvenue dans votre bibliothèque musicale !\n" +
                "Tapez 1 pour ajouter un artiste à votre collection.\n" +
                "Tapez 2 pour ajouter un album à votre collection.\n" +
                "Tapez 3 pour lister tous les artistes présents dans votre collection.\n" +
                "Tapez 4 pour lister tous les albums présents dans votre collection.\n" +
                "Tapez 5 pour lister tous les albums d’un artiste donné.\n" +
                "Tapez 6 pour retirer un artiste de votre collection.\n" +
                "Tapez 7 pour retirer un album de votre collection.\n" +
                "Tapez 0 pour quitter.\n" );
        int saisie = scanner.nextInt();
        return saisie;
    }
    public static void ajoutArtiste(){
        Scanner scanner1 = new Scanner(System.in);
        System.out.println("Veuillez entrer l'artiste que vous voules l'ajouter");
        String artiste = scanner1.nextLine();
        if(bibliotheque.containsKey(artiste)){
            System.out.println("Erreur!\n"+
                    "Cettte artiste est deja existe!");
        }else {
            bibliotheque.put(artiste,new ArrayList<>());
        }



    }
    public static void ajoutAlbum(){
        Scanner scanner4 = new Scanner(System.in);
        System.out.println("Veuillez entrer le nom de l'artiste");
        String artiste = scanner4.nextLine();
        System.out.println(("Veuillez entrer le nom de l'album"));
        String album = scanner4.nextLine();

        if(bibliotheque.containsKey(artiste)){
            ArrayList<String> albums = new ArrayList<>(bibliotheque.get(artiste));
            if (albums.contains(album)){
                System.out.println("Erreur!\n"+
                        "Cett album est deja existe!");
            }else {
                albums.add(album);
                bibliotheque.put(artiste,albums);
            }
        }


    }
    public static void listerArtistes(){
        ArrayList<String> artistes = new ArrayList<>(bibliotheque.keySet());
        Collections.sort(artistes,String.CASE_INSENSITIVE_ORDER);
        for (int i =0;i<artistes.size();i++){
            System.out.println(artistes.get(i));
        }


    }
    public static void listerAlbums(){
        Collection albums = bibliotheque.values();
        ArrayList<String> alBums =new ArrayList<String>(albums);
        ArrayList<String> Albums = new ArrayList<>();
        for (int i= 0;i<alBums.size();i++){
            Albums.add(alBums.get(i));
        }
        Collections.sort(Albums,String.CASE_INSENSITIVE_ORDER);
        for (int i =0;i<Albums.size();i++){
            System.out.println(Albums.get(i));
        }

    }
    public static void listerAlbumsPourArtiste(){
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("Veuillez saisir le nom de l'artiste");
        String artiste = scanner2.nextLine();
        if (bibliotheque.containsKey(artiste)){
            ArrayList<String> albums =new ArrayList<>(bibliotheque.get(artiste));
            Collections.sort(albums,String.CASE_INSENSITIVE_ORDER);
            for (int i =0;i<albums.size();i++){
                System.out.println(albums.get(i));
            }
        }else {
            System.out.println("Erreur!\n"+
                    "Cettte artiste n'est pas existe!");
        }


    }
    static void retirerArtiste(){
        Scanner scanner3 = new Scanner(System.in);
        System.out.println("Veuillez saisir le nom de l'artiste");
        String artiste = scanner3.nextLine();
        if (bibliotheque.containsKey(artiste)){
            bibliotheque.remove(artiste);
            System.out.println("Cette artiste est retiree");
        }else {System.out.println("Erreur!\n"+
                "Cettte artiste n'est pas existe!");}

    }
    public static void retirerAlbum(){
        Scanner scanner5 = new Scanner(System.in);
        System.out.println("Veuillez entrer le nom de l'artiste");
        String artiste = scanner5.nextLine();
        System.out.println(("Veuillez entrer le nom de l'album"));
        String album = scanner5.nextLine();
        if(bibliotheque.containsKey(artiste)){
            ArrayList<String> albums =new ArrayList<>(bibliotheque.get(artiste));
            if(albums.contains(album)){
                albums.remove(album);
                bibliotheque.put(artiste,albums);
            }else {System.out.println("Erreur!\n"+
                    "Cett album n'est pas existe!");}
        }

    }





}
