/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit.sample;

import java.util.Objects;
import kis.fitbit.FitbitConnection;
import lombok.Data;

/**
 *
 * @author naoki
 */
@Data
public class User {
    String userName;
    
    transient FitbitConnection con;

    public User(String userName) {
        this.userName = userName;
    }
    
    FitbitConnection getConnection() {
        Objects.requireNonNull(userName);
        if (con == null) {
            con = new FitbitConnection(Secrets.API_KEY, Secrets.API_SECRET);
            con.authorize(userName);
        }
        return con;
    }
}
