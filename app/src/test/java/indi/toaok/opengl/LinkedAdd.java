package indi.toaok.opengl;

import java.util.ArrayList;

/**
 * @author user
 * @version 1.0  2020/9/9.
 */
public class LinkedAdd {


    public ListNode add(ListNode head1, ListNode head2) {

        head1 = reverseList(head1);
        head2 = reverseList(head2);
        ListNode result = null;
        ListNode p = null;
        while (head1 != null || head2 != null) {

            ListNode current = (p != null && p.next != null) ? p.next : new ListNode(0);
            if (head1 != null) {
                current.val += head1.val;
                head1 = head1.next;
            }
            if (head2 != null) {
                current.val += head2.val;
                head2 = head2.next;
            }
            int carry = current.val / 10;
            current.val %= 10;

            if (result == null) {
                p = result = current;
            } else {
                p.next = current;
                p = current;
            }
            if (carry > 0) {
                p.next = new ListNode(1);
            }


        }
        return reverseList(result);
    }

    public ListNode add2(ListNode head1, ListNode head2) {

        ArrayList<Integer> one = toList(head1);
        ArrayList<Integer> two = toList(head2);

        ArrayList<Integer> result = new ArrayList();
        ListNode p = null;
        for (int i = 0; i < one.size() || i < two.size(); i++) {
            int value = result.size() - 1 == i ? result.get(i) : 0;
            if (i < one.size()) {
                value += one.get(one.size() - i - 1);
            }
            if (i < two.size()) {
                value += two.get(two.size() - i - 1);
            }
            if (result.size() - 1 == i) {
                result.set(i, value % 10);
            } else {
                result.add(value % 10);
            }
            int carry = value / 10;
            if (carry > 0) {
                result.add(1);
            }
        }

        return reverseList(ListNode.creatLinkedNode(result));
    }

    public ListNode add3(ListNode head1, ListNode head2) {

        //先把两个链表翻转
        ListNode one = reverseList(head1);
        ListNode two = reverseList(head2);
        ListNode result=null;
        ListNode p;
        ListNode current = one;
        int carry = 0;
        while (current != null || two != null) {
            if (two != null) {
                current.val += two.val;
                two = two.next;
            }
            carry = current.val / 10;
            current.val %= 10;

            if (current != null) {
                if (current.next == null && (two != null || carry > 0)) {
                    current.next = new ListNode(0);
                }
                //翻转链表
                p = current.next;
                current.next=result;
                result=current;
                current = p;
            }

            if (carry > 0) {
                current.val += 1;
            }

        }
        return result;
    }

    public ListNode add4(ListNode head1, ListNode head2) {

        ListNode one = reverseList(head1);
        ListNode two = reverseList(head2);
        ListNode result=one;
        ListNode current = one;
        int carry = 0;
        while (current != null || two != null) {
            if (two != null) {
                current.val += two.val;
                two = two.next;
            }
            carry = current.val / 10;
            current.val %= 10;

            if (current != null) {
                if (current.next == null && (two != null || carry > 0)) {
                    current.next = new ListNode();
                }
                current = current.next;

//                current = p;
            }

            if (carry > 0 && current != null) {
                current.val += 1;
            }

        }
        return reverseList(result);
    }

    public ArrayList<Integer> toList(ListNode head) {
        ArrayList list = new ArrayList();
        while (head != null) {
            list.add(head.val);
            head = head.next;
        }
        return list;
    }

    public ListNode reverseList(ListNode head) {
        ListNode p = head;
        ListNode pre = null;
        ListNode next = null;
        while (p != null) {
            next = p.next;
            p.next = pre;
            pre = p;
            p = next;
        }
        return pre;
    }

}
