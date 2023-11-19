package io.lukas_krickl.clustering_service.configuration;

import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.logging.AccessLogFactory;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class NettyMetricsConfiguration implements NettyServerCustomizer, WebClientCustomizer {
  @Override
  public HttpServer apply(HttpServer httpServer) {
    return httpServer.metrics(true, uri -> "/*")
      .protocol(HttpProtocol.H2C, HttpProtocol.HTTP11)
      .accessLog(true)
      .wiretap(true);
  }

  @Override
  public void customize(WebClient.Builder webClientBuilder) {
    var connectionPool = ConnectionProvider.builder("custom pool").build();
    var httpClient = HttpClient.create(connectionPool)
      .compress(true)
      .wiretap(true)
      .protocol(HttpProtocol.HTTP11, HttpProtocol.H2C, HttpProtocol.H2);
    webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
  }
}
