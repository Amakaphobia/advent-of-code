package aocJava.day009;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
    private static final String s0 = "../../resources/009.txt";
    private static final String s1 = "../../resources/009b.txt";
    private static final String path = s0;

    public Game(List<String> lines) {
        lines.forEach(System.out::println);
        var histories = lines.stream().map(History::new).toList();

        long sum = histories.stream().mapToLong(h -> h.extrapolate()).sum();
        System.out.println(sum);
        long sum2 = histories.stream().mapToLong(h -> h.extrapolateBackwards()).sum();
        System.out.println(sum2);
    }

    class History {
        private static final String SPACER = "    ";
        final List<Long> list;
        final boolean isEnd;
        final History child;
        final History parent;

        public History(String _input) {
            this.list = new ArrayList<>();
            this.parent = null;
            String[] line = _input.trim().split(" ");
            for (String s : line) {
                if (s.isBlank())
                    continue;
                this.list.add(Long.valueOf(s));
            }

            this.isEnd = this.isEnd();
            this.child = this.makeChild();
        }

        public History(History _parent, List<Long> _list) {
            this.parent = _parent;
            this.list = _list;
            this.isEnd = this.isEnd();
            this.child = this.makeChild();
        }

        public long extrapolate() {
            if (this.isEnd) {
                list.add(0l);
                return 0l;
            }

            long newDifference = this.child.extrapolate();
            long newNumber = this.list.get(list.size() - 1) + newDifference;
            this.list.add(newNumber);
            return newNumber;
        }

        public long extrapolateBackwards() {
            if (this.isEnd) {
                list.add(0, 0l);
                return 0l;
            }

            long newDifference = this.child.extrapolateBackwards();
            long newNumber = this.list.get(0) - newDifference;
            this.list.add(0, newNumber);
            return newNumber;
        }

        private boolean isEnd() {
            return this.list.stream().filter(l -> l != 0).findAny().isEmpty();

        }

        private History makeChild() {

            if (this.isEnd)
                return null;
            else {
                List<Long> differences = new ArrayList<>();

                for (int i = 1; i < this.list.size(); i++) {
                    long smaller = this.list.get(i - 1);
                    long bigger = this.list.get(i);
                    long differ = bigger - smaller;
                    differences.add(differ);
                }

                return new History(this, differences);
            }

        }

        public String toString() {
            return this.toString("");
        }

        public String toString(String inline) {
            StringBuilder strb = new StringBuilder("");
            strb.append(
                    this.list.stream()
                            .map(l -> ((String) String.valueOf(l)))
                            .collect(Collectors.joining(History.SPACER)));

            if (!this.isEnd) {
                strb.append("\n");
                String indentation = inline + "  ";
                strb.append(indentation);
                strb.append(this.child.toString(indentation));
            } else
                strb.append("\n--------------------\n");
            return strb.toString();
        }
    }

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            List<String> lines = new ArrayList<>();
            br.lines().forEach(lines::add);
            Game game = new Game(lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
