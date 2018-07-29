/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author naoki
 */
@Data
public class Device {
    String battery;
    String batteryLevel;
    String deviceVersion;
    String id;
    LocalDateTime lastSyncTime;
    String type;
    List<String> features;
    String mac;
}
