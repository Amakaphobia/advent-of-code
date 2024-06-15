package aocJava.day006;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
            Time:      7  15   30
            Distance:  9  40  200
 */
public class Game {

    private static final String s1 = "../../resources/006.txt";
    private static final String s2 = "../../resources/006b.txt";
    private static final String path = s1;

    private final List<Race> races = new ArrayList<>();

    public Game(List<String[]> _input) {

        long[] times = this.cleanInput(_input.get(0));
        long[] distances = this.cleanInput(_input.get(1));

        for (int i = 0; i < times.length; i++) {
            long time = times[i];
            long distance = distances[i];
            this.races.add(new Race(i, time, distance));
        }
    }

    public long play() {
        long score = 1;

        for (Race r : races) {
            score *= r.solve2();
        }
        return score;
    }

    class Race {
        final long id;
        final long time;
        final long distance;

        public Race(long _id, long _time, long _distance) {
            this.id = _id;
            this.time = _time;
            this.distance = _distance;
        }

        public String toString() {
            return String.format("Race: %s » Time: %s Distance: %s Ways To win: %s", this.id, this.time, this.distance,
                    this.solve());
        }

        public boolean doesWin(long _input) {
            long timeLeft = this.time - _input;
            long travelled = timeLeft * _input;
            return travelled > distance;
        }

        public long solve2() {
            long left = this.findLeftIndex();
            long right = this.findRightIndex();
            return right - left + 1;
        }

        public long solve() {
            boolean isEven = this.time % 2 == 0;

            long score = 0;

            for (long timePressed = 0; timePressed <= Math.ceil(this.time / 2); timePressed++) {
                if (this.doesWin(timePressed))
                    score++;
            }
            score *= 2;

            if (isEven)
                score--;

            return score;
        }

        private long findLeftIndex() {
            long index = 0;
            long lastRightIndex = (long) Math.ceil(this.time / 2);
            long lastLeftIndex = 0;
            long solution = -666;

            while (true) {
                boolean doesWin = this.doesWin(index);
                if (doesWin) {
                    // if does win, either it is boundary, or it overstepped(coming from left) or it
                    // understepped(coming from right)
                    if (!this.doesWin(index - 1)) { // boundary found
                        solution = index;
                        break;
                    } else { // this case handles both under and oversteps because we can track last left and
                             // right indecies independant from another
                        long step = (long) Math.ceil((index - lastLeftIndex) / 2);
                        lastRightIndex = index;
                        index -= step;
                    }
                } else {
                    // if does not win, either it is boundary, or it overstepped(coming from right)
                    // or it understepped(coming from Left)
                    if (this.doesWin(index + 1)) { // boundary found
                        solution = index + 1;
                        break;
                    } else { // this case handles both under and oversteps because we can track last left and
                             // right indecies independant from another
                        long step = (long) Math.ceil((lastRightIndex - index) / 2);
                        lastLeftIndex = index;
                        index += step;
                    }

                }
            }
            return solution;
        }

        private long findRightIndex() {
            long index = this.time;
            long lastLeftIndex = (long) Math.ceil(this.time / 2);
            long lastRightIndex = this.time;
            long solution = -666;

            while (true) {
                boolean doesWin = this.doesWin(index);
                if (doesWin) {
                    // if does win, either it is boundary, or it overstepped(coming from left) or it
                    // understepped(coming from right)
                    if (!this.doesWin(index + 1)) { // boundary found
                        solution = index;
                        break;
                    } else { // this case handles both under and oversteps because we can track last left and
                             // right indecies independant from another
                        long step = (long) Math.ceil((lastRightIndex - index) / 2);
                        lastLeftIndex = index;
                        index += step;
                    }
                } else {
                    // if does not win, either it is boundary, or it overstepped(coming from right)
                    // or it understepped(coming from Left)
                    if (this.doesWin(index - 1)) { // boundary found
                        solution = index - 1;
                        break;
                    } else { // this case handles both under and oversteps because we can track last left and
                             // right indecies independant from another
                        long step = (long) Math.ceil((index - lastLeftIndex) / 2);
                        lastRightIndex = index;
                        index -= step;
                    }

                }
            }
            return solution;
        }
    }

    public long[] cleanInput(String[] _input) {
        return Arrays.stream(_input)
                .filter(s -> !s.isBlank())
                .mapToLong(Long::valueOf)
                .toArray();
    }

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            List<String[]> input = new ArrayList<>();
            // br.lines()
            // .map(s -> s.split(":")[1].trim().split(" "))
            // .forEach(input::add);
            br.lines()
                    .map(s -> s.split(":")[1].replaceAll(" ", ""))
                    .map(s -> new String[] { s })
                    .forEach(input::add);

            Game game = new Game(input);
            System.out.println(game.play());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
