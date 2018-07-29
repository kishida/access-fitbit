/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.FileReader;
import java.io.IOException;
import kis.fitbit.ActivitySteps;
import kis.fitbit.ActivitySteps.ActivityTrackerSteps;

/**
 *
 * @author naoki
 */
public class ReadFitbitSteps {
    public static void main(String[] args) throws IOException {
        var mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ActivitySteps sleep = mapper.readValue(new FileReader("activityStep.json"), ActivitySteps.class);
        System.out.println(sleep);
        
        var tracker = mapper.readValue(new FileReader("activityTrackerStep.json"), ActivityTrackerSteps.class);
        System.out.println(tracker);
        
    }
}
