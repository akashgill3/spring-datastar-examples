package com.akashgill3.springdatastarexamples.datastar.events;

import com.akashgill3.springdatastarexamples.datastar.Consts;

public record PatchElementsEvent(String elements, Options options) implements DatastarEvent {
    public static final Options DEFAULT_OPTIONS = Options.builder().build();

    public static PatchElementsEvent of(String elements) {
        return new PatchElementsEvent(elements, DEFAULT_OPTIONS);
    }

    public static PatchElementsEvent withOptions(String elements, Options options) {
        return new PatchElementsEvent(elements, options);
    }


    public record Options(String selector, ElementPatchMode mode, boolean useViewTransition, String namespace,
                          String eventId, Long retryDuration) {
        public static OptionsBuilder builder() {
            return new OptionsBuilder();
        }

        public static class OptionsBuilder {
            private String eventId;
            private String selector;
            private ElementPatchMode mode = Consts.DEFAULT_ELEMENT_PATCH_MODE;
            private boolean useViewTransition = Consts.DEFAULT_ELEMENTS_USE_VIEW_TRANSITIONS;
            private String namespace = Consts.DEFAULT_NAMESPACE;
            private Long retryDuration = Consts.DEFAULT_SSE_RETRY_DURATION;

            private OptionsBuilder() {
            }

            public OptionsBuilder eventId(String eventId) {
                this.eventId = eventId;
                return this;
            }

            public OptionsBuilder selector(String selector) {
                this.selector = selector;
                return this;
            }

            public OptionsBuilder mode(ElementPatchMode mode) {
                this.mode = mode;
                return this;
            }

            public OptionsBuilder useViewTransition(boolean useViewTransition) {
                this.useViewTransition = useViewTransition;
                return this;
            }

            public OptionsBuilder namespace(String namespace) {
                this.namespace = namespace;
                return this;
            }

            public OptionsBuilder retryDuration(Long retryDuration) {
                this.retryDuration = retryDuration;
                return this;
            }

            public Options build() {
                return new Options(selector, mode, useViewTransition, namespace, eventId, retryDuration);
            }
        }
    }

    public enum ElementPatchMode {
        Outer("outer"),
        Inner("inner"),
        Remove("remove"),
        Replace("replace"),
        Prepend("prepend"),
        Append("append"),
        Before("before"),
        After("after");

        public final String value;

        ElementPatchMode(String value) {
            this.value = value;
        }

    }

}
