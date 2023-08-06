package io.lukas_krickl.car_service.error_handling;

import io.lukas_krickl.car_service.Application;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

import java.util.List;

@Configuration
@ConditionalOnProperty(value = "application.include-error-causing-element.enabled", havingValue = "true")
@ConfigurationProperties("application.include-error-causing-element")
@Data
/*
 * This class configures a reactor hook to add elements that caused an exception as actual nested cause of the thrown exception
 * Individual objects or objects within packages to include or exclude can be defined by configuration.
 */
public class ElementErrorCauseConfiguration {
  private List<String> includedClasses = List.of();
  private List<String> includedPackages = List.of(Application.class.getPackageName()); //include all classes of this application by default
  private List<String> excludedClasses = List.of();
  private List<String> excludedPackages = List.of();

  @PostConstruct
  void init() {
    Hooks.onOperatorError("addErrorCausingElementToException", (throwable, o) -> {
      if (o != null && this.elementShouldBeIncludedAndNotExcluded(o)) {
        return includeErrorCausingElement(throwable, o);
      } else {
        return throwable;
      }
    });
  }

  private boolean elementShouldBeIncludedAndNotExcluded(Object o) {
    var elementClass = o.getClass();
    return excludedPackages.stream().noneMatch(excludedPackage -> elementClass.getPackageName().startsWith(excludedPackage))
      && !excludedClasses.contains(elementClass.getName())
      && (includedPackages.stream().anyMatch(excludedPackage -> elementClass.getPackageName().startsWith(excludedPackage))
      || includedClasses.contains(elementClass.getName()));
  }

  private Throwable includeErrorCausingElement(Throwable throwable, Object element) {
    var elementCause = new Throwable("element in reactor chain: " + element);
    elementCause.setStackTrace(new StackTraceElement[0]);
    Throwable rootCause = throwable;
    while (rootCause.getCause() != null) {
      rootCause = throwable.getCause();
    }
    rootCause.initCause(elementCause);
    return throwable;
  }
}
