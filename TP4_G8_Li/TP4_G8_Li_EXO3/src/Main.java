import java.util.*;
import java.util.Scanner;
import javafx.scene.transform.MatrixType;
import org.w3c.dom.Node;

public class Main {
    public static int disMax = Integer.MAX_VALUE/10;
    public static int[][] A =new int[10][10];
    public static void main(String[] args) {
        for(int i = 0;i<A.length;i++){
            for (int j = 0;j<A.length;j++){
                A[i][j]=disMax;
            }
        }
        A[0][1]=85;A[0][2]=217;A[0][4]=173;
        A[1][5]=80;A[1][0]=85;
        A[2][6]=186;A[2][7]=103;A[2][0]=217;
        A[3][7]=183;
        A[4][9]=502;A[4][0]=173;
        A[5][8]=250;A[5][1]=80;
        A[6][3]=186;
        A[7][9]=167;A[7][2]=103;A[7][3]=183;
        A[8][9]=84;A[8][5]=250;
        A[9][4]=502;A[9][7]=167;A[9][8]=84;

        Dijkstra(A,0,9,disMax);

    }
    public static void Dijkstra(int[][] A,int s,int t,int disMax){
        boolean[] passe = new boolean[A.length];
        int[] d = new int[A.length];

        for (int i = 0; i<A.length;i++){
            passe[i]=false;
            d[i] = disMax;
        }

        passe[s] = true;
        d[s]=0;

        int node = A.length;
        int Q = s;

        while (node > 0 && !passe[t]) {
            int disMin = disMax;
            for(int i =0;i<d.length;i++){
                if(disMin >d[i] && !passe[i]){
                    Q = i;
                    disMin = d[i];
                }
            }
            for (int i=0;i<A.length;i++){
                if (d[Q] + A[Q][i] < d[i]){
                    d[i] = d[Q]+A[Q][i];
                }}
            node--;
            passe[Q]=true;}
        System.out.println("distance minimum est " +d[t]);
    }



}
