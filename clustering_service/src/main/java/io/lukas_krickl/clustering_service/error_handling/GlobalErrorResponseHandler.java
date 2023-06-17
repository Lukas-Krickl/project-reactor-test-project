package io.lukas_krickl.clustering_service.error_handling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@RequiredArgsConstructor
@Slf4j
public class GlobalErrorResponseHandler implements ErrorWebExceptionHandler {
  private final ObjectMapper objectMapper;

  @Override
  public @NotNull Mono<Void> handle(ServerWebExchange serverWebExchange, @NotNull Throwable throwable) {
    DataBufferFactory bufferFactory = serverWebExchange.getResponse().bufferFactory();
    ErrorResponse errorResponse;

    if (throwable instanceof ErrorResponse r) {
      errorResponse = r;
    } else {
      errorResponse = handleException(throwable);
    }
    serverWebExchange.getResponse().setStatusCode(errorResponse.getStatusCode());
    serverWebExchange.getResponse().getHeaders().setAll(errorResponse.getHeaders().toSingleValueMap());
    serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);
    errorResponse.getBody().setInstance(serverWebExchange.getRequest().getURI());
    DataBuffer dataBuffer;
    try {
      dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(errorResponse.getBody()));
    } catch (JsonProcessingException e) {
      dataBuffer = bufferFactory.wrap("".getBytes());
    }
    return serverWebExchange.getResponse().writeWith(Mono.just(dataBuffer));
  }

  private ErrorResponse handleException(Throwable ex) {
    return new ErrorResponseException(HttpStatusCode.valueOf(500), ex);
  }
}
