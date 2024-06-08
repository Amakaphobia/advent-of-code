package aocJava.day003;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*

467..114.. 0
...*...... 1
..35..633. 2
......#... 3
617*...... 4
.....+.58. 5
..592..... 6 
......755. 7
...$.*.... 8
.664.598.. 9

*/
public class Main {

    private final static String filelocation = "../../resources/003.txt";
    private final List<String> board;
    private final List<String> Numbers = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    private final List<Direction> Directions = List.of(
            new Direction("NW", -1, -1),
            new Direction("SW", -1, 1),
            new Direction("W", -1, 0),
            new Direction("N", 0, -1),
            new Direction("S", 0, 1),
            new Direction("NE", 1, -1),
            new Direction("E", 1, 0),
            new Direction("SE", 1, 1));
    private final int SUM_PART_ONE;
    private final long SUM_PART_TWO;

    public Main(List<String> _board) {
        this.board = new ArrayList<>();

        _board.forEach(this.board::add);
        this.SUM_PART_ONE = this.calculatePartOne();
        this.SUM_PART_TWO = this.calculatePartTwo();
    }

    private long calculatePartTwo() {
        System.out.println("--------- Finding Gears: --------------");
        List<Point> symbols = this.findSymbols();
        List<NumberCoordinate> nc = this.findNumbers(symbols);
        var validGears = symbols.stream().filter(Point::isGear).toList();
        validGears.forEach(g -> System.out.printf("%s ::: %s\n", g.toString(), g.gearValue()));
        return validGears.stream().mapToLong(Point::gearValue).sum();
    }

    private int calculatePartOne() {

        // System.out.println("--------- Finding Symbols: --------------");
        List<Point> symbols = this.findSymbols();
        // symbols.forEach(System.out::println);
        // System.out.println("--------- Finding Numbers: --------------");
        List<NumberCoordinate> numbers = this.findNumbers(symbols);
        numbers.forEach(System.out::println);
        // System.out.println("found Numbers: " + numbers.size());

        return numbers.stream().mapToInt(nc -> Integer.valueOf(nc.value)).sum();

    }

    private List<NumberCoordinate> findNumbers(List<Point> _symbols) {
        List<NumberCoordinate> list = new ArrayList<>();

        for (Point p : _symbols) {

            for (Direction d : Directions) {
                Point toTest = p.move(d);
                if (!this.isNumber(toTest))
                    continue;
                NumberCoordinate nc = new NumberCoordinate(toTest);
                if (list.contains(nc))
                    continue;
                p.adjacentNumbers.add(nc);
                list.add(nc);
            }
        }

        return list;
    }

    private List<Point> findSymbols() {
        List<Point> points = new ArrayList<>();
        for (int j = 0; j < this.board.size(); j++) {
            String line = this.board.get(j);
            for (int i = 0; i < line.length(); i++) {
                String test = line.substring(i, i + 1);
                if (this.isSymbol(test)) {
                    Point e = new Point(i, j);
                    e.symbol = test;
                    points.add(e);
                }

            }

        }

        return points;
    }

    private int[] findBounds(Point _toCheck) {

        Point p = new Point(_toCheck.x, _toCheck.y);
        int[] bounds = new int[2];
        Direction d = this.Directions.stream().filter(o -> o.name.equals("W")).findFirst().get();
        do {
            p = p.move(d);
        } while (this.isNumber(p));

        // found left bounds add one because inclusive
        bounds[0] = p.x + 1;

        d = this.Directions.stream().filter(o -> o.name.equals("E")).findFirst().get();
        do {
            p = p.move(d);
        } while (this.isNumber(p));

        // found right bounds

        bounds[1] = p.x;

        return bounds;
    }

    private boolean isNumber(Point _p) {
        return this.isNumber(
                this.charAt(_p));
    }

    private boolean isNumber(String _c) {
        return Numbers.contains(_c);
    }

    private boolean isSymbol(String _c) {
        return !(this.isNumber(_c) || _c.equals("."));
    }

    class NumberCoordinate {
        Point start;
        int length;
        String value;

        public NumberCoordinate(Point _anyPoint) {
            int[] bounds = Main.this.findBounds(_anyPoint);
            this.start = new Point(bounds[0], _anyPoint.y);
            this.length = bounds[1] - bounds[0];
            this.value = Main.this.board.get(_anyPoint.y).substring(bounds[0], bounds[1]);
        }

        public String toString() {
            return String.format("p: %s l: %s v: %s", start, length, value);
        }

        public int getNumber() {
            return Integer.valueOf(value);
        }

        @Override
        public int hashCode() {
            return start.hashCode();
        }

        public boolean equals(Object o) {
            if (o == null)
                return false;
            if (o == this)
                return true;
            if (!(o instanceof Main.NumberCoordinate))
                return false;

            NumberCoordinate other = (NumberCoordinate) o;
            return other.start.equals(this.start);
        }
    }

    class Point {

        int x, y;
        String symbol;
        List<NumberCoordinate> adjacentNumbers = new ArrayList<>();

        public Point(int _x, int _y) {
            this.x = _x;
            this.y = _y;
        }

        private Point move(Point _direction) {
            return new Point(this.x + _direction.x, this.y + _direction.y);
        }

        public String toString() {
            return "<" + x + "|" + y + ">";
        }

        public int hashCode() {
            return Objects.hash(x, y);
        }

        public boolean equals(Object o) {
            if (o == null)
                return false;
            if (o == this)
                return true;
            if (!(o instanceof Main.Point))
                return false;

            Point other = (Point) o;
            return other.x == this.x && other.y == this.y;
        }

        public boolean isGear() {
            return this.symbol.equals("*")
                    && this.adjacentNumbers.size() == 2;
        }

        public int gearValue() {
            return this.isGear()
                    ? this.adjacentNumbers.stream().mapToInt(NumberCoordinate::getNumber).reduce(1, (a, b) -> a * b)
                    : 0;
        }
    }

    class Direction extends Point {
        String name;

        public Direction(String _name, int _x, int _y) {
            super(_x, _y);
            this.name = _name;
        }
    }

    public String charAt(Point _p) {
        if (this.board.size() < _p.y + 1 || _p.y < 0)
            return ".";

        String line = this.board.get(_p.y);

        if (line.length() < _p.x + 1 || _p.x < 0)
            return ".";

        return String.valueOf(line.charAt(_p.x)); // 0123
    }

    public static void main(String[] args) {

        try (BufferedReader br = new BufferedReader(new FileReader(new File(filelocation)))) {

            var something = br.lines().toList();
            something.forEach(System.out::println);
            var Game = new Main(something);
            System.out.println(Game.SUM_PART_ONE);
            System.out.println(Game.SUM_PART_TWO);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
