/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.jdk11sample;

import java.util.Arrays;

/**
 *
 * @author naoki
 */
public class PlusEqual {
    public static void main(String[] args) {
        String[] s = {"aa", "bb"};
        var i = 0;
        s[i++] += i + "";
        System.out.printf("%s %s %d%n",
                          System.getProperty("java.version"),
                          Arrays.toString(s), i);
        
        // 11-ea [bb2, bb] 2
        // 10.0.1 [bb2, bb] 2
        // 1.8.0_151 [aa1, bb] 1
    }
}
