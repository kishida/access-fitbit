/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit.sample;

import static kis.fitbit.sample.Secrets.API_KEY;
import static kis.fitbit.sample.Secrets.API_SECRET;
import java.io.IOException;
import java.time.LocalDate;
import kis.fitbit.FitbitConnection;
import kis.fitbit.mongo.FitbitMongo;

/**
 *
 * @author naoki
 */
public class AccessFitbit {

    private static final String USER_NAME = "4BRXLT";

    public static void main(String[] args) throws IOException{
        var conn = new FitbitConnection(API_KEY, API_SECRET);
        conn.authorize("kishida");
        
        //var data = conn.readData(USER_NAME);
        //Files.writeString(Path.of("step-range.json"), data);
        
        var sl = conn.retrieveSleeps(USER_NAME, LocalDate.of(2018,8,16), LocalDate.of(2018,8,31));
        System.out.println(sl);
        
        try(var con = new FitbitMongo()) {
            if (!sl.getSleep().isEmpty()) {
                con.addSleeps("kishida", sl);
            } else {
                System.out.println("No data");
            }
        }
        
        /*
        var data = conn.devices(USER_NAME);
        Files.writeString(Path.of("devices.json"), data);
        
        var devices = conn.retrieveDevices(USER_NAME);
        
        for (var d : devices) {
            System.out.println(d);
            System.out.printf("%s %s %s%n", d.getType(), d.getLastSyncTime(), d.getBattery());
        }
*/
        
        // if (true) return;
        /*
        var date = LocalDate.of(2018, 6, 29);
        var sleep = conn.retrieveSleeps(USER_NAME, date);
        var heart = conn.retrieveHeartBeats(USER_NAME, date);
        var step = conn.retrieveSteps(USER_NAME, date);
        DrawGraph.putFrame(step, heart, sleep);
*/
    }

}
