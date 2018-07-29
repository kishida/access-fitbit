/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.IntStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import kis.fitbit.ActivitySteps;
import kis.fitbit.DailySleep;
import kis.fitbit.Dataset;
import kis.fitbit.HeartBeats;
import lombok.AllArgsConstructor;

/**
 *
 * @author naoki
 */
public class DrawGraph {
    public static void main(String[] args) throws IOException {
        var mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ActivitySteps step = mapper.readValue(new FileReader("activityStep.json"), ActivitySteps.class);
        HeartBeats heart = mapper.readValue(new FileReader("heartbeat.json"), HeartBeats.class);
        DailySleep sleep = mapper.readValue(new FileReader("sleep.json"), DailySleep.class);
        putFrame(step, heart, sleep);
    }
    
    static void putFrame(ActivitySteps step, HeartBeats heart, DailySleep sleep) {
        BufferedImage img = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 600, 400);

        g.setColor(Color.GRAY);
        var range = new ChartRange(40, 20, 550, 25, 0, 100);
        IntStream.of(0, 6, 12, 18).forEach(t -> {
            int x = range.getX(LocalTime.of(t, 0));
            g.drawLine(x, 15, x, 340);
        });
        
        drawHeart(g, heart.getIntraday().getDataset());
        drawSteps(g, step.getIntraday().getDataset(), 20);
        drawSleeps(g, sleep, 40, 20, 550, 20);
        
        JFrame f = new JFrame();
        JLabel lbl = new JLabel(new ImageIcon(img));
        f.add(lbl);
        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(630, 430);
        f.setVisible(true);
    }
    
    @AllArgsConstructor
    static class ChartRange {
        int offsetX;
        int offsetY;
        int width;
        int height;
        int low;
        int high;
        
        private static final int MINUTES = 60 * 24;
        int getY(int v) {
            return offsetY + height - (v - low) * height / (high - low);
        }
        int getX(LocalTime t) {
            int m = (int)t.getLong(ChronoField.MINUTE_OF_DAY);
            return m * width / MINUTES + offsetX;
        }        
        
        void drawHorizontal(Graphics2D g, int value) {
            g.drawLine(offsetX, getY(value), offsetX + width, getY(value));
        }
        
        int getBottom() {
            return offsetY + height;
        }
    }
    
    static void drawHeart(Graphics2D g, List<Dataset.DataElement> elements) {
        var p = new Path2D.Double() ;
        
        boolean first = true;
        int top = Integer.MIN_VALUE;
        int bottom = Integer.MAX_VALUE;
        
        var f = new ChartRange(40, 200, 550, 150, 45, 150);
        g.setColor(Color.GRAY);
        var oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(1));
        f.drawHorizontal(g, 60);
        f.drawHorizontal(g, 100);
        g.setColor(Color.BLACK);
        g.drawString(" 60", 5, f.getY(60) + 5);
        g.drawString("100", 5, f.getY(100) + 5);
        IntStream.iterate(3, x -> x + 3).takeWhile(x -> x < 24).forEach(t ->
            g.drawString(String.format("% 2d", t), f.getX(LocalTime.of(t, 0)) - 8, f.getBottom() + 15));
        for (var elm : elements) {
            int x = f.getX(elm.getTime());
            int y = f.getY(elm.getValue());
            if (first) {
                p.moveTo(x, y);
                first = false;
            } else {
                p.lineTo(x, y);
            }
            top = Math.max(top, elm.getValue());
            bottom = Math.min(bottom, elm.getValue());
        }
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.RED);
        g.draw(p);
        g.setStroke(oldStroke);
    }
    
    static void drawSteps(Graphics2D g, List<Dataset.DataElement> elements, int minutes) {
        var start = 0;
        int sum = 0;
        int maxStep = 1000;// 100 for 6/20
        var f = new ChartRange(40, 50, 550, 100, 0, maxStep);
        var next = start + minutes;
        g.setColor(Color.BLACK);
        f.drawHorizontal(g, 0);
        f.drawHorizontal(g, maxStep / 2);
        g.setColor(Color.GREEN);
        var time = LocalTime.of(0, 0);
        for (var elm : elements) {
            var m = elm.getTime().getLong(ChronoField.MINUTE_OF_DAY);
            
            if (m < next) {
                sum += elm.getValue();
            } else {
                var nt = time.plusMinutes(minutes);
                g.fillRect(f.getX(time), f.getY(sum), f.getX(nt) - f.getX(time) - 1, f.getY(0) - f.getY(sum));
                start = next;
                time = nt;
                sum = 0;
                next = start + minutes;
            }
        }
        g.fillRect(f.getX(time), f.getY(sum), f.offsetX + f.width - f.getX(time) - 1, f.getY(0) - f.getY(sum));
        
    }
    
    static void drawSleeps(Graphics2D g, DailySleep sleep, int left, int top, int width, int height) {
        var f = new ChartRange(left, top, width, height, 0, 100);
        g.setColor(Color.BLUE);
        for (var slp : sleep.getSleep()) {
            var start = slp.getStartTime().toLocalTime();
            var end = slp.getEndTime().toLocalTime();
            g.fillRoundRect(f.getX(start), f.getY(100), f.getX(end) - f.getX(start), f.getY(0) - f.getY(100), 15, 15);
        }
    }
}
