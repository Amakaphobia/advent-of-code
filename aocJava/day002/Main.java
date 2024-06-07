package aocJava.day002;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        class GameConfiguration {
            String input;
            int id;
            Map<String, Integer> highestStones = new HashMap<>();

            public boolean isPossible(GameConfiguration _rules) {
                return this.highestStones.get("red") <= _rules.highestStones.get("red") &&
                        this.highestStones.get("green") <= _rules.highestStones.get("green") &&
                        this.highestStones.get("blue") <= _rules.highestStones.get("blue");
            }

            public GameConfiguration(int _red, int _green, int _blue) {
                this.input = "rules";
                this.id = -1;
                this.highestStones.put("red", _red);
                this.highestStones.put("blue", _blue);
                this.highestStones.put("green", _green);
            }

            public GameConfiguration(String _round) {

                this.input = _round;
                String[] id_games = _round.split(": ");
                this.id = Integer.valueOf(id_games[0].split(" ")[1]);
                String[] games = id_games[1].split("; ");
                this.highestStones.put("red", 0);
                this.highestStones.put("green", 0);
                this.highestStones.put("blue", 0);
                for (String game : games) {
                    String[] stones = game.split(", ");
                    for (String count_Stone : stones) {
                        this.tryPut(count_Stone.split(" ")[1], count_Stone.split(" ")[0]);
                    }
                }

            }

            private void tryPut(String _color, String _count) {
                int current = this.highestStones.get(_color);
                int newCount = Integer.valueOf(_count);
                if (current < newCount)
                    this.highestStones.put(_color, newCount);

            }

            public int getPower() {
                return this.highestStones.values().stream()
                        .reduce(1, (x, y) -> x * y);
            }

            public String toString() {
                return this.input;
            }
        }

        final GameConfiguration RULES = new GameConfiguration(12, 13, 14);

        try (BufferedReader reader = new BufferedReader(new FileReader(new File("./../../resources/002.txt")))) {
            int gameRecords = reader.lines()
                    .map(GameConfiguration::new)
                    .mapToInt(GameConfiguration::getPower)
                    .sum();
            // GAME <ID>: <Number> <Color>, .... ; <Number> <Color>, ....

            System.out.println(gameRecords);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
