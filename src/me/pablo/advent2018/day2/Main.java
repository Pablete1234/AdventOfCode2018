package me.pablo.advent2018.day2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    // Solution for part 1
    public static void main1(String[] args) throws FileNotFoundException {
        int two = 0, three = 0;

        Scanner sc = new Scanner(new File("input.txt"));

        int minChar = 'a', size = 'z' - minChar + 1;

        while (sc.hasNextLine()) {
            int[] letterAmounts = new int[size];
            String line = sc.nextLine();
            for (char letter : line.toCharArray()) letterAmounts[letter-minChar]++;
            if (Arrays.stream(letterAmounts).anyMatch(i -> i == 2)) two++;
            if (Arrays.stream(letterAmounts).anyMatch(i -> i == 3)) three++;
        }
        System.out.println(two * three);
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
        for (int i = 0; i < hashes.length; i++) {
            if (hashIdx == i) continue;
            if (fuzzyEquals(hash, hashes[i])) return true;
        }
        return false;
    }

    private static boolean fuzzyEquals(char[] str1, char[] str2) {
        short diffChar = -1;
        for (short i = 0; i < str1.length; i++) {
            if (str1[i] == str2[i]) continue;
            if (diffChar > -1) return false;
            diffChar = i;
        }
        System.out.print(Arrays.copyOfRange(str1, 0, diffChar));
        System.out.println(Arrays.copyOfRange(str1, diffChar + 1, str1.length));
        return true;
    }

}
