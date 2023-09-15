package io.lukas_krickl.car_service.configuration;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.ObservationTextPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext;

@Configuration
@Slf4j
public class TracingConfiguration {
  @Bean
  ObservationRegistryCustomizer<ObservationRegistry> ignoreActuatorEndpointTraces() {
    return registry -> registry.observationConfig().observationPredicate((name, context) -> {
      if (context instanceof ServerRequestObservationContext serverRequestObservationContext) {
        return !serverRequestObservationContext.getCarrier().getPath().toString().startsWith("/actuator");
      } else {
        return true;
      }
    });
  }

  @Bean
  ObservationTextPublisher textPublisher() {
    return new ObservationTextPublisher(log::info);
  }
}
