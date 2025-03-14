package com.tradingengine;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class Skiplist {
    static final int MAX_LEVEL = 16;
    static final double PROBABILITY = 0.5;

    static class Node {
        Order order;
        AtomicMarkableReference<Node>[] next;

        @SuppressWarnings("unchecked")
        Node(Order order, int level) {
            this.order = order;
            this.next = new AtomicMarkableReference[level + 1];
            for (int i = 0; i <= level; i++) {
                this.next[i] = new AtomicMarkableReference<>(null, false);
            }
        }
    }

    private final Node head = new Node(null, MAX_LEVEL);

    private int randomLevel() {
        int level = 0;
        while (Math.random() < PROBABILITY && level < MAX_LEVEL) {
            level++;
        }
        return level;
    }

    public void add(Order order, boolean isSellOrder) {
        Node[] update = new Node[MAX_LEVEL + 1];
        Node current = head;

        for (int i = MAX_LEVEL; i >= 0; i--) {
            while (current.next[i].getReference() != null &&
                    ((isSellOrder && current.next[i].getReference().order.getPrice() < order.getPrice()) ||
                            (!isSellOrder && current.next[i].getReference().order.getPrice() > order.getPrice()))) {
                current = current.next[i].getReference();
            }
            update[i] = current;
        }

        int level = randomLevel();
        Node newNode = new Node(order, level);
        for (int i = 0; i <= level; i++) {
            newNode.next[i].set(update[i].next[i].getReference(), false);
            update[i].next[i].set(newNode, false);
        }
        System.out.println(" Added order: " + order);
    }

    public Node getLowest() {
        Node current = head.next[0].getReference();
        while (current != null) {
            boolean[] marked = {false};
            Node nextNode = current.next[0].get(marked);

            if (!marked[0]) { //  Ensure we return only active nodes
                return current;
            }
            current = nextNode;
        }
        return null;
    }

    public boolean remove(Node node) {
        synchronized (this) {
            boolean removed = false;
            for (int i = node.next.length - 1; i >= 0; i--) {
                Node current = head;
                while (current.next[i].getReference() != null) {
                    if (current.next[i].getReference() == node) {
                        removed = current.next[i].compareAndSet(node, node.next[i].getReference(), false, false);
                        break;
                    }
                    current = current.next[i].getReference();
                }
            }
            return removed;
        }
    }

    public void printOrders() {
        Node current = head.next[0].getReference();
        if (current == null) {
            System.out.println(" No active orders.");
            return;
        }
        while (current != null) {
            System.out.println(current.order);
            current = current.next[0].getReference();
        }
    }

    public boolean isEmpty() {
        return head.next[0].getReference() == null;
    }
}
