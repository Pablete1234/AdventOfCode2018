package me.pablo.advent2018.day8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

public class Main {

    private static class Node {
        Node[] child;
        int[] metadata;

        Node(IntSupplier next) {
            child = new Node[next.getAsInt()];
            metadata = new int[next.getAsInt()];
            for (int i = 0; i < child.length; i++) child[i] = new Node(next);
            for (int i = 0; i < metadata.length; i++) metadata[i] = next.getAsInt();
        }

        private Stream<Node> children() {
            return Stream.concat(Stream.of(this), Arrays.stream(child).flatMap(Node::children));
        }

        private int value() {
            return child.length == 0 ? Arrays.stream(metadata).sum() : Arrays.stream(metadata)
                    .map(md -> md - 1)
                    .map(md -> md >= child.length ? 0 : child[md].value()).sum();
        }
    }

    public static void main(String[] args) throws IOException {
        PrimitiveIterator.OfInt iterator = Files.lines(Paths.get("input.txt"))
                .flatMap(s -> Arrays.stream(s.split(" "))).mapToInt(Integer::parseInt).iterator();
        Node parent = new Node(iterator::next);

        System.out.println(parent.children().flatMapToInt(n -> Arrays.stream(n.metadata)).sum());
        System.out.println(parent.value());
    }

}
