package quote.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import javax.annotation.Priority;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quote.service.model.Error;

public final class ExceptionMappers {

  @Provider
  static class WebApplicationExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

    @Override
    public Response toResponse(Throwable throwable) {
      String message = throwable.getMessage();
      if (message == null || message.isEmpty()) {
        message = "An unexpected failure occurred.";
      }
      Error error = new Error().message(message);
      int status = 500;
      if (throwable instanceof WebApplicationException) {
        WebApplicationException webEx = (WebApplicationException) throwable;
        status = webEx.getResponse().getStatus();
      } else {
        LOG.warn("Uncaught exception hit.", throwable);
      }
      return Response.status(status).entity(error).build();
    }
  }

  @Priority(0)
  @Provider
  static class ConstraintViolationExceptionMapper
      implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
      String message =
          exception
              .getConstraintViolations()
              .stream()
              .map(ConstraintViolationExceptionMapper::getValidationMessage)
              .reduce((msg, violation) -> String.format("%s, %s", msg, violation))
              .orElse("Bad Request");
      Error error = new Error().message(message);
      return Response.status(400).entity(error).build();
    }

    public static String getValidationMessage(ConstraintViolation<?> violation) {
      String property = violation.getPropertyPath().toString();
      //Object invalidValue = violation.getInvalidValue();
      String message = violation.getMessage();
      return String.format("%s %s", property, message);
    }
  }

  @Priority(0)
  @Provider
  static class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

    @Override
    public Response toResponse(JsonMappingException exception) {
      Error error = new Error().message(exception.getOriginalMessage());
      return Response.status(400).entity(error).build();
    }
  }
}
