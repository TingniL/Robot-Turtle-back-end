import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner13 = new Scanner(System.in);
        System.out.print("\n" +
                "Quel exercice ? Saisissez :\n" +
                "1. Discriminant\n" +
                "2. Parité d’un nombre\n" +
                "3. Calcul d’extremum\n" +
                "4. Égalité entre chaînes de caractères\n" +
                "5. Factorielle\n" +
                "6. Compte à rebous\n" +
                "7. Valeurs de carrés\n" +
                "8. Table de multiplication\n" +
                "9. Division d’entiers\n" +
                "10. Règle graduée\n" +
                "11. Nombres premiers\n" +
                "12. Manipulations sur un tableau\n");
        int a;
        do {
            System.out.print( "Veuillez saisir entre 1 et 12 :\n" +
                    "1. Discriminant\n" +
                    "2. Parité d’un nombre\n" +
                    "3. Calcul d’extremum\n" +
                    "4. Égalité entre chaînes de caractères\n" +
                    "5. Factorielle\n" +
                    "6. Compte à rebous\n" +
                    "7. Valeurs de carrés\n" +
                    "8. Table de multiplication\n" +
                    "9. Division d’entiers\n" +
                    "10. Règle graduée\n" +
                    "11. Nombres premiers\n" +
                    "12. Manipulations sur un tableau\n");
            a = scanner13.nextInt();
        } while (a < 1 || a > 12);
        switch (a) {
            case 1:
                discriminant();
                break;
            case 2:
                parite();
                break;
            case 3:
                max();
                min();
                break;
            case 4:
                egaliteStr();
                break;
            case 5:
                factorielle();
                break;
            case 6:
                countdown();
                break;
            case 7:
                carres();
                break;
            case 8:
                tableMultipication();
                break;
            case 9:
                division();
                break;
            case 10:
                regle();
                break;
            case 11:
                nombrePremier();
                break;
            case 12:
                initialisationTableau();
                break;
            }
        }

    public static void discriminant() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Quelle est la valeur de a ?");
        int a = scanner.nextInt();
        System.out.println("Quelle est la valeur de b ?");
        int b = scanner.nextInt();
        System.out.println("Quelle est la valeur de c ?");
        int c = scanner.nextInt();
        int delta = (int) Math.pow(b, 2) - 4 * a * c;
        if (delta < 0) {
            System.out.println("Ce polynome n’a pas de racine reelle");
        }  else if (delta == 0){
            int x = (-b)/(2*a);
            System.out.println("Ce polynome a une racine double, elle est " + x);
        }   else {
            float x0 = (float) (-b + Math.sqrt(delta) )/(2*a);
            float x1 = (float) (-b - Math.sqrt(delta) )/(2*a);
            System.out.println("Ce polynome a deux racine , elles sont " + x0 +" et "+ x1);
        }
    }
    public static void parite(){
        Scanner scanner1 = new Scanner(System.in);
        System.out.println("Veuillez saisir un entier");
        int a = scanner1.nextInt();
        if (a%2 == 0){
            System.out.println("Cet entier" + a +" est un chiffre pair");
        }   else {
            System.out.println("Cet entier" + a + " est un chiffre impair");
        }
    }
    public static void max(){
        Scanner scanner3 = new Scanner(System.in);
        System.out.println("Veuillez saisir un entier");
        int a = scanner3.nextInt();
        System.out.println("Veuillez saisir un autre entier");
        int b = scanner3.nextInt();
        if(a > b){
            System.out.println("La valeur maximum est " + a);
        }   else if(b > a){
            System.out.println("La valeur maximum est " + b);
        }
    }
    public static void min(){
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("Veuillez saisir un entier");
        int a = scanner2.nextInt();
        System.out.println("Veuillez saisir un autre entier");
        int b = scanner2.nextInt();
        if(a > b){
            System.out.println("La valeur minimum est " + b);
        }   else if(b > a){
            System.out.println("La valeur minimum est " + a);
        }

    }
    public static void egaliteStr(){
        Scanner scanner4 = new Scanner(System.in);
        System.out.println("Veuillez saisir une chaine de caractere");
        String a = scanner4.nextLine();
        System.out.println("Veuillez saisir une autre chaine de caractere");
        String b = scanner4.nextLine();
        boolean test = a.equals(b);
        if(test){
            System.out.println("Ces deux chaine de caractere sont identiques");
        }   else{
            System.out.println("Ces deux chaine de caractere sont differentes");
        }
    }
    public static void factorielle() {
        Scanner scanner5 = new Scanner(System.in);
        int n;
        do {
            System.out.println("Saisir un entier positif ou nul");
            n = scanner5.nextInt();
        } while( n <0 );
        int factorielle = 1;
        for (int i = 1; i <= n; i++) {
            factorielle *= i;
        }
        System.out.println(n + "! = " + factorielle);
    }


     public static void countdown(){
        int a = 10;
            for (int i = 1; a >= i;){
                a -= i;
                if(a>0){
                    System.out.println(a);}
                else if(a == 0){
                    System.out.println("BOOM");
                }
            }
        }
    public static void carres(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Saisir un entier ");
        int a = scanner.nextInt();
        int b = a * a;
        int[] table = {a, b};
        System.out.println(table[0]+" son carre est "+ table[1]);
    }
    public static void tableMultipication(){
    for (int i = 1; i <= 10; i++){
        for (int j = 1; j<= 10; j++){
        System.out.print(i*j +" ");
        }
        System.out.print("\n");
    }
    }
    public static void division(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Veuillez saisir un entier positif comme numerateur");
        int nume =scanner.nextInt();
        int deno;
        do {
            System.out.println("Veuillez saisir un entier et pas de 0");
            deno = scanner.nextInt();
        } while (deno == 0);
        float divi = nume / deno ;
        System.out.println("la division entre " + nume + " et "+deno+" est "+divi);
    }
    public static void regle(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Longeur ?");
        int longe =scanner.nextInt();
        for (int i = 0; i<= longe ; i++ ){
            int a = i % 10;
            if(a == 0){
                System.out.print("|");
            } else{
                System.out.print("-");
            }
        }
    }
    public static void nombrePremier(){
        Scanner scanner = new Scanner(System.in);
        int nombre ;
        do {
            System.out.println("Veuillez saisir un entier positif");
            nombre = scanner.nextInt();
        } while (nombre <= 0);
        if (nombre < 2){
            System.out.println("ce n'est pas un nombre premier");
        }
        else {int a = 0;
            for (int i = 1; i <= nombre; i++)
            {if (nombre % i == 0) { a++;}}
            if (a ==2 ){System.out.println("c'est  un nombre premier");
            }   else {System.out.println("ce n'est pas un nombre premier");}
        }
    }

    public static void initialisationTableau(){
        int[] tableau = new int[20];
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < tableau.length; i++) {
            System.out.println("Saisir un entier");
            int entier = scanner.nextInt();
            tableau[i] = entier;
        }
        int[] mami = {tableau[0],tableau[0]};
        int somme = 0;
        int[] invtableau = inverseTableau(tableau);
        for (int j = 0; j < tableau.length;j++){
            if(mami[0] < tableau[j]){mami[0]=tableau[j];}
            if(mami[1] > tableau[j]){mami[1]=tableau[j];}
            somme += tableau[j];
            if((tableau[j])%2 ==0){System.out.println("Ce nombre impair "+ tableau[j]+
                    ", il se situe a tableau[" + j+"]"); }
        }
        System.out.println("La tableau inverse est ");
        for (int k = 0; k < invtableau.length;k++){
            System.out.print(invtableau[k]+" ");
        }
        System.out.print("\n");
        System.out.println("La somme de ce tableau est "+somme);
        System.out.println("la valeur maximum dans cette liste est "+mami[0]+
                " et le minimum est " +mami[1]);
        }

    private static int[] inverseTableau(int[] tableau) {
        int[] inVtableau = new int[20];
        for (int i = 19;i >=0;){
            for(int j = 0; j<tableau.length;j++){
                inVtableau[j]=tableau[i];
                i--;
            }
        }
        return inVtableau;
    }
}

