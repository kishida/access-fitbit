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
public class HeartBeats {
    
    @JsonProperty("activities-heart")
    List<ActivitiesHeart> hearts;
    
    @JsonProperty("activities-heart-intraday")
    Dataset intraday;
    
    @Data
    public static class ActivitiesHeart {
        LocalDate dateTime;
        ActivitiesHeartValue value;
    }

    @Data
    public static class ActivitiesHeartValue {
        List<HeartRateZone> customHeartRateZones;
        List<HeartRateZone> heartRateZones;
        int restingHeartRate;
    }
    
    @Data
    public static class HeartRateZone {
        double caloriesOut;
        int max;
        int min;
        int minutes;
        String name;
    }
}
