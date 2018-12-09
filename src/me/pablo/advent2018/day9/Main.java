package me.pablo.advent2018.day9;

import java.util.Arrays;

public class Main {

    private static class Node {
        Node prev, next;
        long val;

        public Node(long val, Node previous, Node next) {
            this.val = val;
            this.prev = previous;
            this.next = next;
        }
    }

    private static class Circle {
        private Node current = new Node(0, null, null);

        private Circle() {
            current.next = current.prev = current;
        }

        public void step(int val) { // Adds & moves moves 1 forward
            current = current.next = current.next.prev = new Node(val, current, current.next);
            move(1, true);
        }

        public long remove() { // Relies on a move happening after removing, otherwise removing isn't really done.
            current.prev.next = current.next;
            current.next.prev = current.prev;
            return current.val;
        }

        public void move(int steps, boolean forward) {
            if (forward) for (int i = 0; i < steps; i++) current = current.next;
            else for (int i = 0; i < steps; i++) current = current.prev;
        }

    }

    private static int
            PLAYERS = 428,
            //LAST_MARBLE = 72061; // Part 1
            LAST_MARBLE = 7206100; // Part 2

    public static void main(String[] args) {
        Circle circle = new Circle();
        long[] scores = new long[PLAYERS];
        for (int marble = 1, player = 1; marble <= LAST_MARBLE; marble++, player = (player + 1) % PLAYERS) {
            if (marble % 23 == 0) {
                circle.move(8, false);
                scores[player] += marble + circle.remove();
                circle.move(2, true);
            } else circle.step(marble);
        }
        System.out.println(Arrays.stream(scores).max().orElse(0));
    }

}
