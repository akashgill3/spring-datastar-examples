package com.akashgill3.springdatastarexamples.web;

import com.akashgill3.springdatastarexamples.datastar.Datastar;
import com.akashgill3.springdatastarexamples.datastar.SpringDatastar;
import com.akashgill3.springdatastarexamples.datastar.events.DatastarEvent;
import com.akashgill3.springdatastarexamples.datastar.events.PatchElementsEvent;
import com.akashgill3.springdatastarexamples.datastar.events.PatchSignalsEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Controller
@RequestMapping("/test")
public class TestController {

    private final Logger log = Logger.getLogger(getClass().getName());
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;
    private final Datastar datastar;
    private static final Executor EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    public TestController(TemplateEngine templateEngine, Datastar datastar, ObjectMapper objectMapper) {
        this.templateEngine = templateEngine;
        this.datastar = datastar;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public SpringDatastar.DatastarSseEmitter get(@RequestParam(name = "datastar") String datastarQuery) throws IOException {
        List<DatastarEvent> events = readEvents(datastarQuery);
        SpringDatastar.DatastarSseEmitter emitter = null;
        for (DatastarEvent event : events) {
            if (event instanceof PatchElementsEvent e) {
                emitter = datastar.patchElements(e);
            } else if (event instanceof PatchSignalsEvent e) {
                emitter = datastar.patchSignals(e);
            }
        }
        if (emitter != null) {
            emitter.complete();
        }
        return emitter;
    }

    @PostMapping
    public SpringDatastar.DatastarSseEmitter handlePost(@RequestParam(name = "datastar") String datastarQuery) throws IOException {
        List<DatastarEvent> events = readEvents(datastarQuery);
        SpringDatastar.DatastarSseEmitter emitter = null;
        for (DatastarEvent event : events) {
            if (event instanceof PatchElementsEvent e) {
                emitter = datastar.patchElements(e);
            } else if (event instanceof PatchSignalsEvent e) {
                emitter = datastar.patchSignals(e);
            } else {
                throw new IllegalArgumentException("Unknown event type: " + event.getClass().getSimpleName());
            }
        }
        if (emitter != null) {
            emitter.complete();
        }
        return emitter;
    }

    public List<DatastarEvent> readEvents(String json) throws IOException {
        try {
            DatastarTestRequest testRequest = objectMapper.readValue(json, DatastarTestRequest.class);
            List<DatastarEvent> eventList = new ArrayList<>();
            if (testRequest.events() != null) {
                for (DatastarTestEvent event : testRequest.events()) {
                    eventList.add(mapToEvent(event));
                }
            }
            return eventList;
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    private DatastarEvent mapToEvent(DatastarTestEvent event) throws IOException {
        return switch (event.type()) {
            case "patchElements" -> {
                var options = PatchElementsEvent.Options.builder();

                if (event.eventId() != null) {
                    options.eventId(event.eventId());
                }
                if (event.mode() != null) {
                    options.mode(PatchElementsEvent.ElementPatchMode.valueOf(event.mode().substring(0, 1).toUpperCase() + event.mode().substring(1)));
                }
                if (event.retryDuration() != null) {
                    options.retryDuration(Long.parseLong(event.retryDuration()));
                }
                if (event.selector() != null) {
                    options.selector(event.selector());
                }
                if (event.useViewTransition() != null) {
                    options.useViewTransition(event.useViewTransition());
                }
                if (event.namespace() != null) {
                    options.namespace(event.namespace());
                }
                yield PatchElementsEvent.withOptions(event.elements, options.build());
            }
            case "patchSignals" -> {
                var optionsBuilder = PatchSignalsEvent.Options.builder();
                if (event.retryDuration() != null) {
                    optionsBuilder.retryDuration(Long.parseLong(event.retryDuration()));
                }
                if (event.onlyIfMissing() != null) {
                    optionsBuilder.onlyIfMissing(event.onlyIfMissing());
                }
                if (event.eventId() != null) {
                    optionsBuilder.eventId(event.eventId());
                }

                String signals = null;
                if (event.signalsRaw() != null) {
                    signals = event.signalsRaw();
                } else if (event.signals != null) {
                    try {
                        signals = objectMapper.writeValueAsString(event.signals());
                    } catch (JacksonException e) {
                        log.severe("Failed to serialize signals: " + event.signals());
                    }
                }
                yield PatchSignalsEvent.withOptions(signals, optionsBuilder.build());
            }
            case "executeScript" -> {
                log.warning("executeScript event not yet supported");
                yield null;
            }
            default -> throw new IllegalArgumentException("Unknown event type: " + event.type());
        };
    }

    public record DatastarTestRequest(@JsonProperty("events") List<DatastarTestEvent> events) {
    }

    public record DatastarTestEvent(@JsonProperty("type") String type, @JsonProperty("elements") String elements,
                                    @JsonProperty("script") String script, @JsonProperty("eventId") String eventId,
                                    @JsonProperty("retryDuration") String retryDuration,
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


    private String renderTemplate(String templateName, Map<String, Object> attributes) {
        StringOutput output = new StringOutput();
        templateEngine.render(templateName.endsWith(".jte") ? templateName : templateName + ".jte", attributes, output);
        return output.toString();
    }

    private String renderTemplate(String templateName) {
        StringOutput output = new StringOutput();
        templateEngine.render(templateName.endsWith(".jte") ? templateName : templateName + ".jte", null, output);
        return output.toString();
    }
}
