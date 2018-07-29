/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.jdk11sample;

import java.time.LocalDate;
import java.time.chrono.JapaneseChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 *
 * @author naoki
 */
public class LenientEra {
    public static void main(String[] args) {
        var dtf = new DateTimeFormatterBuilder()
                .appendPattern("GGGG y-M-d")
                .toFormatter(Locale.JAPANESE)
                .withChronology(JapaneseChronology.INSTANCE);
        try {
            System.out.println(LocalDate.parse("平成 32-1-1", dtf));
            System.out.println("Wrong behavior");
        } catch (Exception ex) {
            System.out.println(ex);
        }
        System.out.println(LocalDate.parse("平成 32-1-1", dtf.withResolverStyle(ResolverStyle.LENIENT)));
    }
}
