/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit.sample;

import java.io.Closeable;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import kis.fitbit.mongo.FitbitMongo;
import kis.fitbit.mongo.FitbitMongo.MongoSleep;

/**
 *
 * @author naoki
 */
public class FitbitService implements Closeable {
    FitbitMongo mongo;
    Clock clock = Clock.systemDefaultZone();

    private FitbitMongo getMongo() {
        if (mongo == null) {
            mongo = new FitbitMongo();
        }
        return mongo;
    }
    
    public List<MongoSleep> retrieveMonthSleeps(String user, YearMonth month) {
        List<MongoSleep> sleeps;
        // if (nowMonth > month && getMongo().sleepStored(user, month)) {
        sleeps = getMongo().retrieveSleep(user, month.atDay(1), month.atEndOfMonth());
        // } else {
        //   sleeps = fitbit.get();
        //   getMongo().store(sleeps);
        //   getMongo().sleepStored(user, month, true);
        // }
        return sleeps;
    }

    public List<MongoSleep> retrieveSleeps(String user, LocalDate date) {
        return getMongo().retrieveSleep(user, date);
    }
    
    @Override
    public void close() {
        if (mongo == null) {
            return;
        }
        mongo.close();
    }
    
    public static void main(String[] args) {
        try(var serv = new FitbitService()) {
            System.out.println(serv.retrieveSleeps("kishida", LocalDate.of(2018, 6,30)));
            for (var s : serv.retrieveMonthSleeps("kishida", YearMonth.of(2018, 7))) {
                System.out.println(s.getSleepData().getStartTime() + " - " + s.getSleepData().getEndTime());
            }
        }
    }
}
