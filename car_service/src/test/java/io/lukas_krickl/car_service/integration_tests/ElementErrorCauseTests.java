package io.lukas_krickl.car_service.integration_tests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationTest")
@Slf4j
class ElementErrorCauseTests {
  @Test
  void assemblyTimeError() {
    Flux.range(0, -5)
      .map(Objects::toString)
      .subscribe(System.out::println, t -> log.error(t.getMessage(), t));
  }

  @Test
  void executionTimeError() {
    Flux.range(0, 10)
      .map(number -> {
        throw new RuntimeException();
      })
      .map(Objects::toString)
      .subscribe(System.out::println, t -> log.error(t.getMessage(), t));
  }

  @Test
  void fallbackValues() {
    Flux.range(0, 10)
      .map(number -> {
        throw new RuntimeException();
      })
      .onErrorReturn(-1)
      .map(Objects::toString)
      .subscribe(System.out::println, t -> log.error(t.getMessage(), t));
  }

  @Test
  void fallbackValuesWithOperator() {
    Flux.range(0, 10)
      .map(number -> {
        throw new RuntimeException();
      })
      .map(Objects::toString)
      .doOnError(t -> log.error(t.getMessage(), t))
      .onErrorReturn("-1")
      .subscribe(System.out::println, t -> log.error(t.getMessage(), t));
  }
}
