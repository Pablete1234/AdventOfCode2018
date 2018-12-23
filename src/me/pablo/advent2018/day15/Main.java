package me.pablo.advent2018.day15;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    private static int HEALTH_MASK = 0xFF, TYPE_MASK = 0x300; // Health is bits 0-8, type is bits 9-10
    private enum Type {
        AIR, WALL, ELF, GOBLIN;
        int val() {
            return ordinal() << 8;
        }
    }

    private static class Playfield {
        private int[][] field;
        private int rounds = 0, elfDamage;
        private boolean killEndedRound = false; // true if the last unit "ticked" killed. False if others got ticked.
        private Set<Place> units;
        private Playfield(int[][] field, int elfDamage) {
            this.field = field;
            this.elfDamage = elfDamage;
            units = IntStream.range(0, field.length).boxed().flatMap(y -> IntStream.range(0, field[y].length)
                    .filter(x -> (field[y][x] & TYPE_MASK) >= Type.ELF.val())
                    .mapToObj(x -> new Place(x, y, this))).collect(Collectors.toSet());
        }

        public boolean tick() {
            units.addAll(units.stream().sorted().peek(u -> killEndedRound = false)
                    .filter(u -> !u.attack()).map(Place::move).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            units.removeIf(u -> u == null || (field[u.y][u.x] & HEALTH_MASK) == 0); // Clean dead or moved
            rounds++;
            return units.stream().mapToInt(Place::type).distinct().count() > 1; // Same type or empty
        }

        private int process() {
            while (tick());
            return (rounds - (killEndedRound ? 0 : 1)) * units.stream().mapToInt(Place::health).sum();
        }
    }

    private static class Place implements Comparable<Place> {
        private Playfield pf;
        private int x, y;
        public Place(int x, int y, Playfield playfield) {
            this.x = x;
            this.y = y;
            this.pf = playfield;
        }
        private int health() {
            return pf.field[y][x] & HEALTH_MASK;
        }
        private int type() {
            return pf.field[y][x] & TYPE_MASK;
        }
        private Type enemyType() {
            return type() == Type.ELF.val() ? Type.GOBLIN : Type.ELF;
        }
        private Stream<Place> adjacent(Type type) { // Returns 4 adjacent positions filtered by their type.
            return Stream.of(new Place(x, y-1, pf), new Place(x-1, y, pf), new Place(x+1, y, pf), new Place(x, y+1, pf))
                    .filter(p -> p.type() == type.val());
        }
        private void damage(int amount) {
            pf.field[y][x] = health() > amount ? pf.field[y][x] - amount : Type.AIR.val();
            if (health() == 0) pf.killEndedRound = true;
        }
        private boolean attack() {
            if (health() == 0) return true; // This unit already died, ignore attack or move.
            return adjacent(enemyType()).sorted(Comparator.comparingInt(Place::health))
                    .peek(p -> p.damage(type() == Type.GOBLIN.val() ? 3 : pf.elfDamage)).findFirst().isPresent();
        }
        private Place move() {
            Set<Place> explored = new HashSet<>();
            Map<Place, Set<Place>> expanding = adjacent(Type.AIR).filter(explored::add)
                    .collect(Collectors.toMap(p -> p, Collections::singleton));
            Place[] move = new Place[1];
            while (move[0] == null && !expanding.isEmpty()) {
                expanding.values().stream().flatMap(Collection::stream).filter(p -> p.adjacent(enemyType()).count() > 0)
                        .min(Place::compareTo).ifPresent(obj -> move[0] = expanding.entrySet().stream()
                        .filter(entry -> entry.getValue().contains(obj)).map(Map.Entry::getKey).min(Place::compareTo)
                        .orElseThrow(IllegalStateException::new)); // Found target but no path
                expanding.forEach((key, val) -> expanding.put(key, val.stream().flatMap(p -> p.adjacent(Type.AIR))
                        .filter(p -> !explored.contains(p)).collect(Collectors.toSet())));
                expanding.entrySet().removeIf(e -> e.getValue().isEmpty());
                expanding.values().forEach(explored::addAll);
            }
            if (move[0] != null) {
                pf.field[move[0].y][move[0].x] = pf.field[y][x];
                pf.field[y][x] = Type.AIR.val();
                move[0].attack();
            }
            return move[0];
        }

        @Override
        public int compareTo(Place other) {
            return this.y == other.y ? this.x - other.x : this.y - other.y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Place place = (Place) o;
            return x == place.x && y == place.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public static void main(String[] args) throws IOException {
        int[][] field = Files.lines(Paths.get("input.txt")).map(str -> str.chars()
                .mapToObj(ch -> ch == '#' ? Type.WALL : ch == '.' ? Type.AIR : ch == 'E' ? Type.ELF : Type.GOBLIN)
                .mapToInt(Type::val).map(val -> val >= Type.ELF.val() ? val + 200 : val).toArray()).toArray(int[][]::new);

        Playfield pl = new Playfield(copy(field), 3);
        int elfCount = (int) pl.units.stream().filter(u -> u.type() == Type.ELF.val()).count();

        System.out.println(pl.process()); // Pt 1
        System.out.println(IntStream.iterate(4, i -> ++i).mapToObj(i -> new Playfield(copy(field), i)).mapToInt(p ->
                p.process() * (p.units.stream().filter(u -> u.type() == Type.ELF.val()).count() == elfCount ? 1 : -1))
                .filter(i -> i > 0).findFirst().orElse(0)); // Pt 2
    }

    private static int[][] copy(int[][] original) {
        return Arrays.stream(original).map(ints -> Arrays.copyOf(ints, ints.length)).toArray(int[][]::new);
    }

}
