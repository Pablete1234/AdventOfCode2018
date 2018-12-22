package me.pablo.advent2018.day13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    enum Dir {
        NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0);
        int x, y;

        Dir(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Dir turn(int num) { // Turns num clockwise, accepts negative for counter-clockwise
            return Dir.values()[(this.ordinal() + num + 4) % 4]; // Always add 4 cause negative modulus is a bitch.
        }
    }

    public static class Cart {
        Dir dir;
        int x, y, turn = -2; // -1 = left, 0 = straight, 1 = right

        Cart(int x, int y, int[][] field) {
            this.dir = Dir.values()[field[y][x] - 6];
            this.x = x;
            this.y = y;
            field[y][x] = (field[y][x] % 2) + 11; // % 2 + 1 to convert to | or -, +10 for the cart.
        }

        private boolean move(int[][] field) {
            if (field[y][x] > 20) return true; // Can't move, someone else crashed into this, must stay for cleanup.
            field[y][x] -= 10; // Cleanup before leaving
            x += dir.x;
            y += dir.y;
            if (field[y][x] % 10 == 3) dir = dir.turn(turn = (turn == 1 ? -1 : turn + 1)); // Cross (+)
            if (field[y][x] % 10 == 4) dir = dir.turn(dir.y != 0 ? 1 : -1); // Right slope (/)
            if (field[y][x] % 10 == 5) dir = dir.turn(dir.y != 0 ? -1 : 1); // Left slope  (\)
            return (field[y][x] += 10) < 20; // No cart was there previously
        }
    }

    private static final List<Character> CHARS = Arrays.asList(' ', '|', '-', '+', '/', '\\', '^', '>', 'v', '<');

    public static void main(String[] args) throws IOException {
        int[][] field = Files.readAllLines(Paths.get("input.txt")).stream().map(lin -> IntStream.range(0, lin.length())
                .map(x -> CHARS.indexOf(lin.charAt(x))).toArray()).toArray(int[][]::new); // ByteStream would be nice.
        List<Cart> carts = IntStream.range(0, field.length).boxed().flatMap(y -> IntStream.range(0, field[y].length)
                .filter(x -> field[y][x] > 5).mapToObj(x -> new Cart(x, y, field))).collect(Collectors.toList());

        while (carts.size() > 1) {
            carts.removeAll(carts.stream().sorted(Comparator.<Cart>comparingInt(c -> c.y).thenComparing(c -> c.x))
                    .filter(c -> !c.move(field)).peek(c -> System.out.println("Crash at: " + c.x + "," + c.y))
                    .flatMap(dead -> carts.stream().filter(c -> c.x == dead.x && c.y == dead.y)) // Find the other cart
                    .peek(c -> field[c.y][c.x] %= 10) // Remove both carts from the track
                    .collect(Collectors.toList()));
        }
        carts.forEach(c -> System.out.println("Alive: " + c.x + "," + c.y));
    }

}
