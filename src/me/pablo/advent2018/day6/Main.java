package me.pablo.advent2018.day6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    private static class Position {
        int x, y, size = 0;

        private Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private int distanceTo(int x, int y) {
            return Math.abs(this.x - x) + Math.abs(this.y - y);
        }
    }

    public static void main(String[] args) throws IOException {
        List<Position> places = Files.lines(Paths.get("input.txt")).map(pos -> pos.split(", "))
                .map(coords -> new Position(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])))
                .collect(Collectors.toList());

        Position min = places.stream().reduce((p1, p2) -> new Position(Math.min(p1.x, p2.x), Math.min(p1.x, p2.y))).get(),
                max = places.stream().reduce((p1, p2) -> new Position(Math.max(p1.x, p2.x), Math.max(p1.x, p2.y))).get();

        for (int x = min.x; x <= max.x; x++) {
            for (int y = min.y; y <= max.y; y++) {
                Position best = places.get(0);
                int bestDist = Integer.MAX_VALUE, dist;
                for (Position place : places) {
                    if ((dist = place.distanceTo(x, y)) == bestDist) best = null;
                    else if (dist < bestDist && (bestDist = dist) > -1) best = place;// 2nd check purposely always true.
                }
                if (best != null && (x == min.x || x == max.x || y == min.y || y == max.y)) best.size = -1;
                else if (best != null && best.size > -1) best.size++;
            }
        }

        System.out.println(places.stream().mapToInt(p -> p.size).max().orElse(0)); // Pt 1

        System.out.println(IntStream.rangeClosed(min.x, max.x).flatMap(x -> IntStream.rangeClosed(min.y, max.y)
                .filter(y -> places.stream().mapToInt(p -> p.distanceTo(x, y)).sum() < 10000)).count()); // Pt 2
    }

}
