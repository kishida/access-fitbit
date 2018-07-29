/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit;

import java.time.LocalTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author naoki
 */
@Data
public class Dataset {
    
    List<DataElement> dataset;
    int datasetInterval;
    String datasetType;

    @Data
    public static class DataElement {

        LocalTime time;
        int value;
    }
    
}
