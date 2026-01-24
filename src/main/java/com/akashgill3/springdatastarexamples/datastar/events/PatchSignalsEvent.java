package com.akashgill3.springdatastarexamples.datastar.events;

import com.akashgill3.springdatastarexamples.datastar.Consts;

public record PatchSignalsEvent(String script, Options options) implements DatastarEvent {
    public static final Options DEFAULT_OPTIONS = Options.builder().build();

    public static PatchSignalsEvent of(String script) {
        return new PatchSignalsEvent(script, DEFAULT_OPTIONS);
    }

    public static PatchSignalsEvent withOptions(String script, Options options) {
        return new PatchSignalsEvent(script, options);
    }


    public record Options(String eventId, boolean onlyIfMissing, Long retryDuration) {
        public static OptionsBuilder builder() {
            return new OptionsBuilder();
        }

        public static class OptionsBuilder {
            private String eventId;
            private boolean onlyIfMissing = Consts.DEFAULT_PATCH_SIGNAL_ONLY_IF_MISSING;
            private Long retryDuration = Consts.DEFAULT_SSE_RETRY_DURATION;

            private OptionsBuilder() {
            }

            public OptionsBuilder eventId(String eventId) {
                this.eventId = eventId;
                return this;
            }

            public OptionsBuilder onlyIfMissing(boolean onlyIfMissing) {
                this.onlyIfMissing = onlyIfMissing;
                return this;
            }

            public OptionsBuilder retryDuration(Long retryDuration) {
                this.retryDuration = retryDuration;
                return this;
            }

            public Options build() {
                return new Options(eventId, onlyIfMissing, retryDuration);
            }
        }
    }

}
