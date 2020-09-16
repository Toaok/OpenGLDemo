package indi.toaok.opengl;

import java.util.ArrayList;

/**
 * @author user
 * @version 1.0  2020/9/10.
 */
public class MargeList {
    public static ListNode mergeKLists(ArrayList<ListNode> lists) {
        ListNode resultList = null;
        ListNode firstList = null;
        ListNode lastListHead = null;
        for (int i = 0; i < lists.size(); i++) {
            firstList = resultList;
            ListNode headList = lists.get(i);
            if (firstList == null) {
                resultList = firstList = headList;
                continue;
            }
            ListNode fristPre = null;
            ListNode fristNext = null;

            ListNode headPre = null;
            ListNode headNext = null;
            while (firstList != null || headList != null) {
                if (headList == null) {
                    break;
                }
                if (firstList.next == null && firstList.val <= headList.val) {
                    firstList.next = headList;
                    break;
                }
                if (firstList.val > headList.val) {
                    if (fristPre != null) {
                        fristPre.next = headList;//将fristList的前一个节点的下一个节点指向headList
                    } else {
                        resultList = headList;
                    }
                    fristPre = headList;//将fristList的前一个节点后移一位
                    headNext = headList.next;
                    headList.next = firstList;
                    headList = headNext;
                } else {
                    fristPre = firstList;
                    firstList = firstList.next;
                }
            }
        }
        return resultList;
    }

    public static ListNode mergeKLists(ListNode l1, ListNode l2) {
        ListNode results = null;
        ListNode rootList = null;
        results = rootList = l1;
        if (rootList == null) {
            results = l2;
        } else {
            ListNode preRootList = null;
            ListNode nextl2 = null;
            while (rootList != null || l2 != null) {
                if (l2 == null) break;
                if (rootList.next == null && rootList.val <= l2.val) {
                    rootList.next = l2;
                    break;
                }
                if (rootList.val > l2.val) {
                    nextl2 = l2.next;
                    if (preRootList == null) {
                        results=l2;
                        preRootList = l2;
                        l2.next = rootList;
                    } else {
                        preRootList.next = l2;
                        preRootList=l2;
                        l2.next = rootList;
                    }
                    l2 = nextl2;
                } else {
                    preRootList = rootList;
                    rootList = rootList.next;
                }

            }
        }
        return results;
    }
}

