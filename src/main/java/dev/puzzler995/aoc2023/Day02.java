package dev.puzzler995.aoc2023;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@Slf4j
public class Day02 {
  private static final Pattern colorPattern =
      Pattern.compile("(?<blue>\\d blue)|(?<red>\\d red)|(?<green>\\d green)");
  private static final int MAX_RED = 12;
  private static final int MAX_GREEN = 13;
  private static final int MAX_BLUE = 14;

  @PostConstruct
  public void init() {
    Resource example = new ClassPathResource("day02/example.txt");
    Resource input = new ClassPathResource("day02/input.txt");
    part1(example);
    part1(input);
    part2(example);
    part2(input);
  }

  private void part1(Resource resource) {
    final String logBase = "Part 1 - " + resource.getFilename() + ": ";
    log.info(logBase + "Start");
    List<Integer> possibleGames = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.debug(logBase + line);
        var s1 = line.split(":");
        var s2 = s1[0].split(" ");
        var game = Integer.parseInt(s2[1]);
        var s3 = s1[1].split(";");
        log.debug(logBase + "Game: " + game);
        boolean gamePossible = true;
        for (String s : s3) {
          var s4 = s.split(", ");
          for (String n : s4) {
            var s5 = n.trim().split(" ");
            var color = s5[1];
            var number = Integer.parseInt(s5[0]);
            log.debug(logBase + "Color: " + color + " Number: " + number);
            switch (color) {
              case "red":
                if (number > MAX_RED) {
                  gamePossible = false;
                }
                break;
              case "green":
                if (number > MAX_GREEN) {
                  gamePossible = false;
                }
                break;
              case "blue":
                if (number > MAX_BLUE) {
                  gamePossible = false;
                }
                break;
            }
          }
        }
        if (gamePossible) {
          possibleGames.add(game);
          log.debug(logBase + "Game " + game + " is possible");
        }
      }
    } catch (IOException e) {
      log.error(logBase + "Error reading file", e);
    }
    log.info(logBase + "Possible Games: " + possibleGames);
    int total = 0;
    for (Integer i : possibleGames) {
      total += i;
    }
    log.info(logBase + "Total: " + total);
  }

  private void part2(Resource resource) {
    final String logBase = "Part 2 - " + resource.getFilename() + ": ";
    log.info(logBase + "Start");
    List<Integer> powers = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.debug(logBase + line);
        var s1 = line.split(":");
        var s2 = s1[0].split(" ");
        var game = Integer.parseInt(s2[1]);
        var s3 = s1[1].split(";");
        log.debug(logBase + "Game: " + game);
        int minRed = 0;
        int minGreen = 0;
        int minBlue = 0;
        for (String s : s3) {
          var s4 = s.split(", ");
          for (String n : s4) {
            var s5 = n.trim().split(" ");
            var color = s5[1];
            var number = Integer.parseInt(s5[0]);
            log.debug(logBase + "Color: " + color + " Number: " + number);
            switch (color) {
              case "red":
                if (number > minRed) {
                  minRed = number;
                }
                break;
              case "green":
                if (number > minGreen) {
                  minGreen = number;
                }
                break;
              case "blue":
                if (number > minBlue) {
                  minBlue = number;
                }
                break;
            }
          }
        }
        log.debug(
            logBase + "Min Red: " + minRed + " Min Green: " + minGreen + " Min Blue: " + minBlue);
        int power = minRed * minGreen * minBlue;
        log.debug(logBase + "Power: " + power);
        powers.add(power);
      }
    } catch (IOException e) {
      log.error(logBase + "Error reading file", e);
    }
    log.info(logBase + "Powers: " + powers);
    int total = 0;
    for (Integer i : powers) {
      total += i;
    }
    log.info(logBase + "Total: " + total);
  }
}
