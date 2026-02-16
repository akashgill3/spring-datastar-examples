@import java.util.Map
@import com.akashgill3.springdatastarexamples.web.ClocksController.ClockData

@param Map<String, ClockData> availableTimeZones

<div id="clock" xmlns="http://www.w3.org/1999/html">
    <div class="clock-container">
        @for (Map.Entry<String, ClockData> timeZone : availableTimeZones.entrySet())
            <div class="clock-wrapper">
                <div class="timezone-label">${timeZone.getKey()}</div>
                <div class="clock">
                    <!-- Clock numbers -->
                    <div class="clock-number" style="top: 5px; left: 50%; transform: translateX(-50%);">12</div>
                    <div class="clock-number" style="top: 50%; right: 5px; transform: translateY(-50%);">3</div>
                    <div class="clock-number" style="bottom: 5px; left: 50%; transform: translateX(-50%);">6</div>
                    <div class="clock-number" style="top: 50%; left: 5px; transform: translateY(-50%);">9</div>

                    <!-- Clock hands -->
                    <div class="hand hour-hand" style="transform: translateX(-50%) rotate(${timeZone.getValue().hourAngle()}deg)"></div>
                    <div class="hand minute-hand" style="transform: translateX(-50%) rotate(${timeZone.getValue().minuteAngle()}deg)"></div>
                    <div class="hand second-hand" style="transform: translateX(-50%) rotate(${timeZone.getValue().secondAngle()}deg)"></div>
                    <div class="clock-center"></div>
                </div>
                <div class="time-display">${timeZone.getValue().time()}</div>
            </div>
        @endfor
    </div>
</div>