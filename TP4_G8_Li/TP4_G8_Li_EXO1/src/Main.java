import java.util.ArrayDeque;
import java.util.Scanner;


public class Main {
    public static char[][] plateau;
    public static int[] position;
    public static char direction;
    public static Scanner scanner = new Scanner(System.in);
    public static boolean Jeu = true;
    public static char pion = 'o';
    public static void main(String[]args){
        initialize();
        while (Jeu){
            deplacement(creationFile());
            System.out.println("Vous etre la " + (position[0]+1) +" eme ligne\n"
            + "et la " + (position[1]+1) + " eme coloone\n"
            + "dans la direction " + direction);
            afficherPlateau(plateau);
        }
    }
    public static void afficherPlateau(char[][] plateau) {
        System.out.print("  12345678 \n"
                + " +--------+\n");
        for (int i = 0; i < 8; i++) {
            System.out.print(i + "|");
            for (int j = 0; j < 8; j++) {
                System.out.print(plateau[i][j]);
            }
            System.out.print("|" + '\n');
        }
        System.out.print(" +--------+");
    }
    public static void initialize(){
        plateau = new char[8][8];
        position = new int[]{7, 0};
        direction = 'E';
        plateau[position[0]][position[1]] = pion;
    }
    public static ArrayDeque<String> creationFile(){
        ArrayDeque<String> instruction = new ArrayDeque<>();
        while (instruction.size()< 5){
            System.out.println("Voulez vous Avancer: Entrez A\n" +
                "Un quart tour vers la gauche : Entrez G\n" +
                "Un quart tour vers la droit : Entrez D\n");
            String a = scanner.nextLine();
            if(a.equals("A")||a.equals("G")||a.equals("D")){
                instruction.addLast(a);
            }
            else {System.out.println("Mauvais sasition");
            continue;}
        };
        return instruction;

    }

    public static void deplacement(ArrayDeque<String> instruction){
        String[] ins = copyFile(instruction);
        for(int i = 0;i<5;i++){
            String a = ins[i];
            if(a.equals("A")){
                if(direction=='E'){
                    int b =position[1]+1;
                    if(b >= 0 && b < 8){
                        plateau[position[0]][position[1]] = 32;
                        position[1] =b;
                        plateau[position[0]][position[1]] = pion;
                        instruction.addLast(a);
                    }
                }else if(direction =='S'){ ;
                    int b =position[0]+1;
                    if(b >= 0 && b < 8){
                        plateau[position[0]][position[1]] = 32;
                        position[0] =b;
                        plateau[position[0]][position[1]] = pion;
                        instruction.addLast(a);
                    }
                }else if(direction =='W'){ ;
                    int b =position[1]-1;
                    if(b >= 0 && b < 8){
                        plateau[position[0]][position[1]] = 32;
                        position[1] =b;
                        plateau[position[0]][position[1]] = pion;
                        instruction.addLast(a);
                    }
                }else if(direction =='N'){ ;
                    int b =position[0]-1;
                    if(b >= 0 && b < 8){
                        plateau[position[0]][position[1]] = 32;
                        position[0] =b;
                        plateau[position[0]][position[1]] = pion;
                        instruction.addLast(a);
                    }
                }
            } else if (a.equals("G")) {
                if(direction=='E'){
                    direction = 'N';
                }else if(direction =='S'){ ;
                    direction = 'E';
                }else if(direction =='W'){ ;
                    direction = 'S';
                }else if(direction =='N'){ ;
                    direction = 'W';
                }
            } else if (a.equals("D")) {
                if(direction=='E'){
                    direction = 'S';
                }else if(direction =='S'){ ;
                    direction = 'W';
                }else if(direction =='W'){ ;
                    direction = 'N';
                }else if(direction =='N'){ ;
                    direction = 'E';
                }
            }
        }
    }
    public static String[] copyFile(ArrayDeque<String> File){
        String[] ins = new String[5];
        for(int i = 0;i<5;i++){
            ins[i] = File.pollFirst();
        }
        return ins;
    }
}
