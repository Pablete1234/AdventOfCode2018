package me.pablo.advent2018.day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    // Solution for part 1
    public static void main1(String[] args) throws IOException {
        System.out.println(Files.lines(Paths.get("input.txt"))
                .map(line -> Arrays.stream(line.split(""))
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.reducing(0, e -> 1, Integer::sum)))
                        .values().stream()
                        .filter(amount -> amount == 2 || amount == 3)
                        .reduce(new int[2], (freq, num) -> {
                            freq[num - 2] = 1;
                            return freq;
                        }, (a, b) -> { throw new UnsupportedOperationException(); })
                ).reduce((freq1, freq2) -> {
                    IntStream.range(0, 2).forEach(i -> freq1[i] += freq2[i]);
                    return freq1;
                }).map(freq -> freq[0] * freq[1]).orElse(0));
    }

    // Solution for part 2
    public static void main(String[] args) throws IOException {
        char[][] hashes = Files.lines(Paths.get("input.txt"))
                .map(String::toCharArray)
                .toArray(char[][]::new);

        for (int i = 0; i < hashes.length; i++) {
            if (testHash(hashes, i)) break;
        }
    }

    private static boolean testHash(char[][] hashes, int hashIdx) {
        char[] hash = hashes[hashIdx];
        return IntStream.range(0, hashes.length)
                .filter(i -> i != hashIdx)
                .anyMatch(i -> {
                    int diffChar = IntStream.range(0, hash.length)
                            .filter(j -> hash[j] != hashes[i][j])
                            .limit(2) // Need max 2 matches to know it's not the correct hash
                            .reduce(-1, (i1, i2) -> i1 == -1 ? i2 : -2);// -1 for no diff, -2 for 2+ diff. idx otherwise
                    if (diffChar >= 0) IntStream.range(0, hash.length).filter(j -> j != diffChar)
                            .mapToObj(j -> hash[j]).forEach(System.out::print);
                    return diffChar >= 0;
                });
    }

}
