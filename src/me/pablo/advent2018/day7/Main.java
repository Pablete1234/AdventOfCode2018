package me.pablo.advent2018.day7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.IntStream;

public class Main {

    private static class Task {
        int timeDone = Integer.MAX_VALUE;
        char ch;
        Set<Character> dependencies = new HashSet<>();

        public Task(int ch) {
            this.ch = (char) ch;
        }

        private int canRun(Task[] tasks) {
            return timeDone == Integer.MAX_VALUE ? dependencies.stream().map(dep -> tasks[dep - 'A'])
                    .mapToInt(t -> t.timeDone).max().orElse(0) : Integer.MAX_VALUE;
        }
    }

    private static final boolean PART_1 = true;

    public static void main(String[] args) throws IOException {
        Task[] tasks = IntStream.rangeClosed('A', 'Z').mapToObj(Task::new).toArray(Task[]::new);

        Files.lines(Paths.get("input.txt"))
                .map(in -> in.split(" "))
                .map(in -> new char[]{in[1].charAt(0), in[7].charAt(0)})
                .forEach(in -> tasks[in[1] - 'A'].dependencies.add(in[0]));

        PriorityQueue<Integer> doneBy = new PriorityQueue<>(5);
        for (int i = 0; i < 5; i++) doneBy.add(0);
        while (Arrays.stream(tasks).anyMatch(t -> t.timeDone == Integer.MAX_VALUE)) {
            int elfReady = doneBy.element();
            Task next = nextTask(tasks, elfReady);
            int finishTime = PART_1 ? 0 : Math.max(next.canRun(tasks), elfReady) + (next.ch - 'A') + 61;
            doneBy.add(next.timeDone = finishTime);
            System.out.print(next.ch); // Pt 1. To get this result finishTime must always be 0, set via PART_1 flag.
        }
        System.out.println();
        System.out.println(Arrays.stream(tasks).mapToInt(t -> t.timeDone).max().orElse(0)); // Pt 2
    }

    private static Task nextTask(Task[] tasks, int time) {
        return Arrays.stream(tasks)
                .filter(t -> t.timeDone == Integer.MAX_VALUE)
                .filter(t -> t.canRun(tasks) <= time)
                .min(Comparator.comparingInt(t -> t.ch))
                .orElseGet(() -> nextTask(tasks, Arrays.stream(tasks)
                        .mapToInt(t -> t.canRun(tasks)).min()
                        .orElseThrow(() -> new IllegalStateException("No new task?"))));
    }

}
