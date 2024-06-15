package aocJava.day004;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ForMain {
    private static final String s1 = "../../resources/004.txt";
    private static final String s2 = "../../resources/004b.txt";

    private static final String path = s1;

    private final List<Card> cards;

    private final List<CardCount> cardCounts;
    private final int maxCount;

    class CardCount {

        int count = 1;
        Card card;

        public CardCount(Card _card) {
            this.card = _card;
        }

        public String toString() {
            return "Cardcount:\n" + this.count + " times:\n" + card.toString();
        }

    }

    public ForMain(List<String> _lines) {
        // Get Cards
        this.cards = Collections.unmodifiableList(_lines.stream().map(Card::new).toList());
        this.cardCounts = Collections.unmodifiableList(
                this.cards.stream().sorted((a, b) -> Integer.compare(a.id, b.id)).map(CardCount::new).toList());
        this.maxCount = this.cardCounts.size();
        this.part2();
    }

    public void part2() {
        for (int i = 0; i < maxCount; i++) {
            CardCount cc = this.cardCounts.get(i);
            int wins = cc.card.numberOfWins;
            int count = cc.count;

            for (int j = 1; j < wins + 1; j++) {
                int nextCard = cc.card.id + j;
                if (!(nextCard < maxCount))
                    continue;
                CardCount mod_cc = this.getById(nextCard);
                mod_cc.count += count;
            }
        }
    }

    private CardCount getById(int _id) {
        return this.cardCounts.get(_id - 1);
    }

    private long getScore() {
        long score = this.cards.stream().mapToLong(c -> c.points).sum();
        return score;
    }

    private long getCount() {
        long score = this.cardCounts.stream().mapToLong(cc -> cc.count).sum();
        return score;
    }

    class Card {
        private final String input;
        private final int id;
        private final int[] winningNumbers;
        private final int[] myNumbers;
        private final int[] myWinningNumbers;
        private final int numberOfWins;
        private final long points;

        public Card(String _input) {
            this.input = _input.trim();
            String[] id_numbers = _input.split(": ");
            String[] card_id = id_numbers[0].split(" ");
            this.id = this.findId(card_id);

            String[] win_my = id_numbers[1].split("\\|");
            this.winningNumbers = this.handleNumberLine(win_my[0]);
            this.myNumbers = this.handleNumberLine(win_my[1]);
            this.myWinningNumbers = this.checkForWinningNumbers();
            this.numberOfWins = this.myWinningNumbers.length;
            this.points = this.calculatePoints();
        }

        private int findId(String[] card_id) {
            int id = 0;
            for (String s : card_id) {
                try {
                    id = Integer.valueOf(s);
                } catch (Exception e) {

                }
            }
            return id;
        }

        private int[] checkForWinningNumbers() {
            return Arrays.stream(myNumbers).filter(this::doesWin).toArray();
        }

        private long calculatePoints() {
            int numberwins = this.myWinningNumbers.length;
            return numberwins > 0 ? (long) Math.pow(2, numberwins - 1) : 0l;
        }

        private boolean doesWin(int _number) {
            return Arrays.stream(this.winningNumbers).filter(n -> n == _number).findFirst().isPresent();
        }

        private int[] handleNumberLine(String _input) {
            String[] numbers = _input.split(" ");
            return Arrays.stream(numbers)
                    .filter(s -> !s.isEmpty())
                    .filter(s -> !s.isBlank())
                    .mapToInt(Integer::valueOf)
                    .toArray();
        }

        public String toString() {

            String wn = Arrays.stream(this.winningNumbers).mapToObj(i -> "" + i).collect(Collectors.joining(","));
            String mn = Arrays.stream(this.myNumbers).mapToObj(i -> "" + i).collect(Collectors.joining(","));
            String mwn = Arrays.stream(this.myWinningNumbers).mapToObj(i -> "" + i).collect(Collectors.joining(","));
            return "<<%s>> \nwinning numbers:%s\nmyNumbers: %s\n".formatted(this.id, wn, mn) +
                    "%s wins: %s points\nmy winning numbers:{%s}\n------------\n"
                            .formatted(this.myWinningNumbers.length, this.points, mwn);
        }
    }

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            var lines = br.lines().toList();
            ForMain fm = new ForMain(lines);
            System.out.println(fm.getScore());
            System.out.println(fm.getCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
