package me.pablo.advent2018.day14;

public class Main {

    private static class Node {
        Node prev, next;
        int val;

        Node(int val, Node previous, Node next) {
            this.val = val;
            this.prev = previous;
            this.next = next;
        }

        Node next(int steps) {
            return steps == 0 ? this : next.next(steps - 1);
        }
    }

    private static class Circle {
        private Node end = new Node(3, null, null);
        private int size;

        Circle() {
            end.next = end.prev = end;
        }

        Node append(int val) {
            if (val >= 10) append(val / 10); // Recursive add for independent digits.
            size++;
            return end = end.next = end.next.prev = new Node(val % 10, end, end.next);
        }

    }

    private static final int RECIPES = 760221;  // Part 1 steps to iterate
    private static final String SEQ = "760221"; // Part 2 sequence to find

    public static void main(String[] args) {
        Circle circle = new Circle();
        Node elf1 = circle.end, elf2 = circle.append(7);
        while (true) {
            if (circle.size == RECIPES + 10) System.out.println(text(circle.end, 10)); // Pt 1
            circle.append(elf1.val + elf2.val);
            elf1 = elf1.next(elf1.val + 1);
            elf2 = elf2.next(elf2.val + 1);
            if (text(circle.end, SEQ.length()).equals(SEQ)) {// Triggers twice for 2 inserted values
                System.out.println(circle.size - SEQ.length());
                break;
            }
        }
    }

    private static String text(Node end, int chars) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < chars; i++) output.append((end = end.prev).val);
        return output.reverse().toString();
    }

}
