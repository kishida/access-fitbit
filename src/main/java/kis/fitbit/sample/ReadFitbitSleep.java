/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.FileReader;
import java.io.IOException;
import kis.fitbit.DailySleep;

/**
 *
 * @author naoki
 */
public class ReadFitbitSleep {
    public static void main(String[] args) throws IOException {
        var jsonF = new JacksonFactory();
        var parser = jsonF.createJsonParser(new FileReader("sleep.json"));
        var data = parser.parseAndClose(DailySleep.class);
        System.out.println(data);
        
        var mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        DailySleep sleep = mapper.readValue(new FileReader("sleep.json"), DailySleep.class);
        System.out.println(sleep);
    }
}
