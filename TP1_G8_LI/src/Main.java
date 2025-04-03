import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        somme();
        division();
        volume();
        nouveau();

        System.out.println("Bonjour, quel est votre prénom?");
        Scanner scanner = new Scanner(System.in);
        String preNom = scanner.nextLine();
        /*int unEntier = scanner.nextInt();
        float unReel = scanner.nextFloat();
        System.out.println("J’ai recupere un entier: " + unEntier);
        System.out.println("J’ai aussi recupere un reel: " + unReel);*/

        System.out.println("Bonjour, " + preNom);
    }
    public static void somme() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Veuillez saisir le premier entier");
        int premierEntier = scanner.nextInt();

        System.out.println("Veuillez saisir le deuxieme entier");
        int deuxiemeEntier = scanner.nextInt();

        int somme = premierEntier + deuxiemeEntier;
        System.out.println("La somme de "+ premierEntier +" avec " + deuxiemeEntier + " est egal a " +
                somme);
    }
    public static void division(){
        Scanner scanner2 = new Scanner(System.in);

        System.out.println("Veuillez saisir le premier entier");
        int premierEntier = scanner2.nextInt();

        System.out.println("Veuillez saisir le deuxieme entier");
        int deuxiemeEntier = scanner2.nextInt();

        float division = (float)premierEntier / (float)deuxiemeEntier;
        System.out.println("La division de "+ premierEntier +" avec " + deuxiemeEntier + " est egal a " +
                division);
    }
    public static void volume(){
        /*nous avons besoin de 3 variables
        longeur largeur et hauteur
        on a recu par Scanner suite la saisie
        volume = longeur*largeur*hauteur
        on l'affiche en console
         */
        Scanner scanner3 = new Scanner(System.in);

        System.out.println("Veuillez saisir le longeur");
        float lonGeur = scanner3.nextFloat();

        System.out.println("Veuillez saisir l'hauteur");
        float hauTeur = scanner3.nextFloat();

        System.out.println("Veuillez saisir la largeur");
        float larGeur  = scanner3.nextFloat();

        float volume = larGeur * lonGeur * hauTeur;
        System.out.println("Le volume de pavé droit est " + volume);
    }
    public static void nouveau(){
        System.out.println("Saisir un entier");
// On saisit ’11’
        Scanner scanner4 = new Scanner(System.in);
        int entier = scanner4.nextInt();
        System.out.println("Saisir une operation");
// Parce que le caractere newline n’a pas ete lu, c’est lui qui va se
// retrouver dans la variable operation
// Il va aussi etre impossible de saisir une autre valeur pour operation
        String operation = scanner4.nextLine();
    }
}