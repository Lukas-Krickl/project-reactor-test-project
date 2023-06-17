package io.lukas_krickl.car_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ComparingAssemblyAndExecutionTimeErrorsTests {

  @Test
    /*
     * In assembly time errors, no "sign" of reactor can be seen as the chain cannot even be created
     */
  void assemblyTimeErrors() {
    Mono.just(List.of(null))
      .log()
      .subscribe(System.out::println);
  }

  @Test
  /*
   * In execution time errors, log will show propagating onError signals
   */
  void executionTimeErrors() {
    Mono.just(List.of(1, 2, 3, 4))
      .map(i -> {
        throw new RuntimeException();
      })
      .log()
      .subscribe();
  }
}
