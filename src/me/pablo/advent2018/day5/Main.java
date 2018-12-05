package me.pablo.advent2018.day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) throws IOException {
        char[] str = Files.readAllLines(Paths.get("input.txt")).get(0).toCharArray();

        System.out.println(polySize(str, (char) -1)); // Pt 1

        System.out.println(IntStream.rangeClosed('a', 'z')
                .map(i -> polySize(str, (char) i))
                .min().orElse(0)); // Pt 2
    }

    private static int polySize(char[] str, char ignored) {
        char[] buffer = new char[str.length];
        int size = 0;
        for (char c : str) {
            if (Character.toLowerCase(c) == ignored) continue;
            if (size > 0 && equalDifferCase(c, buffer[size - 1])) size--;
            else buffer[size++] = c;
        }
        return size;
    }

    private static boolean equalDifferCase(char a, char b) {
        return a != b
                && Character.toLowerCase(a) == Character.toLowerCase(b)
                && Character.isUpperCase(a) != Character.isUpperCase(b);
    }

}
