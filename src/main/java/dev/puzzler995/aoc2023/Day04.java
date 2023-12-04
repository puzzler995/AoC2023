package dev.puzzler995.aoc2023;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Day04 {

  @PostConstruct
  public void init() {
    Resource example = new ClassPathResource("day04/example.txt");
    Resource input = new ClassPathResource("day04/input.txt");
    List<Card> exampleCards = deserializeCards(example);
    List<Card> inputCards = deserializeCards(input);
    part1(example, new ArrayList<>(exampleCards));
    part1(input, new ArrayList<>(inputCards));
    part2(example, new ArrayList<>(exampleCards));
    part2(input, new ArrayList<>(inputCards));
  }

  private List<Card> deserializeCards(Resource resource) {
    final String logBase = "Deserializing - " + resource.getFilename() + ": ";
    log.debug(logBase + "Start");

    List<Card> cards = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.debug(logBase + line);
        var card = new Card(line);
        log.debug(logBase + "New Card: " + card);
        cards.add(card);
      }
    } catch (Exception e) {
      log.error(logBase + "Error: " + e.getMessage());
    }
    return cards;
  }

  private void part1(Resource resource, List<Card> cards) {
    final String logBase = "Part 1 - " + resource.getFilename() + ": ";
    log.debug(logBase + "Cards: " + cards);
    int score = cards.stream().mapToInt(Card::getScore).sum();
    log.info(logBase + "Score: " + score);
  }

  private void part2(Resource resource, List<Card> cards) {
    final String logBase = "Part 2 - " + resource.getFilename() + ": ";
    log.debug(logBase + "Cards: " + cards);
    cards.sort(Comparator.comparingInt(Card::getId));
    for (Card card : cards) {
      log.debug(logBase + "Card: " + card);
      var matches = card.getMatches().size();
      for (int i = 0; i < matches; i++) {
        int finalI = i;
        var cardToCopy =
            cards.stream().filter(c -> c.getId() == (card.getId() + finalI + 1)).findFirst();
        if (cardToCopy.isPresent()) {
          var cc = cardToCopy.get();
          log.debug(logBase + "Copy: " + cc);
          cc.addCopies(card.getCopies());
        }
      }
    }
    var total = cards.stream().mapToInt(Card::getCopies).sum();
    log.info(logBase + "Total: " + total);
  }

  @Data
  @AllArgsConstructor
  private static class Card {
    private final List<Integer> cardNumbers;
    private final int id;
    private final List<Integer> winningNumbers;
    private int copies = 1;

    public Card(String line) {
      var split1 = line.split(":");
      var idStr = split1[0].split(" ");
      var i = Integer.parseInt(idStr[idStr.length - 1]);
      var numbers = split1[1].split("\\|");
      List<Integer> wNumbers = parseNumbers(numbers[0]);
      List<Integer> cNumbers = parseNumbers(numbers[1]);

      this.id = i;
      this.winningNumbers = wNumbers;
      this.cardNumbers = cNumbers;
    }

    private static List<Integer> parseNumbers(String numberString) {
      return Arrays.stream(numberString.trim().split(" "))
          .filter(StringUtils::isNumeric)
          .map(Integer::parseInt)
          .toList();
    }

    public void addCopies(int copies) {
      this.copies += copies;
    }

    public List<Integer> getMatches() {
      return cardNumbers.stream().filter(winningNumbers::contains).toList();
    }

    public int getScore() {
      List<Integer> matches = getMatches();
      return (int) Math.floor(Math.pow(2, (matches.size() - 1)));
    }
  }
}
