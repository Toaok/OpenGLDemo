package indi.toaok.opengl;

/**
 * @author user
 * @version 1.0  2020/9/9.
 */


public class ReverseList {
    public ListNode reverseList(ListNode head) {
        ListNode p = head;
        ListNode pre = null;
        ListNode next = null;
        while (p != null) {
            next = p.next;
            p.next = pre;
            pre = p;
            p = p.next;
        }
        return pre;
    }
}
