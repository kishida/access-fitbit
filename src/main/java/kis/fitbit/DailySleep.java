/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author naoki
 */
@Data
public class DailySleep {
    
    List<Sleep> sleep;
    DailySummary summary;

    @Data
    public static class RangeSleep {
        List<Sleep> sleep;
    }
    
    @Data
    public static class Sleep {
        LocalDate dateOfSleep;
        long duration;
        int efficiency;
        LocalDateTime endTime;
        int infoCode;
        
        @JsonProperty("isMainSleep")
        boolean mainSleep;
        Levels levels;
        long logId;
        int minutesAfterWakeup;
        int minutesAsleep;
        int minutesAwake;
        int minutesToFallAsleep;
        LocalDateTime startTime;
        int timeInBed;
        String type;
    }
    
    @Data
    public static class Levels {
        List<Level> data;
        List<Level> shortData;
        Summary summary;
    }
    
    @Data
    public static class Level {
        LocalDateTime dateTime;
        String level;
        int seconds;
    }
    
    @Data
    public static class Summary {
        // for stage;
        SummaryElement deep;
        SummaryElement light;
        SummaryElement rem;
        SummaryElement wake;
        
        // for classic
        SummaryElement asleep;
        SummaryElement awake;
        SummaryElement restless;
    }
    
    @Data
    public static class SummaryElement {
        int count;
        int minutes;
        int thirtyDayAvgMinutes;
    }
    
    @Data
    public static class Stages {
        int deep;
        int light;
        int rem;
        int wake;
    }
    
    @Data
    public static class DailySummary {
        Stages stages;
        int totalMinutesAsleep;
        int totalSleepRecords;
        int totalTimeInBed;
    }
}
