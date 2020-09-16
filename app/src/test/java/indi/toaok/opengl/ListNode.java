package indi.toaok.opengl;

import java.util.ArrayList;

/**
 * @author user
 * @version 1.0  2020/9/9.
 */
public class ListNode {
    int val;
    ListNode next = null;
    ListNode() {
    }
    ListNode(int val) {
        this.val = val;
    }

    public static ListNode creatLinkedNode(int... args) {
        ListNode head = null;
        ListNode p = null;
        for (int i : args) {
            if (head == null) {
                p = head = new ListNode(i);
                continue;
            }
            p.next = new ListNode(i);
            p = p.next;
        }
        return head;
    }
    public static ListNode creatLinkedNode(ArrayList<Integer> args) {
        ListNode head = null;
        ListNode p = null;
        for (int i : args) {
            if (head == null) {
                p = head = new ListNode(i);
                continue;
            }
            p.next = new ListNode(i);
            p = p.next;
        }
        return head;
    }
}
