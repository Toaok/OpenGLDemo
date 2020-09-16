package indi.toaok.opengl;


import java.util.ArrayList;

import static indi.toaok.opengl.ListNode.creatLinkedNode;

public class Solution {


    public static void main(String[] args) {
        ListNode one = creatLinkedNode(4, 6, 0, 2, 6, 6, 3, 6, 3, 0, 7, 8, 0, 4, 1, 7, 0, 5, 6, 5, 2, 4, 9, 9, 1, 5, 1, 5);
        ListNode two = creatLinkedNode(6, 2, 7, 8, 6, 4, 7, 0, 9, 3, 0, 3, 6, 2, 5, 6, 0, 9, 6, 2, 7, 4, 2, 7, 1, 0, 9, 0, 5, 6, 5, 4, 9, 1, 8, 9, 3, 4, 0, 2, 1, 8, 8, 2, 2, 0);

        ListNode one1 = creatLinkedNode(4, 6, 0, 2, 6, 6, 3, 6, 3, 0, 7, 8, 0, 4, 1, 7, 0, 5, 6, 5, 2, 4, 9, 9, 1, 5, 1, 5);
        ListNode two1 = creatLinkedNode(6, 2, 7, 8, 6, 4, 7, 0, 9, 3, 0, 3, 6, 2, 5, 6, 0, 9, 6, 2, 7, 4, 2, 7, 1, 0, 9, 0, 5, 6, 5, 4, 9, 1, 8, 9, 3, 4, 0, 2, 1, 8, 8, 2, 2, 0);

//        ListNode p = new LinkedAdd().add3(one,two);
//        ListNode p1 = new LinkedAdd().add4(one1,two1);


//        ArrayList<ListNode> nodes=new ArrayList<>();
//        nodes.add(creatLinkedNode());
//        nodes.add(creatLinkedNode(-1,5,11));
//        nodes.add(creatLinkedNode());
//        nodes.add(creatLinkedNode(6,10));
//        nodes.add(creatLinkedNode(-1,-1,-1));
//        nodes.add(creatLinkedNode(-2,-2,-1));
//        ListNode p=MargeList.mergeKLists(nodes);
        ListNode p=MargeList.mergeKLists(creatLinkedNode(2),creatLinkedNode(1));

        if (p == null) {
            System.out.println("p==null");
        }
        while (p != null) {
            System.out.print(p.val+" ");
            p = p.next;
        }
//        System.out.println();
//        if (p1== null) {
//            System.out.println("p1==null");
//        }
//        while (p1 != null) {
//            System.out.print(p1.val+" ");
//            p1 = p1.next;
//        }

//        int[] sortArry = Sort.sort(new int[]{3,1,5,2,7,4,9,6});
//        for (int i : sortArry) {
//            System.out.print(" " + i);
//        }
    }

}