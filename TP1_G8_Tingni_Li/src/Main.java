import javax.swing.event.MenuKeyListener;
import java.util.Scanner;

public class Main {
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("voulez-vous encoder un message ou jouer morpion?\n" +
                "1.encoder un message\n" +
                "2.Morpion\n");
        int a = scanner.nextInt();
        switch (a) {
            case 1:
                System.out.println("Entrez votre message");
                String message = scanner.nextLine();
                String messageEncodeur = messageEncodeur(message);
                System.out.println(messageEncodeur);
                break;
            case 2:
                morpion();
                break;
        }
    }


    public static String messageEncodeur(String message) {
        int tailleM = message.length();
        char[] messageEnco = new char[tailleM];
        for (int i = 0; i < tailleM; i++) {
            char unChar = message.charAt(i);
            int unEn = unChar;
            if (unEn == 32) {
                char unChara = (char) unEn;
                messageEnco[i] = unChara;
            } else if (unEn >= 97 || unEn <= 122) {
                if (unEn % 2 == 0) {
                    if (unEn < 100) {
                        int unEnt = 123 - (100 - unEn);
                        char unChara = (char) unEnt;
                        messageEnco[i] = unChara;
                    } else {
                        int unEnt = unEn - 3;
                        char unChara = (char) unEnt;
                        messageEnco[i] = unChara;
                    }
                } else if (unEn % 2 == 1) {
                    if (unEn >= 120) {
                        int unEnt = 99 - (122 - unEn);
                        char unChara = (char) unEnt;
                        messageEnco[i] = unChara;
                    } else {
                        int unEnt = unEn + 3;
                        char unChara = (char) unEnt;
                        messageEnco[i] = unChara;
                    }
                }
            } else {
                System.out.println("Invalide,veuillez entrez que les lettres miniscules et les espaces");
            }
        }
        String messageEncodeur = String.valueOf(messageEnco);
        return messageEncodeur;
    }

    public static char[][] plateau;
    public static char joueur1 = 'O';
    public static char joueur2 = 'X';
    public static boolean estJoueur1 = true;

    public static void morpion() {
        initialiserPlateau();
    }

    public static void initialiserPlateau() {
        plateau = new char[3][3];
        afficherPlateau(plateau);
        updatePlateau(saisieJoueur());
        finDuJeu();

    }

    public static void afficherPlateau(char[][] plateau) {
        System.out.print("  012 \n"
                + " +---+\n");
        for (int i = 0; i < 3; i++) {
            System.out.print(i + "|");
            for (int j = 0; j < 3; j++) {
                System.out.print(plateau[i][j]);
            }
            System.out.print("|" + '\n');
        }
        System.out.print(" +---+");
    }

    public static int[] saisieJoueur() {
        System.out.println("Saisir la ligne entre 0 et 2");
        int ligne = scanner.nextInt();
        System.out.println("Saisir la colonne entre 0 et 2");
        int colonne = scanner.nextInt();
        int[] indice = {ligne, colonne};
        saisieValide(ligne, colonne);
        return indice;
        estJoueur1 = false;
    }

    public static boolean saisieValide(int ligne, int colonne) {
        if (ligne >= 0 && ligne <= 2) {
            if (colonne >= 0 && colonne <= 2) {
                if (plateau[ligne][colonne] == 32) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void updatePlateau(int[] saisie) {
        int ligne = saisie[0];
        int colonne = saisie[1];
        if(estJoueur1){
            plateau[ligne][colonne]=joueur1;
        }else{plateau[ligne][colonne]=joueur2;}
        afficherPlateau(plateau);
    }

    public static boolean finDuJeu() {
        if(alignementGagnant()){return true;}
        else if(plateauEstComplet()){return true;}
        else {return false;}
    }

    public static boolean plateauEstComplet() {
        int com=0;
        for(int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if(plateau[i][j]!='\u0000'){
                    com++;
                }
            }
        }
        if(com==9){return true;}
        else {return false;}
    }
    public static boolean alignementGagnant() {
        for (int i = 0; i < 3; i++) {
            if (plateau[i][0] == plateau[i][1]) {
                if (plateau[i][0] == plateau[i][2]) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        for (int j = 0; j < 3; j++) {
            if (plateau[0][j] == plateau[1][j]) {
                if (plateau[0][j] == plateau[2][j]) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (plateau[0][0] == plateau[1][1]) {
            if (plateau[0][0] == plateau[2][2]) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
        if (plateau[2][0] == plateau[1][1]) {
            if (plateau[2][0] == plateau[0][2]) {
            return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
