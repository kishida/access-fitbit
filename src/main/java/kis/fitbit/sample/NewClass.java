/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit.sample;

import java.util.Set;

/**
 *
 * @author naoki
 */
public class NewClass {

    public static void main(String[] args) {
          var a = 102;
          var strs = Set.of("aa", "bb");

        System.out.println(a);
        System.out.println(strs);
        System.out.println(convertToUnicode("テスト"));
    }

    private static String convertToUnicode(String original) {
          var sb = new StringBuilder();
        for (  var i = 0; i < original.length(); i++) {
            sb.append(String.format("\\u%04X", Character.codePointAt(original, i)));
        }
          var unicode = sb.toString();
        return unicode;
    }
}
