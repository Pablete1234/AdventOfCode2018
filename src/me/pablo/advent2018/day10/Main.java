package me.pablo.advent2018.day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static class Point {
        int x, y, x2, y2; // Serves both point + velocity and area (minX, minY, maxX, maxY)

        public Point(int x, int y, int x2, int y2) {
            this.x = x;
            this.y = y;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    public static void main(String[] args) throws IOException {
        List<Point> sky = Files.lines(Paths.get("input.txt"))
                .map(str -> Arrays.stream(str.split("[^-0-9]+")).skip(1).mapToInt(Integer::parseInt).toArray())
                .map(ints -> new Point(ints[0], ints[1], ints[2], ints[3]))
                .collect(Collectors.toList());

        AtomicInteger time = new AtomicInteger();
        Point area = Stream.generate(() -> sky.stream().peek(p -> p.x += p.x2).peek(p -> p.y += p.y2)
                .reduce(new Point(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE),
                        (a, point) -> new Point(Math.min(a.x, point.x), Math.min(a.y, point.y),
                                Math.max(a.x2, point.x), Math.max(a.y2, point.y))))
                .peek(a -> time.incrementAndGet())
                .filter(a -> Math.abs(a.y - a.y2) < 12)// <12 height is a safe value. My Solution has 10, example has 8.
                .findFirst().orElseThrow(IllegalStateException::new);

        boolean[][] points = new boolean[Math.abs(area.y - area.y2) + 1][Math.abs(area.x - area.x2) + 1];
        for (Point point : sky) points[point.y - area.y][point.x - area.x] = true;
        for (boolean[] point : points) {
            for (boolean b : point) System.out.print(b ? "#" : ".");
            System.out.println();
        }
        System.out.println(time);
    }

}
