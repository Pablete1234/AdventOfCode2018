package me.pablo.advent2018.day11;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {

    private static class Max {
        int x, y, size, max;

        public Max(int x, int y, int size, int max) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.max = max;
        }

        private Max max(Max other) {
            return other == null || other.max < max ? this : other;
        }

        @Override
        public String toString() {
            return (x + 1) + "," + (y + 1) + "," + size + " - " + max;
        }
    }

    private static final int GRID_SERIAL = 7403,
            TEN_GRID_SERIAL = GRID_SERIAL * 10,
            GRID_SIZE = 300;

    public static void main(String[] args) {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        for (int x = 1, ROW_SERIAL = GRID_SERIAL + TEN_GRID_SERIAL; x <= GRID_SIZE; x++, ROW_SERIAL += GRID_SERIAL)
            for (int y = 1, xy = x; y <= GRID_SIZE; y++, xy += x) grid[x-1][y-1] = ((x*xy + xy*20 + y*100 + ROW_SERIAL) / 100) % 10 - 5;

        System.out.println(max(grid, 3, GRID_SIZE - 3)); // Part 1
        System.out.println(IntStream.rangeClosed(1, GRID_SIZE - 1)
                .parallel()
                .mapToObj(s -> max(grid, s, GRID_SIZE - s))
                .reduce(Max::max).orElseThrow(IllegalStateException::new)); // Part 2
    }

    private static Max max(int[][] grid, int size, int maxSize) {
        return IntStream.range(0, maxSize).boxed().flatMap(x ->
                IntStream.range(0, maxSize).mapToObj(y -> getVal(grid, x, y, size)))
                .reduce(Max::max).orElseThrow(IllegalStateException::new);
    }

    private static Max getVal(int grid[][], int inX, int inY, int size) {
        return new Max(inX, inY, size, IntStream.range(inX, inX + size)
                .flatMap(x -> Arrays.stream(grid[x], inY, inY + size)).sum());
    }

}
