/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 *
 * @author naoki
 */
@Data
public class ActivitySteps {
    @JsonProperty("activities-steps")
    List<TrackerSteps> steps;
    @JsonProperty("activities-steps-intraday")
    Dataset intraday;
    
    @Data
    public static class ActivityTrackerSteps {
        @JsonProperty("activities-tracker-steps")
        List<TrackerSteps> steps;
    }
    
    @Data
    public static class TrackerSteps {
        LocalDate dateTime;
        int value;
    }
    
    
}
