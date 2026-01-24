package com.akashgill3.springdatastarexamples.datastar;

import com.akashgill3.springdatastarexamples.datastar.events.PatchElementsEvent.ElementPatchMode;

public final class Consts {
    private Consts() {
    }

    public static final String DATASTAR_KEY = "datastar";

    public static final Long DEFAULT_SSE_RETRY_DURATION = 1000L;
    public static final boolean DEFAULT_ELEMENTS_USE_VIEW_TRANSITIONS = false;
    public static final boolean DEFAULT_PATCH_SIGNAL_ONLY_IF_MISSING = false;
    public static final boolean DEFAULT_EXECUTE_AUTO_REMOVE = true;
    public static final String DEFAULT_NAMESPACE = "html";
    public static final ElementPatchMode DEFAULT_ELEMENT_PATCH_MODE = ElementPatchMode.Outer;

    public static final String SELECTOR_DATALINE_LITERAL = "selector";
    public static final String MODE_DATALINE_LITERAL = "mode";
    public static final String NAMESPACE_DATALINE_LITERAL = "namespace";
    public static final String ELEMENTS_DATALINE_LITERAL = "elements";
    public static final String USE_VIEW_TRANSITION_DATALINE_LITERAL = "useViewTransition";
    public static final String SIGNALS_DATALINE_LITERAL = "signals";
    public static final String ONLY_IF_MISSING_DATALINE_LITERAL = "onlyIfMissing";

}
