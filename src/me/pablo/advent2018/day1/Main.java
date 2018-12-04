package me.pablo.advent2018.day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    // Solution for part 1
    public static void main1(String[] args) throws IOException {
        System.out.println(Files.lines(Paths.get("input.txt"))
                .mapToInt(Integer::parseInt).sum());
    }

    // Solution for part 2
    public static void main(String[] args) throws IOException {
        Iterator<Integer> it = new InfiniteIterator<>(Files.lines(Paths.get("input.txt"))
                .map(Integer::parseInt)
                .collect(Collectors.toList()));

        int total = 0;
        Set<Integer> frequencies = new HashSet<>();
        while (frequencies.add(total)) total += it.next();
        System.out.println(total);
    }

    private static class InfiniteIterator<T> implements Iterator<T> {
        private Iterable<T> iterable;
        private Iterator<T> current;

        InfiniteIterator(Iterable<T> iterable) {
            this.iterable = iterable;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public T next() {
            if (current == null || !current.hasNext())
                current = iterable.iterator();
            return current.next();
        }
    }

}
