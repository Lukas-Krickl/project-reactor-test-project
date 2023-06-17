package io.lukas_krickl.car_service.error_handling;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponseExceptionFactory {
  public static ErrorResponseException createBadRequestResponse(String message, @Nullable Throwable cause) {
    return new ErrorResponseException(
      HttpStatusCode.valueOf(400),
      ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), message),
      cause
    );
  }

  public static ErrorResponseException createBadRequestResponse(String invalidParameter, String location, String reason, @Nullable Throwable cause) {
    var problemDetail = ProblemDetail.forStatus(HttpStatusCode.valueOf(400));
    var invalidParams = Map.of(
      "parameter", invalidParameter,
      "location", location,
      "reason", reason
    );
    problemDetail.setProperty("invalid-param", invalidParams);
    return new ErrorResponseException(HttpStatusCode.valueOf(400), problemDetail, cause);
  }
}
