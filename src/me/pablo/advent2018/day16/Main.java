package me.pablo.advent2018.day16;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    enum Op {
        ADDR((reg, a, b) -> reg[a] + reg[b]),
        ADDI((reg, a, b) -> reg[a] + b),
        MULR((reg, a, b) -> reg[a] * reg[b]),
        MULI((reg, a, b) -> reg[a] * b),
        BANR((reg, a, b) -> reg[a] &  reg[b]),
        BANI((reg, a, b) -> reg[a] & b),
        BORR((reg, a, b) -> reg[a] | reg[b]),
        BORI((reg, a, b) -> reg[a] | b),
        SETR((reg, a, b) -> reg[a]),
        SETI((reg, a, b) -> a),
        GTIR((reg, a, b) -> a > reg[b] ? 1 : 0),
        GTRI((reg, a, b) -> reg[a] > b ? 1 : 0),
        GTRR((reg, a, b) -> reg[a] > reg[b] ? 1 : 0),
        EQIR((reg, a, b) -> a == reg[b] ? 1 : 0),
        EQRI((reg, a, b) -> reg[a] == b ? 1 : 0),
        EQRR((reg, a, b) -> reg[a] == reg[b] ? 1 : 0);

        interface Executer { int run(int[] mem, int a, int b);}
        Executer action;
        Op(Executer action) {
            this.action = action;
        }
        int[] apply(int[] reg, int[] instruction) {
            reg[instruction[3]] = action.run(reg, instruction[1], instruction[2]);
            return reg;
        }
    }

    public static void main(String[] args) throws IOException {
        int plus3Samples = 0;
        Set<Op>[] guesses = new Set[16];
        for (Iterator<String> lin = Files.readAllLines(Paths.get("input.txt")).iterator(); lin.hasNext(); lin.next()) {
            int[] prev = parse(lin.next()), instruction = parse(lin.next()), expect = parse(lin.next());
            Set<Op> matching = Arrays.stream(Op.values()).filter(op -> Arrays.equals(op.apply(
                    Arrays.copyOf(prev, prev.length), instruction), expect)).collect(Collectors.toSet());
            if (matching.size() >= 3) plus3Samples++;
            if (guesses[instruction[0]] == null) guesses[instruction[0]] = matching;
            else guesses[instruction[0]].retainAll(matching);
        }
        while (Arrays.stream(guesses).anyMatch(s -> s.size() > 1)) {
            Set<Op> defined = Arrays.stream(guesses).filter(s -> s.size() == 1).map(s -> s.iterator().next())
                    .collect(Collectors.toSet());
            for (Set<Op> guess : guesses) if (guess.size() > 1) guess.removeAll(defined);
        }
        System.out.println(plus3Samples); // Part 1

        Op[] codes = Arrays.stream(guesses).map(set -> set.iterator().next()).toArray(Op[]::new);
        int[] registry = new int[4];
        Files.lines(Paths.get("input2.txt")).map(Main::parse).forEach(in -> codes[in[0]].apply(registry, in ));
        System.out.println(registry[0]); // Part 2
    }

    private static int[] parse(String in) {
        return Arrays.stream(in.replaceAll("[^0-9 ]", "").trim().split(" ")).mapToInt(Integer::parseInt).toArray();
    }

}
