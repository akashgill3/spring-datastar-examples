package com.akashgill3.springdatastarexamples.datastar;

import com.akashgill3.springdatastarexamples.datastar.events.DatastarEvent;
import com.akashgill3.springdatastarexamples.datastar.events.DatastarEventType;
import com.akashgill3.springdatastarexamples.datastar.events.PatchElementsEvent;
import com.akashgill3.springdatastarexamples.datastar.events.PatchSignalsEvent;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class SpringDatastar implements Datastar {

    private static final Logger log = LoggerFactory.getLogger(SpringDatastar.class);

    @Override
    public DatastarSseEmitter patchElements(PatchElementsEvent event) throws IOException {
        DatastarSseEmitter emitter = new DatastarSseEmitter();
        emitter.send(event);
        return emitter;
    }

    @Override
    public DatastarSseEmitter patchSignals(PatchSignalsEvent event) throws IOException {
        DatastarSseEmitter emitter = new DatastarSseEmitter();
        emitter.send(event);
        return emitter;
    }

    @Override
    public ResponseBodyEmitter executeScript(String script) {
        return null;
    }

    @Override
    public ResponseBodyEmitter executeScript(String script, boolean autoRemove, String[] attributes, Long retryDuration) {
        return null;
    }

    @Override
    public ResponseBodyEmitter redirect() {
        return null;
    }

    public static class DatastarSseEmitter extends ResponseBodyEmitter {

        public void send(DatastarEvent event) throws IOException {
            switch (event) {
                case PatchElementsEvent patchElementsEvent -> super.send(formatPatchElementsEvent(patchElementsEvent));
                case PatchSignalsEvent patchSignalsEvent -> super.send(formatPatchSignalsEvent(patchSignalsEvent));
                default -> throw new IllegalStateException("Unexpected value: " + event);
            }
        }

        @Override
        protected void extendResponse(@NonNull ServerHttpResponse outputMessage) {
            super.extendResponse(outputMessage);

            HttpHeaders headers = outputMessage.getHeaders();
            if (headers.getContentType() == null) {
                headers.setContentType(MediaType.TEXT_EVENT_STREAM);
            }
        }

        private Set<DataWithMediaType> formatPatchElementsEvent(PatchElementsEvent event) {
            Set<DataWithMediaType> dataToSend = new LinkedHashSet<>(4);
            StringBuilder sb = new StringBuilder();

            appendLine(sb, "event", DatastarEventType.PATCH_ELEMENTS.value);

            if (event.options().eventId() != null) {
                appendLine(sb, "id", event.options().eventId());
            }
            if (event.options().retryDuration() != null) {
                appendLine(sb, "retry", event.options().retryDuration());
            }
            if (event.options().mode() != null) {
                appendLine(sb, Consts.MODE_DATALINE_LITERAL, event.options().mode().value);
            }
            if (event.options().selector() != null) {
                appendLine(sb, Consts.SELECTOR_DATALINE_LITERAL, event.options().selector());
            }
            if (event.options().useViewTransition()) {
                appendLine(sb, Consts.USE_VIEW_TRANSITION_DATALINE_LITERAL, "true");
            }
            if (event.options().namespace() != null) {
                appendLine(sb, Consts.NAMESPACE_DATALINE_LITERAL, event.options().namespace());
            }

            if (event.elements() != null && !event.elements().isEmpty()) {
                event.elements().lines()
                        .filter(line -> !line.isBlank())
                        .forEach(line -> appendDataLine(sb, Consts.ELEMENTS_DATALINE_LITERAL, line));
            }

            log.info("Sending SSE event: \n{}\n", sb);
            return dataToSend;
        }

        private Set<DataWithMediaType> formatPatchSignalsEvent(PatchSignalsEvent event) {
            Set<DataWithMediaType> dataToSend = new LinkedHashSet<>(4);

            StringBuilder sb = new StringBuilder();

            appendLine(sb, "event", DatastarEventType.PATCH_SIGNALS.value);

            if (event.options().retryDuration() != null) {
                appendLine(sb, "retry", event.options().retryDuration());
            }

            appendLine(sb, Consts.MODE_DATALINE_LITERAL, PatchElementsEvent.ElementPatchMode.Append.value);


            if (event.options().onlyIfMissing()) {
                appendLine(sb, Consts.ONLY_IF_MISSING_DATALINE_LITERAL, "true");
            }

            if (event.script() != null && !event.script().isEmpty()) {
                event.script().lines()
                        .filter(line -> !line.isBlank())
                        .forEach(line -> appendDataLine(sb, Consts.SIGNALS_DATALINE_LITERAL, line));
            }

            return dataToSend;
        }

        private void appendLine(StringBuilder sb, String literal, Object value) {
            sb.append("%s: %s\n".formatted(literal, value));
        }

        private void appendDataLine(StringBuilder sb, String literal, String value) {
            sb.append("data: %s %s\n".formatted(literal, value));
        }
    }

}
