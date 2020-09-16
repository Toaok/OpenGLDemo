package indi.toaok.opengl;

import java.util.*;


public class LRU {
    /**
     * lru design
     *
     * @param operators int整型二维数组 the ops
     * @param k         int整型 the k
     * @return int整型一维数组
     */
    public int[] lru(int[][] operators, int k) {
        // write code here
        LinkedHashMap<Integer, Integer> lruMap = new LinkedHashMap<>(0, 0.75f, true);
        ArrayList<Integer> results = new ArrayList();
        for (int[] opt : operators) {
            int key = opt[1];
            switch (opt[0]) {
                case 1:
                    int value = opt[2];
                    if (lruMap.size() < k) {
                        lruMap.put(key, value);
                    } else {
                        Iterator iterable = lruMap.keySet().iterator();
                        lruMap.remove(iterable.next());
                        lruMap.put(key, value);
                    }
                    break;
                case 2:
                    if (lruMap.containsKey(key)) {
                        int v = lruMap.get(key);
                        results.add(v);
                    } else {
                        results.add(-1);
                    }
                    break;
                default:
                    break;
            }

        }
        int[] resultArry = new int[results.size()];
        for (int i = 0; i < results.size(); i++) {
            resultArry[i] = results.get(i);
        }
        return resultArry;
    }

}