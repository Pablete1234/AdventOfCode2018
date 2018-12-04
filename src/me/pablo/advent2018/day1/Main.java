package me.pablo.advent2018.day1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    // Solution for part 1
    public static void main1(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("input.txt"));
        int total = 0;
        while (sc.hasNextInt()) total += sc.nextInt();
        System.out.println(total);
    }

    // Solution for part 2
    public static void main(String[] args) throws FileNotFoundException {
        int total = 0;
        Set<Integer> frequencies = new HashSet<>();

        Scanner sc = new Scanner(new File("input.txt"));

        while (frequencies.add(total)) {
            if (!sc.hasNextInt()) sc = new Scanner(new File("input.txt"));
            total += sc.nextInt();
        }
        System.out.println(total);
    }

}
