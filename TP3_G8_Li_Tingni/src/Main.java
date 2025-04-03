import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

public class Main {
    static int SIZE = Integer.MAX_VALUE/1000;
    static int[] tableau = new int[SIZE];
    public static void main(String[] args){
        initialiserTableau();
        int[] tableauSlec = new int[SIZE];
        System.arraycopy(tableau,0,tableauSlec,0,SIZE);
        triSlection(tableauSlec);

        int[] tableauInse = new int[SIZE];
        System.arraycopy(tableau,0,tableauInse,0,SIZE);
        triInsetion(tableauInse);

        int[] tableauBulles = new int[SIZE];
        System.arraycopy(tableau,0,tableauBulles,0,SIZE);
        triBulles(tableauBulles);

        int[] tableauQuickSort = new int[SIZE];
        System.arraycopy(tableau,0,tableauQuickSort,0,SIZE);
        Instant start = Instant.now();
        System.out.println("Debut de TRI");
        quiksort(tableauQuickSort,0,tableauQuickSort.length-1);
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();
        System.out.println("Tri par selection a pris " + duration +"ms");

        int[] tableauSort = new int[SIZE];
        System.arraycopy(tableau,0,tableauSort,0,SIZE);
        triJava(tableauSort);

    }
    public static void initialiserTableau(){
        Instant start = Instant.now();
        System.out.println("Debut d'initialisation");
        Random random = new Random();
        for (int i = 0; i < tableau.length;i++){
            tableau[i] = random.nextInt(SIZE);
        }
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();
        System.out.println("L'innisialisation a pris " + duration +"ms");
    }
    public static void triSlection(int[] tableauSlec){
        Instant start = Instant.now();
        System.out.println("Debut de selection");
        for (int i = 0;i<tableauSlec.length-1;i++){
            int indiceMin = i;
            for (int j = i; j<tableauSlec.length;j++){
                if(tableauSlec[j]<tableauSlec[indiceMin]){
                    indiceMin = j;
                }
            }
            int swap = tableauSlec[i];
            tableauSlec[i]= tableauSlec[indiceMin];
            tableauSlec[indiceMin]= swap;
        }
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();
        System.out.println("Tri par selection a pris " + duration +"ms");
    }

    public static void triInsetion(int[] tableau) {
        Instant start = Instant.now();
        System.out.println("Debut d'Insection'");
        for (int i = 1;i<tableau.length;i++){
            int elementATrier = tableau[i];
            int j = i;
            while (j>0 && tableau[j-1]>elementATrier){
                tableau[j] = tableau[j - 1];
                j--;
            }
            tableau[j] = elementATrier;
        }
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();
        System.out.println("Tri par selection a pris " + duration +"ms");
    }
    public static void triBulles(int[] tableau){
        Instant start = Instant.now();
        System.out.println("Debut de TRI");
        boolean estTri = false;
        while (!estTri){
            estTri = true;
            for (int i= 1; i<tableau.length;i++){
                if (tableau[i-1] > tableau[i]){
                int swap = tableau[i-1];
                tableau[i-1] = tableau[i];
                tableau[i] = swap;
                estTri = false;
                }
            }
        }
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();
        System.out.println("Tri par selection a pris " + duration +"ms");
    }

    public static void quiksort(int[] tableau,int indGauche,int indDroit) {
        if (indGauche<indDroit){
            int indicePartition = partition(tableau, indGauche,indDroit);
            quiksort(tableau,indGauche,indicePartition);
            quiksort(tableau,indicePartition+1,indDroit);
        }
    }
    public static int partition(int[] tableau,int indGauche,int indDroit){
        int elementPivot = tableau[indGauche + (indDroit-indGauche) / 2];
        int left = indGauche - 1;
        int right = indDroit + 1;
        while (true){
            do {
                left++;
            }while (tableau[left]<elementPivot);
            do {
                right--;
            }while (tableau[right]>elementPivot);
            if (left >= right) {
                return right; }
            int tmp = tableau[left]; tableau[left] = tableau[right]; tableau[right] = tmp;
        }
    }

    public static void triJava(int[] tableau){
        Instant start = Instant.now();
        System.out.println("Debut de TRI");
        Arrays.sort(tableau);
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();
        System.out.println("Tri par selection a pris " + duration +"ms");
    }
}
