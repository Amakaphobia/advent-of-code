package aocJava.day007;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
public class Game2 {

    private static final String s1 = "../../resources/007c.txt";
    private static final String s2 = "../../resources/007b.txt";
    private static final String s3 = "../../resources/007.txt";
    private static final String path = s3;
    private static final List<Bet> bets = new ArrayList<>();

    class Bet implements Comparable<Bet> {
        final String input;
        final Hand original;
        final int amount;
        final Hand replaced;

        public Bet(String _input) {
            this.input = _input;
            String[] line = _input.split(" ");
            this.original = new Hand(line[0]);
            this.amount = Integer.valueOf(line[1]);

            this.replaced = this.original.replaceWildcards();
        }

        public String toString() {
            return String.format("---------------\nBet: %s for %s \n Freqencies: \n %s %s\n turns into: \n %s",
                    this.input, this.amount, this.original.frequency.toString(),
                    this.original.toString(),
                    this.replaced.toString());
        }

        @Override
        public int compareTo(Bet o) {
            int dif = 0;

            if (dif == 0) {
                dif = Integer.compare(this.replaced.combination.value, o.replaced.combination.value);
            }

            if (dif == 0) {
                dif = this.original.compareTo(o.original);
            }

            return dif;
        }
    }

    class CardFrequency {

        final List<Entry<Cards, Integer>> list;

        public CardFrequency(Cards[] cards) {
            list = this.add(cards);
        }

        public int getFrequencyOf(Cards _c) {
            return this.list.stream()
                    .filter(c -> c.getKey().compareTo(_c) == 0)
                    .mapToInt(Entry::getValue)
                    .findFirst()
                    .orElse(0);

        }

        public Cards getWildcardTarget() {
            return list.stream()
                    .filter(e -> !e.getKey().name.equals("J"))
                    .findFirst()
                    .orElseThrow()
                    .getKey();
        }

        public List<Integer> frequencies() {
            return this.list.stream()
                    .map(Entry::getValue)
                    .toList();
        }

        public String toString() {
            return list.stream()
                    .map(e -> String.format("Card: %s Count: %s\n", e.getKey(), e.getValue()))
                    .collect(Collectors.joining("\n"));
        }

        @SuppressWarnings("unchecked")
        public List<Entry<Cards, Integer>> add(Cards[] _c) {
            Map<Cards, Integer> cardCounts = new HashMap<>();
            for (Cards c : _c) {
                cardCounts.putIfAbsent(c, 0);
                int newCount = cardCounts.get(c) + 1;
                cardCounts.put(c, newCount);
            }
            return cardCounts.entrySet().stream()
                    .sorted(Comparator
                            .comparingInt(a -> -((Entry<Cards, Integer>) (a)).getValue())
                            .thenComparingInt(a -> -((Entry<Cards, Integer>) (a)).getKey().value))
                    .toList();
        }
    }

    class Hand implements Comparable<Hand> {
        final String input;
        final Cards[] cards;
        final CardFrequency frequency;
        final Combinations combination;

        public Hand(String _cards) {
            this.input = _cards;
            cards = new Cards[5];
            for (int i = 0; i < _cards.length(); i++) {
                cards[i] = Cards.withName(_cards.substring(i, i + 1));
            }

            this.frequency = new CardFrequency(this.cards);
            this.combination = this.findCombination();
        }

        public Hand replaceWildcards() {

            // check if wildcards Exist in this hand

            int jokerFrequency = this.frequency.getFrequencyOf(Cards.withName("J"));

            // No Wildcards
            if (jokerFrequency == 0)
                return new Hand(this.input);

            // only Wildcards
            if (jokerFrequency == 5)
                return new Hand("AAAAA");

            // some Wildcards

            String newName = this.frequency.getWildcardTarget().name;

            String newHand = this.input.replaceAll("J", newName);

            return new Hand(newHand);
        }

        public String toString() {
            String cs = Arrays.stream(cards).map(Cards::toString).collect(Collectors.joining(" "));

            return String.format("%s \n %s \n %s", this.input, cs, this.combination.name);
        }

        private Combinations findCombination() {

            List<Integer> frequencies = this.frequency.frequencies();

            if (frequencies.get(0) == 5)
                return Combinations.FiveOfAKind;
            if (frequencies.get(0) == 4)
                return Combinations.FourOfAKind;
            if (frequencies.get(0) == 3)
                if (frequencies.get(1) == 2)
                    return Combinations.FullHouse;
                else
                    return Combinations.ThreeOfAKind;
            if (frequencies.get(0) == 2)
                if (frequencies.get(1) == 2)
                    return Combinations.TwoPairs;
                else
                    return Combinations.Pair;
            return Combinations.HighCard;
        }

        @Override
        public int compareTo(Hand o) {
            int dif = 0;
            int i = 0;
            while (dif == 0 && i < 5) {
                dif = Integer.compare(this.cards[i].value, o.cards[i].value);
                i++;
            }
            return dif;
        }

    }

    enum Cards {

        Jack("J", 1),
        Two("2", 2),
        Three("3", 3),
        Four("4", 4),
        Five("5", 5),
        Six("6", 6),
        Seven("7", 7),
        Eight("8", 8),
        Nine("9", 9),
        Ten("T", 10),
        Queen("Q", 12),
        King("K", 13),
        Ace("A", 14);

        String name;
        int value;

        private Cards(String _name, int _value) {
            this.name = _name;
            this.value = _value;
        }

        public String toString() {
            return String.format("{%s|%s}", this.name, this.value);
        }

        public static Cards withName(String _name) {
            return Arrays.stream(Cards.values())
                    .filter(c -> c.name.equals(_name))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(_name + " doesnt work"));
        }
    }

    enum Combinations {
        HighCard("Single Card", 1),
        Pair("Pair", 2),
        TwoPairs("Two Pairs", 3),
        ThreeOfAKind("Three of a Kind", 4),
        FullHouse("Full House", 5),
        FourOfAKind("Four of a Kind", 6),
        FiveOfAKind("Five of a Kind", 7);

        private Combinations(String _name, int _value) {
            this.name = _name;
            this.value = _value;
        }

        String name;
        int value;
    }

    public Game2(Stream<String> lines) {
        lines.map(Bet::new)
                .sorted()
                .forEachOrdered(bets::add);
    }

    public int calculate() {
        int sum = 0;
        var list = Game2.bets.stream().sorted().toList();
        for (int i = 0; i < list.size(); i++) {
            sum += (i + 1) * list.get(i).amount;

        }
        return sum;
    }

    public static void main(String[] args) {

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            var lines = br.lines();

            Game2 game = new Game2(lines);
            int score = game.calculate();
            System.out.println(score);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
