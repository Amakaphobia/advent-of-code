package aocJava.day008;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Game {

    private static final String s0 = "../../resources/008d.txt";
    private static final String s1 = "../../resources/008c.txt";
    private static final String s2 = "../../resources/008b.txt";
    private static final String s3 = "../../resources/008.txt";
    private static final String path = s3;

    final Node start;
    final NodeMap nmap;
    final DirectionList dlist;
    final long steps;

    final List<Long> primes;

    public Game(List<String> lines) {
        String directions = lines.remove(0);
        System.out.println(directions);

        this.nmap = new NodeMap(lines);
        this.dlist = new DirectionList(directions);

        this.start = nmap.get("AAA");
        this.primes = new ArrayList<>();
        this.steps = this.walk2();

    }

    private long walk2() {

        int size = this.nmap.starts.size();
        Node[] current = new Node[size];
        Walk[] steps = new Walk[size];
        for (int i = 0; i < size; i++) {
            current[i] = this.nmap.starts.get(i);
            steps[i] = new Walk(current[i]);
        }

        return this.findLcm(steps);
    }

    private long findLcm(Walk[] _walks) {
        long biggestStepCount = Arrays.stream(_walks)
                .mapToLong(s -> s.steps)
                .max()
                .orElse(-666l);

        this.makePrimeList(biggestStepCount);

        Arrays.stream(_walks).forEach(w -> w.factorize());

        var groupedByBase = Arrays.stream(_walks)
                .flatMap(w -> w.primeFactors.stream())
                .collect(Collectors.groupingBy(pf -> pf.base));

        return groupedByBase.values().stream()
                .map(ll -> {
                    ll.sort(Comparator.comparingLong(pf -> pf.exponent));
                    return ll.get(ll.size() - 1);
                })
                .mapToLong(PrimeFactors::get)
                .reduce((a, b) -> a * b)
                .getAsLong();
    }

    private void makePrimeList(Long _input) {
        long boundary = (long) Math.ceil(Math.sqrt(_input)) + 1;
        this.primes.add(2l);

        for (long l = 3; l < boundary; l += 2) {
            if (this.isPrime(l)) {
                primes.add(l);
            }
        }
    }

    private boolean isPrime(long _input) {
        for (long l : this.primes) {
            if (_input % l == 0)
                return false;
        }
        return true;
    }

    class PrimeFactors {
        final long base;
        long exponent = 0;

        public PrimeFactors(long _base) {
            this.base = _base;
        }

        public void increment() {
            this.exponent++;
        }

        public long get() {
            return (long) Math.pow(this.base, this.exponent);
        }

        public String toString() {
            return String.format("%s^%s = %s", this.base, this.exponent, this.get());
        }
    }

    class Walk {

        final Node start;
        final long steps;
        List<PrimeFactors> primeFactors;

        public Walk(Node _start) {
            this.start = _start;
            this.steps = this.walk();
            this.primeFactors = new ArrayList<>();
        }

        private PrimeFactors getFactor(long _factor) {
            var opf = primeFactors.stream()
                    .filter(pf -> pf.base == _factor)
                    .findFirst();
            PrimeFactors pf;
            if (opf.isEmpty()) {
                pf = new PrimeFactors(_factor);
                primeFactors.add(pf);
            } else
                pf = opf.get();
            return pf;
        }

        public void factorize() {
            long currentValue = this.steps;
            Iterator<Long> it = primes.iterator();

            long currentPrime = it.next();

            while (currentValue != 1) {
                // Current Prime is not a Factor for current Value
                if (currentValue % currentPrime != 0) {
                    if (!it.hasNext())
                        break;
                    currentPrime = it.next();
                    continue;
                }

                // Current Prime is Factor

                PrimeFactors pf = this.getFactor(currentPrime);
                pf.increment();
                currentValue /= currentPrime;
            }

            if (currentValue != 1 && isPrime(currentValue)) {
                PrimeFactors pf = this.getFactor(currentValue);
                pf.increment();
                currentValue /= pf.get();
            }

            if (currentValue != 1)
                throw new RuntimeException("Catch me Daddy:\n" + this.start.toString() + "\nLeft: " + currentValue);
        }

        private int walk() {
            Node current = this.start;
            int steps = 0;
            for (Direction d : dlist) {
                if (current.isEnd)
                    break;
                current = d.walker.apply(current);

                steps++;
            }
            return steps;
        }
    }

    class DirectionList implements Iterable<Direction> {
        int size = 0;
        Direction first = null;
        Direction last = null;

        public DirectionList(String _directions) {
            for (int i = 0; i < _directions.length(); i++) {
                String s = _directions.substring(i, i + 1);
                this.add(new Direction(s));
            }
        }

        public void add(Direction d) {
            if (first == null) {
                this.first = d;
                this.last = d;
            } else {
                this.last.next = d;
                this.last = this.last.next;
            }
            size++;
        }

        @Override
        public Iterator<Direction> iterator() {
            return new DirectionIterator(this.first);
        }

        private class DirectionIterator implements Iterator<Direction> {

            Direction current;
            final Direction first;

            DirectionIterator(Direction _first) {
                this.first = _first;
            }

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Direction next() {

                if (this.current == null)
                    this.current = this.first;
                else if (this.current.next == null)
                    this.current = this.first;
                else
                    this.current = this.current.next;

                return this.current;

            }

        }
    }

    class Direction {
        final Function<Node, Node> walker;
        final String name;
        Direction next;

        public Direction(String _name) {
            this.name = _name;

            this.walker = _name.equals("R") ? Node::goRight : Node::goLeft;
        }

    }

    class NodeMap {
        final Map<String, Node> map = new HashMap<>();
        final List<Node> starts;

        public NodeMap(List<String> lines) {
            starts = new ArrayList<>();
            for (String s : lines) {
                if (s.isBlank())
                    continue;
                Node n = new Node(s, this);
                if (n.isStart)
                    this.starts.add(n);
                this.map.put(n.name, n);
            }

        }

        public Node get(String _name) {
            return this.map.get(_name);
        }

    }

    class Node {
        final String name;
        final String leftName;
        final String rightName;
        final boolean isStart;
        final boolean isEnd;
        private final NodeMap nmap;
        private Node left;
        private Node right;

        public Node(String _input, NodeMap _nmap) {
            String[] line = _input.split("=");
            this.name = line[0].trim();
            String[] cleanNodes = line[1].replaceAll(" ", "").replaceAll("\\(", "").replaceAll("\\)", "").split(",");
            this.leftName = cleanNodes[0];
            this.rightName = cleanNodes[1];
            this.isStart = this.name.endsWith("A");
            this.isEnd = this.name.endsWith("Z");

            this.nmap = _nmap;
        }

        public Node goLeft() {
            if (this.left == null) {
                this.left = this.nmap.get(this.leftName);
            }
            return this.left;
        }

        public Node goRight() {
            if (this.right == null) {
                this.right = this.nmap.get(this.rightName);
            }
            return this.right;
        }

        public boolean equals(Object other) {
            if (other == null)
                return false;
            if (other == this)
                return true;
            if (!(other instanceof Node))
                return false;

            return this.name.equals(((Node) other).name);
        }

        public int hasCode() {
            return this.name.hashCode();
        }

        public String toString() {
            return String.format("%s | <<<<%s : %s>>>>", this.name, this.leftName, this.rightName);
        }
    }

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            var brlines = br.lines().toList();
            brlines.forEach(System.out::println);
            List<String> list = new ArrayList<>();
            brlines.forEach(list::add);
            Game game = new Game(list);
            System.out.println(game.steps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
