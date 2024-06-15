
package aocJava.day005;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 
seeds: 79 14 55 13

seed 59 
soil 57
fert 7
wate 7
ligh 7
temp 7
humi 6
loca 6

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4
 *
 *
 *
 * Build all rules
 * Build all relationships
 * sort rules and create rules for empty space
 * sort rules again
 * start with lowest possible seed find out range of this rule.
 * go to next relationship according to rule0 find that range repeat until ruleA
 * find the lowest RangeWANT and the soil of first seed
 * skip RangeWant at rule0 repeat until first seed range is depleted and go to next
 * remember lowest soil
 *
 */
public class GameRight {
    private static final String s1 = "../../resources/005b.txt";
    private static final String s2 = "../../resources/005.txt";
    private static final String path = s2;

    private final List<Range> seeds;
    private final List<Relationship> relationships;

    public GameRight(List<String> _input) {

        this.seeds = this.makeSeeds(_input);
        this.relationships = this.makeRelationships(_input);

    }

    private List<Relationship> makeRelationships(List<String> _input) {
        List<Relationship> list = new ArrayList<>();

        List<List<String>> splitlist = new ArrayList<>();
        for (int i = 1; i < _input.size(); i++) {
            String line = _input.get(i).trim();
            if (line.isBlank()) {
                splitlist.add(new ArrayList<>());
                continue;
            }
            splitlist.get(splitlist.size() - 1).add(line);
        }

        Relationship lastRelationship = null;

        for (int i = splitlist.size() - 1; i >= 0; i--) {
            List<String> currentList = splitlist.get(i);
            String name = currentList.get(0);
            List<Range> ranges = currentList.stream().skip(1)
                    .map(Range::new)
                    .toList();
            Relationship rs = new Relationship(i, name, lastRelationship, ranges);
            lastRelationship = rs;
            list.add(rs);
        }

        list.sort((a, b) -> Integer.compare(a.id, b.id));

        return list;
    }

    private List<Range> makeSeeds(List<String> _input) {
        List<Range> list = new ArrayList<>();
        // Isolate the Numbers describing Seed ranges
        String[] seedInput = _input.get(0).split(":")[1].trim().split(" ");
        for (int i = 0; i < seedInput.length - 1; i += 2) {

            long start = Long.valueOf(seedInput[i]);
            long range = Long.valueOf(seedInput[i + 1]);

            list.add(new Range(start, start, range));
        }
        list.sort((a, b) -> Long.compare(a.sourceStart, b.sourceStart));
        return list;
    }

    private void test() {
        long smallestSoil = Long.MAX_VALUE;
        for (Range s : this.seeds) {
            long currentSeedValue = s.sourceStart;
            do {
                Walk w = new Walk(currentSeedValue);
                smallestSoil = Math.min(smallestSoil, w.soil);
                currentSeedValue += w.minimalRange;
            } while (currentSeedValue < s.sourceEnd);
        }

        System.out.println(smallestSoil);
    }

    class Walk {
        final long seed;
        final long soil;
        final long minimalRange;
        final List<Step> steps = new ArrayList<>();

        public Walk(long _seed) {
            this.seed = _seed;

            for (Relationship rs : relationships) {
                long input = this.steps.size() == 0 ? this.seed : this.steps.get(this.steps.size() - 1).output;

                Step s = rs.takeStep(input);
                steps.add(s);
            }

            this.minimalRange = this.steps.stream().mapToLong(s -> s.openRange).min().orElse(-666);
            this.soil = steps.get(steps.size() - 1).output;
        }

        public String toString() {
            String s = this.steps.stream().map(Step::toString).collect(Collectors.joining("\n"));

            return String.format("Walk from %s to %s, minimalRange: %s\nSteps:\n%s\n-----", this.seed, this.soil,
                    this.minimalRange, s);
        }
    }

    class Step {
        final int idStart;
        final long input;
        final long openRange;
        final long output;

        public Step(long _input, Relationship _relationship) {
            this.input = _input;
            Range range = _relationship.ranges.stream()
                    .filter(r -> r.test(this.input))
                    .findFirst()
                    .orElseThrow();
            this.idStart = _relationship.id;
            this.openRange = range.sourceEnd - this.input;
            this.output = range.translate(this.input);
        }

        public String toString() {
            return String.format("Step: %s, from %s to %s, consumedRange: %s", this.idStart, this.input, this.output,
                    this.openRange);
        }
    }

    class Relationship {
        final String name;
        final List<Range> ranges;
        final Relationship nextRelationship;
        final int id;

        public Relationship(int _id, String _name, Relationship _nextRelationship, List<Range> _ranges) {
            this.name = _name;
            this.nextRelationship = _nextRelationship;
            this.ranges = this.makeRanges(_ranges);
            this.id = _id;
        }

        public Step takeStep(long _input) {

            Step step = new Step(_input, this);

            return step;
        }

        private List<Range> makeRanges(List<Range> _ranges) {
            List<Range> sortedInput = new ArrayList<>();
            for (Range r : _ranges)
                sortedInput.add(r);

            // sort all given ranges and find undefined spaces create Ranges for these
            // spaces that translate input 1:1 to output
            sortedInput.sort((a, b) -> Long.compare(a.sourceStart, b.sourceStart));
            List<Range> missingRanges = new ArrayList<>();

            long lastEnd = 0;
            for (int i = 0; i < sortedInput.size(); i++) {
                Range currentRange = sortedInput.get(i);
                if (currentRange.sourceStart != lastEnd) {
                    missingRanges.add(new Range(lastEnd, lastEnd, currentRange.sourceStart - lastEnd));
                }
                lastEnd = currentRange.sourceStart + currentRange.range;
            }

            sortedInput.addAll(missingRanges);
            sortedInput.sort((a, b) -> Long.compare(a.sourceStart, b.sourceStart));

            long lastRange = Long.MAX_VALUE - lastEnd;
            Range last = new Range(lastEnd, lastEnd, lastRange);
            sortedInput.add(last);

            return sortedInput;
        }

        public String toString() {
            String ranges = this.ranges.stream().map(Range::toString).collect(Collectors.joining("\n")) + "\n";
            String next = this.nextRelationship == null ? "<<<END>>>" : this.nextRelationship.toString();

            return String.format("<<< %s: %s>>>\n-----\n%s\n-----\n%s", this.id, this.name, ranges, next);
        }
    }

    class Range {
        final long sourceStart;
        final long range;
        final long destinationStart;
        final long sourceEnd;

        public Range(long _sourceStart, long _destinationStart, long _range) {
            this.sourceStart = _sourceStart;
            this.range = _range;
            this.destinationStart = _destinationStart;
            this.sourceEnd = this.sourceStart + this.range;
        }

        public Range(String _input) {
            String[] line = _input.trim().split(" ");
            this.destinationStart = Long.valueOf(line[0]);
            this.sourceStart = Long.valueOf(line[1]);
            this.range = Long.valueOf(line[2]);
            this.sourceEnd = this.sourceStart + this.range;
        }

        public long translate(long _input) {
            long offset = _input - this.sourceStart;
            return this.destinationStart + offset;
        }

        public String toString() {
            return String.format("Source: %s Destination: %s Range: %s", this.sourceStart, this.destinationStart,
                    this.range);
        }

        public boolean test(long _input) {
            return _input >= this.sourceStart && _input < this.sourceEnd;
        }
    }

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {

            List<String> input = br.lines().toList();
            GameRight game = new GameRight(input);
            game.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
