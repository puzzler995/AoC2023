package dev.puzzler995.aoc2023;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class Day03 {

  private List<List<String>> exampleMatrixRows;
  private List<List<String>> inputMatrixRows;

  @PostConstruct
  public void init() {
    Resource example = new ClassPathResource("day03/example.txt");
    Resource input = new ClassPathResource("day03/input.txt");
    exampleMatrixRows = deserializeMatrix(example);
    inputMatrixRows = deserializeMatrix(input);
    part1(example, exampleMatrixRows);
    part1(input, inputMatrixRows);
    part2(example, exampleMatrixRows);
    part2(input, inputMatrixRows);
  }

  private static List<List<String>> deserializeMatrix(Resource resource) {
    final String logBase = "Deserializing - " + resource.getFilename() + ": ";
    log.debug(logBase + "Start");

    List<List<String>> matrix = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.debug(logBase + line);
        var row = new ArrayList<String>();
        for (int i = 0; i < line.length(); i++) {
          row.add(String.valueOf(line.charAt(i)));
        }
        matrix.add(row);
      }
    } catch (Exception e) {
      log.error(logBase + "Error: " + e.getMessage());
    }
    return matrix;
  }

  private void part1(Resource resource, List<List<String>> matrix) {
    final String logBase = "Part 1 - " + resource.getFilename() + ": ";
    log.debug(logBase + "Start");
    List<PartNumber> partNumbers = extractPartNumbers(matrix, logBase);
    validatePartNumbers(partNumbers, matrix, logBase);
    int total = 0;
    for (PartNumber partNumber : partNumbers) {
      if (partNumber.isValidated()) {
        total += partNumber.getNumber();
      }
    }
    log.info(logBase + "Total: " + total);
  }

  private void part2(Resource resource, List<List<String>> matrix) {
    final String logBase = "Part 2 - " + resource.getFilename() + ": ";
    log.debug(logBase + "Start");
    List<PartNumber> partNumbers = extractPartNumbers(matrix, logBase);
    validatePartNumbers(partNumbers, matrix, logBase);
    List<Gear> gears = extractGears(matrix, partNumbers, logBase);
    log.debug(logBase + "Gears: " + gears);
    int total = 0;
    for (Gear gear : gears) {
      total += gear.ratio();
    }
    log.info(logBase + "Total: " + total);
  }

  private static List<PartNumber> validatePartNumbers(
      List<PartNumber> partNumbers, List<List<String>> matrix, String logBase) {
    for (PartNumber partNumber : partNumbers) {
      var coords = partNumber.getCoordinates();
      for (Coordinate coord : coords) {
        var row = coord.getRow();
        var column = coord.getColumn();
        for (int i = row - 1; i < row + 2; i++) {
          for (int j = column - 1; j < column + 2; j++) {
            if (i == row && j == column) {
              continue;
            }
            if (i < 0 || j < 0 || i >= matrix.size() || j >= matrix.get(i).size()) {
              continue;
            }
            var cell = matrix.get(i).get(j);
            if (StringUtils.containsAny(
                cell, '*', '#', '+', '$', '=', '/', '@', '!', '%', '^', '&', '-')) {
              log.debug(
                  logBase
                      + "Validated: "
                      + partNumber.getNumber()
                      + " at "
                      + i
                      + ", "
                      + j
                      + " with "
                      + cell);
              partNumber.setValidated(true);
            }
          }
        }
      }
    }
    return partNumbers;
  }

  private static List<Gear> extractGears(List<List<String>> matrix, List<PartNumber> partNumbers, String logBase) {
    List<Gear> gears = new ArrayList<>();
    for (int i = 0; i < matrix.size(); i++) {
      var row = matrix.get(i);
      for (int j = 0; j < row.size(); j++) {
        var cell = row.get(j);
        if (StringUtils.equals(cell, "*")) {
          log.debug(logBase + "Possible Gear at " + i + ", " + j);
          Set<PartNumber> partNumberList = new HashSet<>();
          for (int q = i - 1; q < i + 2; q++) {
            for (int w = j - 1; w < j + 2; w++) {
              if (q == i && w == j) {
                continue;
              }
              if (q < 0 || w < 0 || q >= matrix.size() || w >= matrix.get(q).size()) {
                continue;
              }
              int finalQ = q;
              int finalW = w;
              partNumbers.stream().filter(partNumber -> partNumber.getCoordinates().contains(new Coordinate(finalQ, finalW))).forEach(partNumberList::add);
            }
          }
          if (partNumberList.size() == 2) {
            var partNumberArray = partNumberList.toArray();
            gears.add(new Gear(new Coordinate(i, j), (PartNumber) partNumberArray[0], (PartNumber) partNumberArray[1]));
          }
        }
      }
    }

    return gears;
  }

  private static List<PartNumber> extractPartNumbers(List<List<String>> matrix, String logBase) {
    List<PartNumber> partNumbers = new ArrayList<>();
    for (int i = 0; i < matrix.size(); i++) {
      StringBuilder cache = new StringBuilder();
      List<Coordinate> coordinateCache = new ArrayList<>();
      var row = matrix.get(i);
      for (int j = 0; j < row.size(); j++) {
        var cell = row.get(j);
        if (StringUtils.isNumeric(cell)) {
          log.debug(logBase + "Cell: " + cell + " at " + i + ", " + j);
          cache.append(cell);
          coordinateCache.add(new Coordinate(i, j));
        } else {
          if (!cache.isEmpty()) {
            var number = Integer.parseInt(cache.toString());
            log.debug(logBase + "Number: " + number);
            partNumbers.add(new PartNumber(number, coordinateCache, false));
            cache = new StringBuilder();
            coordinateCache = new ArrayList<>();
          }
        }
      }
      if (!cache.isEmpty()) {
        var number = Integer.parseInt(cache.toString());
        log.debug(logBase + "Number: " + number);
        partNumbers.add(new PartNumber(number, coordinateCache, false));
      }
    }
    return partNumbers;
  }

  @Data
  @AllArgsConstructor
  private static class Gear {
    Coordinate coordinate;
    PartNumber partNumber;
    PartNumber partNumber2;
    public Integer ratio() {
      return partNumber.getNumber() * partNumber2.getNumber();
    }
  }

  @Data
  @AllArgsConstructor
  private static class PartNumber {
    int number;
    List<Coordinate> coordinates;
    boolean validated;
  }

  @Data
  @AllArgsConstructor
  private static class Coordinate {
    int row;
    int column;
  }
}
