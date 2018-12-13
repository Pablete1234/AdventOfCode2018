package me.pablo.advent2018.day12;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    private static class Hallway {
        Set<Integer> plants;
        int min, max;
        Hallway(String initialState) {
            plants = Collections.synchronizedSet(IntStream.range(0, initialState.length())
                    .filter(i -> initialState.charAt(i) == '#')
                    .boxed().collect(Collectors.toSet()));
            tick(); // Forces min & max to calculate 1st time.
        }

        private int tick(int... rules) {
            Set<Integer> state = new HashSet<>(plants);
            Arrays.stream(rules).filter(r -> (r & 0b1) != 0).flatMap(r -> this.match(r, state)).forEach(plants::add);
            Arrays.stream(rules).filter(r -> (r & 0b1) == 0).flatMap(r -> this.match(r, state)).forEach(plants::remove);
            min = plants.stream().mapToInt(pos -> pos).min().orElse(Integer.MAX_VALUE);
            max = plants.stream().mapToInt(pos -> pos).max().orElse(Integer.MIN_VALUE);
            return plants.stream().mapToInt(pos -> pos).sum();
        }

        private IntStream match(int rule, Set<Integer> state) {
            return IntStream.rangeClosed(min - 3, max + 3).filter(pos -> IntStream.range(0, 5)
                    .allMatch(i -> state.contains((pos - 2) + i) == ((rule & 0b1 << 5 - i) != 0)));
        }
    }

    private static final long STEPS = 500, MAX = 50000000000L;// STEPS needs to be enough to reach a stale.

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("input.txt"));
        Hallway hallway = new Hallway(lines.get(0).split(" ")[2]);
        int[] rules = IntStream.range(2, lines.size()).mapToObj(lines::get) // "...## => #" = "...###" = 000111 = 7
                .mapToInt(pattern -> IntStream.of(0, 1, 2, 3, 4, 9).filter(i -> pattern.charAt(i) == '#')
                        .reduce(0, (tempPattern, flag) -> tempPattern += 0b1 << (flag < 5 ? (5 - flag) : 0))).toArray();

        for (int i = 0; i < 19; i++) hallway.tick(rules);
        System.out.println(hallway.tick(rules)); // Pt 1

        for (long i = 20; i < STEPS - 1; i++) hallway.tick(rules);
        int sum = hallway.tick(rules), diff = hallway.tick(rules) - sum; // The hallway ends in STEPS + 1 state.

        System.out.println(BigInteger.valueOf(MAX - STEPS).multiply(BigInteger.valueOf(diff))
                .add(BigInteger.valueOf(sum))); // Pt 2
    }

}
