package aocJava.day007;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
In Camel Cards, you get a list of hands, and your goal is to order them based on the strength of each hand.
A hand consists of five cards labeled one of A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, or 2.
The relative strength of each card follows this order, where A is the highest and 2 is the lowest.

Every hand is exactly one type. From strongest to weakest, they are:

    Five of a kind, where all five cards have the same label: AAAAA
    Four of a kind, where four cards have the same label and one card has a different label: AA8AA
    Full house, where three cards have the same label, and the remaining two cards share a different label: 23332
    Three of a kind, where three cards have the same label, and the remaining two cards are each different from any other card in the hand: TTT98
    Two pair, where two cards share one label, two other cards share a second label, and the remaining card has a third label: 23432
    One pair, where two cards share one label, and the other three cards have a different label from the pair and each other: A23A4
    High card, where all cards' labels are distinct: 23456


32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483

*/
public class Game {

    private static final String s1 = "../../resources/007b.txt";
    private static final String s2 = "../../resources/007.txt";
    private static final String path = s2;
    private final Card[] pairs = {
            new Card("2", 2),
            new Card("3", 3),
            new Card("4", 4),
            new Card("5", 5),
            new Card("6", 6),
            new Card("7", 7),
            new Card("8", 8),
            new Card("9", 9),
            new Card("T", 10),
            new Card("J", 11),
            new Card("Q", 12),
            new Card("K", 13),
            new Card("A", 14)
    };

    final List<Bet> bets;

    public Game(Stream<String> lines) {
        this.bets = lines
                .map(Bet::new)
                .sorted()
                .toList();
    }

    public int calculatePart1() {
        int i = 0;

        for (int j = 1; j <= bets.size(); j++) {
            i += j * bets.get(j - 1).amount;
        }
        return i;
    }

    class Bet implements Comparable<Bet> {
        final Hand hand;
        final int amount;

        public Bet(String _input) {
            String[] line = _input.split(" ");
            this.amount = Integer.valueOf(line[1]);
            Card[] cards = new Card[5];

            for (int i = 0; i < 5; i++) {
                String name = String.valueOf(line[0].charAt(i));
                cards[i] = Arrays.stream(pairs).filter(c -> c.name.equals(name)).findFirst().orElseThrow();
            }
            this.hand = new Hand(cards);
        }

        public String toString() {
            return String.format("<<<<<<<Bet>>>>>>\namount: %s\n%s\n", this.amount, this.hand.toString());
        }

        @Override
        public int compareTo(Bet o) {
            return this.hand.compareTo(o.hand);
        }
    }

    class Hand implements Comparable<Hand> {
        final Card[] cards;
        final Map<Card, Integer> ccount = new HashMap<>();
        final int value;

        public Hand(Card[] _cards) {
            this.cards = _cards;
            Arrays.stream(_cards).forEach(this::addCard);
            var c = ccount.values()
                    .stream()
                    .sorted()
                    .toList();
            int j = 0;
            int[] counts = new int[5];
            for (int i = c.size() - 1; i >= 0; i--) {
                counts[j++] = c.get(i);
            }
            this.value = this.getValue(counts);
        }

        private void addCard(Card _c) {
            int count = 1;
            if (this.ccount.containsKey(_c)) {
                count = this.ccount.get(_c) + 1;
                this.ccount.remove(_c);
            }
            this.ccount.put(_c, count);
        }

        public String toString() {
            String c = this.ccount.entrySet().stream()
                    .map(cc -> String.format("%s times: %s", cc.getValue(), cc.getKey().toString()))
                    .collect(Collectors.joining("\n"));

            return String.format("-------\nHand with Value: %s\n%s\n---------\n", this.value, c);
        }

        private int getValue(int[] _counts) {
            if (_counts[0] == 5)
                return 7;
            if (_counts[0] == 4)
                return 6;
            if (_counts[0] == 3 && _counts[1] == 2)
                return 5;
            if (_counts[0] == 3)
                return 4;
            if (_counts[0] == 2 && _counts[1] == 2)
                return 3;
            if (_counts[0] == 2)
                return 2;
            return 1;
        }

        @Override
        public int compareTo(Hand o) {
            int dif = Integer.compare(this.value, o.value);
            int i = 0;
            while (dif == 0 && i < 5) {
                dif = this.cards[i].compareTo(o.cards[i]);
                i++;
            }
            return dif;
        }
    }

    class Card implements Comparable<Card> {
        final String name;
        final int value;

        public Card(String _name, int _value) {
            this.name = _name;
            this.value = _value;
        }

        @Override
        public int compareTo(Card o) {
            return Integer.compare(this.value, o.value);
        }

        public String toString() {
            return String.format("{%s|%s}", this.name, this.value);
        }

    }

    private String test() {
        StringBuilder strb = new StringBuilder();

        strb.append(this.bets.stream()
                .map(Bet::toString)
                .collect(Collectors.joining("\n")));
        strb.append("========================================\n");
        strb.append(this.calculatePart1());
        return strb.toString();
    }

    public static void main(String[] args) {

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            var lines = br.lines();

            Game game = new Game(lines);
            String input = game.test();
            System.out.println(input);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
