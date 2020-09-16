package indi.toaok.opengl;

/**
 * @author user
 * @version 1.0  2020/9/10.
 */
public class Sort {

    public static int[] sort(int[] arry) {
        for (int i = 0; i < arry.length-1; i++) {
            for (int j = i+1; j < arry.length; j++) {
                if (arry[i] > arry[j]) {
                    int temp = arry[i];
                    arry[i] = arry[j];
                    arry[j] = temp;
                }
            }
            for (int a : arry) {
                System.out.print(" " + a);
            }
            System.out.println();
        }
        return arry;
    }
}
