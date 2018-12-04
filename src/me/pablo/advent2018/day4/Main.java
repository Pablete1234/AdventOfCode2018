package me.pablo.advent2018.day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .toFormatter();

    private static class Guard {
        int id;
        int[] sleepMinutes = new int[60];
        int totalMinutes;

        int lastSleep;

        public Guard(int id) {
            this.id = id;
        }

        private void process(int minute, boolean sleep) {
            if (sleep) lastSleep = minute;
            else {
                for (int i = lastSleep; i < minute; i++) sleepMinutes[i]++;
                totalMinutes += minute - lastSleep;
            }
        }

    }

    private static int bestMinute(int[] sleepMinutes) {
        return IntStream.range(0, 60).boxed()
                .max(Comparator.comparingInt(i -> sleepMinutes[i]))
                .orElse(-1);
    }

    public static void main(String[] args) throws IOException {
        Map<Integer, Guard> guards = new HashMap<>();
        int guardId = -1;
        for (Map.Entry<LocalDateTime, String> entry : Files.lines(Paths.get("input.txt"))
                .map(line -> line.split("[\\[\\]] ?"))
                .collect(Collectors.toMap(
                        parts -> LocalDateTime.parse(parts[1], DATE_FORMAT),
                        parts -> parts[2],
                        (key1, key2) -> {throw new IllegalStateException("Conflicting keys");},
                        TreeMap::new)).entrySet()) {
            if (entry.getValue().endsWith("begins shift"))
                guardId = Integer.parseInt(entry.getValue().replaceAll("[^0-9]", ""));
            else guards.computeIfAbsent(guardId, Guard::new)
                        .process(entry.getKey().getMinute(), entry.getValue().contains("asleep"));
        }

        // Part 1
        guards.values().stream().max(Comparator.comparingInt(g -> g.totalMinutes))
                .ifPresent(sleepyGuard -> System.out.println(bestMinute(sleepyGuard.sleepMinutes) * sleepyGuard.id));

        // Part 2
        guards.values().stream().max(Comparator.comparingInt(g -> g.sleepMinutes[bestMinute(g.sleepMinutes)]))
                .ifPresent(sleepyGuard ->  System.out.println(sleepyGuard.id * bestMinute(sleepyGuard.sleepMinutes)));
    }

}
