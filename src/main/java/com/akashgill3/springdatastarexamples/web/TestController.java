package com.akashgill3.springdatastarexamples.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.akashgill3.datastar.Datastar;
import io.github.akashgill3.datastar.DatastarSseEmitter;
import io.github.akashgill3.datastar.events.ElementPatchMode;
import io.github.akashgill3.datastar.events.Namespace;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Controller for testing Datastar Spring Boot Starter against SDK testsuite
 */
@Controller
@RequestMapping("/test")
public class TestController {

  private final Logger log = Logger.getLogger(getClass().getName());
  private final ObjectMapper objectMapper;
  private final Datastar datastar;
  private static final Executor EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

  public TestController(Datastar datastar, ObjectMapper objectMapper) {
    this.datastar = datastar;
    this.objectMapper = objectMapper;
  }

  @GetMapping
  public DatastarSseEmitter get(HttpServletRequest request) throws IOException {
    DatastarSseEmitter sseEmitter = datastar.createEmitter();
    var datastarTestRequest = datastar.readSignals(request, DatastarTestRequest.class);
    log.info("SSE: " + datastarTestRequest.events());
    EXECUTOR.execute(() -> {
      handleRequest(sseEmitter, datastarTestRequest);
    });
    return sseEmitter;
  }

  @PostMapping
  public DatastarSseEmitter handlePost(HttpServletRequest request) throws IOException {
    DatastarSseEmitter sse = datastar.createEmitter();
    var datastarTestRequest = datastar.readSignals(request, DatastarTestRequest.class);
    log.info("SSE: " + datastarTestRequest.events());
    EXECUTOR.execute(() -> {
      handleRequest(sse, datastarTestRequest);
      sse.complete();
    });
    return sse;
  }

  public void handleRequest(DatastarSseEmitter sseEmitter, DatastarTestRequest datastarTestRequest) {
    for (DatastarTestEvent event : datastarTestRequest.events()) {
      switch (event.type()) {
        case "patchElements" -> {
          try {
            sseEmitter.patchElements(event.elements(), options -> {
              if (event.eventId() != null) options.eventId(event.eventId());
              if (event.mode != null)
                options.mode(ElementPatchMode.valueOf(event.mode.substring(0, 1).toUpperCase() + event.mode.substring(1).toLowerCase()));
              if (event.namespace != null)
                options.namespace(Namespace.valueOf(event.namespace.substring(0, 1).toUpperCase() + event.namespace.substring(1).toLowerCase()));
              if (event.retryDuration != null) options.retryDuration(event.retryDuration);
              if (event.selector != null) options.selector(event.selector);
              if (event.useViewTransition != null) options.useViewTransition(event.useViewTransition);
            });
          } catch (IOException e) {
            log.severe(e.getMessage());
          }
        }
        case "patchSignals" -> {
          try {
            sseEmitter.patchSignals(event.signals() != null ? objectMapper.writeValueAsString(event.signals()) : event.signalsRaw(),
                options -> {
                  if (event.eventId() != null) options.eventId(event.eventId());
                  if (event.retryDuration() != null) options.retryDuration(event.retryDuration());
                  if (event.onlyIfMissing() != null) options.onlyIfMissing(event.onlyIfMissing());
                });
          } catch (IOException e) {
            log.severe(e.getMessage());
          }
        }
        case "executeScript" -> {
          try {
            sseEmitter.executeScript(event.script(),
                options -> {
                  if (event.eventId() != null) options.eventId(event.eventId());
                  if (event.autoRemove() != null) options.autoRemove(event.autoRemove());
                  if (event.retryDuration() != null) options.retryDuration(event.retryDuration());
                  if (event.attributes() != null) {
                    for (Map.Entry<String, String> entry : event.attributes().entrySet()) {
                      options.attribute(entry.getKey(), entry.getValue());
                    }
                  }
                });
          } catch (IOException e) {
            log.severe(e.getMessage());
          }
        }
        default -> log.warning("Unknown SSE event type: " + event.type());
      }
    }
    sseEmitter.complete();
  }

  record DatastarTestRequest(@JsonProperty("events") List<DatastarTestEvent> events) {
  }

  record DatastarTestEvent(@JsonProperty("type") String type, @JsonProperty("elements") String elements,
                           @JsonProperty("script") String script, @JsonProperty("eventId") String eventId,
                           @JsonProperty("retryDuration") Long retryDuration,
                           @JsonProperty("namespace") String namespace,
                           @JsonProperty("mode") String mode,
                           @JsonProperty("useViewTransition") Boolean useViewTransition,
                           @JsonProperty("attributes") Map<String, String> attributes,
                           @JsonProperty("signals") Object signals,
                           @JsonProperty("signals-raw") String signalsRaw,
                           @JsonProperty("autoRemove") Boolean autoRemove,
                           @JsonProperty("onlyIfMissing") Boolean onlyIfMissing,
                           @JsonProperty("selector") String selector
  ) {
  }
}
