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
import kis.fitbit.HeartBeats;

/**
 *
 * @author naoki
 */
public class ReadFitbitHearts {
    public static void main(String[] args) throws IOException {
        var mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        HeartBeats sleep = mapper.readValue(new FileReader("heartbeat.json"), HeartBeats.class);
        System.out.println(sleep);

    }
}
