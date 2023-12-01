package dev.puzzler995.aoc2023;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import static dev.puzzler995.aoc2023.Helper.reverse;

@Component
@Slf4j
public class Day01 {

  private static final Pattern digitPattern = Pattern.compile("\\D*");

  private static int calculateValue(String line, String logBase) {
    log.debug(logBase + line);
    String digits = digitPattern.matcher(line).replaceAll("");
    log.debug(logBase + digits);
    String calValue = digits.charAt(0) + digits.substring(digits.length() - 1);
    log.debug(logBase + calValue);
    return Integer.parseInt(calValue);
  }

  private static String findFirstDigit(String line, String logBase) {
    String[] words = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
    StringBuilder cache = new StringBuilder();
    for (char c : line.toCharArray()) {
      if (Character.isDigit(c)) {
        log.debug(logBase + "Found digit: " + c);
        return String.valueOf(c);
      }
      cache.append(c);
      String currentCache = cache.toString();
      log.debug(logBase + "Current cache: " + currentCache);
      for (String word : words) {
        if (currentCache.endsWith(word)) {
          return String.valueOf(Arrays.asList(words).indexOf(word) + 1);
        }
      }
    }
    return "0";
  }
  private static String findLastDigit(String line, String logBase) {
    String[] words = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
    StringBuilder cache = new StringBuilder();

    for (char c : reverse(line.toCharArray())) {
      if (Character.isDigit(c)) {
        log.debug(logBase + "Found digit: " + c);
        return String.valueOf(c);
      }
      cache.insert(0, c);
      String currentCache = cache.toString();
      log.debug(logBase + "Current cache: " + currentCache);
      for (String word : words) {
        if (currentCache.startsWith(word)) {
          return String.valueOf(Arrays.asList(words).indexOf(word) + 1);
        }
      }
    }
    return "0";
  }

  @PostConstruct
  public void init() {
    Resource example = new ClassPathResource("day01/example.txt");
    Resource example2 = new ClassPathResource("day01/example2.txt");
    Resource input = new ClassPathResource("day01/input.txt");
    part1(example);
    part1(input);
    part2(example2);
    part2(input);
  }

  private void part1(Resource resource) {
    final String logBase = "Part 1 - " + resource.getFilename() + ": ";
    log.info(logBase);
    int total = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        total += calculateValue(line, logBase);
        log.debug(logBase + "New Total: " + total);
      }
    } catch (Exception e) {
      log.error(logBase + "Error reading file", e);
    }
    log.info(logBase + "Total: " + total);
  }

  private void part2(Resource resource) {
    final String logBase = "Part 2 - " + resource.getFilename() + ": ";
    log.info(logBase);
    int total = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.debug(logBase + line);
        String val = findFirstDigit(line, logBase) + findLastDigit(line, logBase);
        total += Integer.parseInt(val);
      }
    } catch (Exception e) {
      log.error(logBase + "Error reading file", e);
    }
    log.info(logBase + "Total: " + total);
  }
}
