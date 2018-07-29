/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.jdk11sample;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;
import java.util.stream.IntStream;

/**
 *
 * @author naoki
 */
public class Base64Performance {
    public static void main(String[] args) {
        var baos = new ByteArrayOutputStream();
        new Random().ints(20 * 1024 * 1024).forEach(n -> baos.write(n));
        var data = baos.toByteArray();

        IntStream.range(0, 30).forEach(n ->
            Base64.getEncoder().encode(data));

        var start = System.currentTimeMillis();
        IntStream.range(0, 300).forEach(n ->
            Base64.getEncoder().encode(data));
        System.out.println(System.currentTimeMillis() - start);
        System.out.println("ea18 5703");
    }
}
