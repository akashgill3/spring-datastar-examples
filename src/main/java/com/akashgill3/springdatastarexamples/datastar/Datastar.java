package com.akashgill3.springdatastarexamples.datastar;

import com.akashgill3.springdatastarexamples.datastar.events.PatchElementsEvent;
import com.akashgill3.springdatastarexamples.datastar.events.PatchSignalsEvent;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;

public interface Datastar {

    SpringDatastar.DatastarSseEmitter patchElements(PatchElementsEvent event) throws IOException;

    SpringDatastar.DatastarSseEmitter patchSignals(PatchSignalsEvent event) throws IOException;

    ResponseBodyEmitter executeScript(String script);

    ResponseBodyEmitter executeScript(String script, boolean autoRemove, String[] attributes, Long retryDuration);

    ResponseBodyEmitter redirect();
}
