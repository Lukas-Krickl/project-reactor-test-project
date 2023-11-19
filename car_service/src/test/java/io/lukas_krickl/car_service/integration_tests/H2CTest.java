package io.lukas_krickl.car_service.integration_tests;

import io.lukas_krickl.car_service.model.Car;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;
import java.net.URI;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2c")
@Slf4j
class H2CTest {
  @Autowired
  WebTestClient webTestClient;
  @Autowired
  WebClient.Builder builder;
  @LocalServerPort
  int port;

  @Test
  @DisplayName("It should be possible to use HTTP2 with WebTestClient")
  void getCarWithWebTestClientAndHTTP2() {
    //given
    Function<UriBuilder, URI> uri = uriBuilder -> uriBuilder.path("/cars/{id}").build(1);
    //when
    var actualCar = webTestClient.get()
      .uri(uri)
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody(Car.class)
      .returnResult()
      .getResponseBody();

    //then
    assertNotNull(actualCar);
  }

  @Test
  @DisplayName("It should be possible to use HTTP2 with WebClient")
  void getSpecificCarWithWebclient() {
    WebClient webClient = builder.baseUrl("http://localhost:"+port).build();
    //given
    var request = webClient.get()
      .uri(uriBuilder -> uriBuilder.path("/cars").build());
    //when
    for (int i = 0; i < 3; i++) {
        request.exchangeToMono(clientResponse -> {
          log.info(clientResponse.headers().asHttpHeaders().getUpgrade());
          return clientResponse.bodyToMono(new ParameterizedTypeReference<List<Car>>() {});
        })
        .as(StepVerifier::create)
        .assertNext(Assertions::assertNotNull)
        .verifyComplete();
    }
  }

  @Test
  @DisplayName("It should be possible to use HTTP2 with the Reactor Netty HttpClient")
  void getSpecificCarWithHttpClient() {
    //given
    HttpClient client = HttpClient.create()
      .keepAlive(true)
      .baseUrl("http://localhost:"+port)
      .protocol(HttpProtocol.H2C)
      .wiretap(true);

    //when
    client.get()
      .uri("/cars/1")
      .response()
      .doOnNext(r -> log.info(r.version().text()))
      .block();
    client.get()
      .uri("/cars/1")
      .response()
      .doOnNext(r -> log.info(r.version().text()))
      .block();
  }
}
