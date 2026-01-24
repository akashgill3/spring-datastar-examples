package com.akashgill3.springdatastarexamples.datastar.events;

public enum DatastarEventType {
    PATCH_ELEMENTS("datastar-patch-elements"),
    PATCH_SIGNALS("datastar-patch-signals");

    public final String value;

    DatastarEventType(String value) {
        this.value = value;
    }
}
