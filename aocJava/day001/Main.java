package aocJava.day001;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    /*
     *
     * twone
     * letter by letter
     * is last letter number? -> out
     * is there a number string in selection?
     * convert -> out
     *
     * front to back
     * back to front
     * 10a+b
     * 
     */
    private static final String NUMBERS[][] = {
            { "one", "1" },
            { "two", "2" },
            { "three", "3" },
            { "four", "4" },
            { "five", "5" },
            { "six", "6" },
            { "seven", "7" },
            { "eight", "8", },
            { "nine", "9" }
    };

    public static void main(String[] args) {

        try (BufferedReader reader = new BufferedReader(
                new FileReader(new File("resources/001.txt")))) {

            int sum = reader.lines()
                    // .peek(System.out::println)
                    .mapToInt(Main::findNumber)
                    // .peek(System.out::println)
                    .sum();

            System.out.println(sum);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int findNumber(String inputString) {
        List<Integer> foundNumbers = new ArrayList<>();
        for (int i = 0; i < inputString.length(); i++) {
            String slice = inputString.substring(i);
            char firstLetter = slice.charAt(0);
            if (firstLetter > '0' && firstLetter <= '9') {
                foundNumbers.add(Integer.valueOf("" + firstLetter));
                continue;
            }

            for (String[] number : NUMBERS) {
                if (slice.startsWith(number[0])) {
                    foundNumbers.add(Integer.valueOf(number[1]));
                    break;
                }
            }
        }
        return foundNumbers.get(0) * 10 + foundNumbers.get(foundNumbers.size() - 1);
    }
}
