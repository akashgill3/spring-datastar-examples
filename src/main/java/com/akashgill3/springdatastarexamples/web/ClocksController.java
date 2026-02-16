package com.akashgill3.springdatastarexamples.web;

import com.akashgill3.springdatastarexamples.TemplateRenderer;
import io.github.akashgill3.datastar.Datastar;
import io.github.akashgill3.datastar.DatastarSseEmitter;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Controller
public class ClocksController {
  private static final Executor EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

  private final TemplateRenderer templateRenderer;
  private final Datastar datastar;

  public ClocksController(TemplateRenderer templateRenderer, Datastar datastar) {
    this.templateRenderer = templateRenderer;
    this.datastar = datastar;
  }

  @GetMapping("/clock")
  public DatastarSseEmitter streamClockTime() {
    DatastarSseEmitter emitter = datastar.createEmitter(60_000L);
    EXECUTOR.execute(() -> {
      while (true) {
        try {
          emitter.patchElements(templateRenderer.renderTemplate(
              "components/clock",
              Map.of("availableTimeZones", generateClockData())
          ));
          Thread.sleep(1000L);
        } catch (Exception e) {
          emitter.completeWithError(e);
          break;
        }
      }
    });
    return emitter;
  }

  private static @NonNull Map<String, ClockData> generateClockData() {
    Instant now = Instant.now();
    return ZoneId.getAvailableZoneIds().stream()
        .map(ZoneId::of)
        .map(zoneId -> Map.entry(zoneId.getRules().getOffset(now), LocalTime.now(zoneId)))
        .sorted(Comparator.comparingInt(entry -> entry.getKey().getTotalSeconds()))
        .collect(LinkedHashMap::new,
            (map, entry) -> {
              int hour = entry.getValue().getHour();
              int minute = entry.getValue().getMinute();
              int second = entry.getValue().getSecond();

              // Calculate angles (0° = 12 o'clock, clockwise)
              double hourAngle = (hour % 12) * 30 + minute * 0.5; // 30° per hour + minute adjustment
              double minuteAngle = minute * 6 + second * 0.1; // 6° per minute + second adjustment
              double secondAngle = second * 6; // 6° per second

              map.put(
                  entry.getKey().getId(),
                  new ClockData(entry.getValue().format(ClocksController.TIME_FORMATTER), hourAngle, minuteAngle, secondAngle)
              );
            }, Map::putAll);
  }

  public record ClockData(String time, double hourAngle, double minuteAngle, double secondAngle) {
  }

}
