package me.pablo.advent2018.day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    static class Rectangle {
        short top, left, bottom, right;

        Rectangle(short top, short left, short bottom, short right) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }

        public Rectangle intersection(Rectangle other) {
            return new Rectangle(max(top, other.top), max(left, other.left),
                    min(bottom, other.bottom), min(right, other.right));
        }

        private static short max(short s1, short s2) {
            return s1 > s2 ? s1 : s2;
        }

        private static short min(short s1, short s2) {
            return s1 > s2 ? s2 : s1;
        }

        public boolean hasArea() {
            return bottom > top && right > left;
        }
    }

    private static class Claim extends Rectangle {
        short id;

        static Claim of(String in) {
            String[] parts = in.split("[^0-9]");
            short top =  Short.parseShort(parts[4]), left = Short.parseShort(parts[5]);
            return new Claim(
                    Short.parseShort(parts[1]),
                    top,
                    left,
                    (short) (top + Short.parseShort(parts[7])),
                    (short) (left + Short.parseShort(parts[8])));
        }

        Claim(short id, short top, short left, short bottom, short right) {
            super(top, left, bottom, right);
            this.id = id;
        }
    }

    private static class Shape {
        private static class Position {
            short x, y;

            private Position(short x, short y) {
                this.x = x;
                this.y = y;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Position position = (Position) o;
                return x == position.x &&
                        y == position.y;
            }

            @Override
            public int hashCode() {
                return Objects.hash(x, y);
            }
        }

        private Set<Position> positions = new HashSet<>();

        void add(Rectangle rectangle) {
            for (short i = rectangle.left; i < rectangle.right; i++) {
                for (short j = rectangle.top; j < rectangle.bottom; j++) {
                    positions.add(new Position(i, j));
                }
            }
        }

        void addAll(Shape other) {
            positions.addAll(other.positions);
        }

    }

    public static void main(String[] args) throws IOException {
        List<Claim> claims = Files.lines(Paths.get("input.txt"))
                .map(Claim::of)
                .collect(Collectors.toList());

        // Part 1
        System.out.println(IntStream.range(0, claims.size()).boxed().flatMap(i -> IntStream.range(i + 1, claims.size())
                .mapToObj(j -> claims.get(i).intersection(claims.get(j))))
                .filter(Rectangle::hasArea)
                .collect(Shape::new, Shape::add, Shape::addAll).positions.size());

        // Part 2
        for (int i = 0; i < claims.size(); i++) {
            Claim current = claims.get(i);
            if (claims.stream().filter(cl -> cl.id != current.id)
                    .map(cl -> cl.intersection(current))
                    .noneMatch(Rectangle::hasArea)) {
                System.out.println(current.id);
            }
        }
    }


}
