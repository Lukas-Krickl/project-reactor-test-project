package io.lukas_krickl.car_service.configuration;

import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

@Configuration
public class NettyMetricsConfiguration implements NettyServerCustomizer {
  @Override
  public HttpServer apply(HttpServer httpServer) {
    return httpServer.metrics(true, uri -> "/*")
      .protocol(HttpProtocol.H2C, HttpProtocol.HTTP11)
      .accessLog(true)
      .wiretap(true);
  }
}
