/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit.sample;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import kis.fitbit.mongo.FitbitMongo.MongoSleep;
import lombok.Value;

/**
 *
 * @author naoki
 */
public class DrawSleepGraph {
    @Value
    static class TimeRange {
        LocalDateTime start;
        LocalDateTime end;
    }
    static FitbitService service;
    public static void main(String[] args) {
        service = new FitbitService();
        YearMonth[] month = {YearMonth.of(2018,8)};
        final var width = 500;
        final var height = 700;
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        drawSleepGraph(g, month[0]);

        // ウィンドウ
        var f = new JFrame();
        
        var l = new JLabel(new ImageIcon(image));
        f.add(l);
        
        var lb = new JButton("<");
        f.add(BorderLayout.WEST, lb);
        lb.addActionListener(al -> {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            month[0] = month[0].minusMonths(1);
            drawSleepGraph(g, month[0]);
            l.repaint();
        });
        
        var rb = new JButton(">");
        rb.addActionListener(al -> {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            month[0] = month[0].plusMonths(1);
            drawSleepGraph(g, month[0]);
            l.repaint();
        });
        f.add(BorderLayout.EAST, rb);
        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocation(300, 300);
        f.setSize(600, 750);
        f.setVisible(true);
    }
    
    private static void drawSleepGraph(Graphics2D g, YearMonth month) {
        List<MongoSleep> sleeps = service.retrieveMonthSleeps("kishida", month);

        int yoffset = 25;
        
        // 日付
        g.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
        g.setColor(Color.BLACK);
        g.drawString(String.format("%tY/%<tm", month), 200, 15);
        for (int i = 1; i <= month.lengthOfMonth(); ++i) {
            var day = month.atDay(i);
            g.drawString(String.format("%te(%<ta)", day), 10, i * 20 + 10 + yoffset);
            if (day.get(ChronoField.DAY_OF_WEEK) == 7) {
                g.setColor(new Color(212, 212, 212));
                g.fillRect(60, i * 20 + yoffset - 5, 24 * 17, 20);
                g.setColor(Color.BLACK);
            }
        }
        
        // 縦線
        for (int i = 0; i <= 24; i += 3) {
            if (i % 6 == 0) {
                g.setStroke(new BasicStroke(1));
            } else {
                g.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1, new float[]{5, 5}, 0));
            }
            g.setColor(Color.GRAY);
            g.drawLine(i * 17 + 60, 13 + yoffset, i * 17 + 60, 635 + yoffset);
            g.setColor(Color.BLACK);
            g.drawString(i + "", i * 17 + 55, 655 + yoffset);
        }
        
        var fn = new Object() {
            int getX(LocalDateTime t) {
                return t.getHour() * 17 + t.getMinute() * 17 / 60 + 60;
            }
            int getY(LocalDateTime d) {
                return d.getDayOfMonth() * 20 + yoffset;
            }
        };
        
        g.setColor(Color.BLUE);
        sleeps.stream()
                .map(MongoSleep::getSleepData)
                .map(s -> new TimeRange(s.getStartTime(), s.getEndTime()))
                .flatMap(s -> s.getStart().toLocalDate().equals(s.getEnd().toLocalDate())
                                ? Stream.of(s)
                                : Stream.of(new TimeRange(s.getStart(), LocalDateTime.of(s.getStart().toLocalDate(), LocalTime.of(23, 59))),
                                            new TimeRange(LocalDateTime.of(s.getEnd().toLocalDate(), LocalTime.of(0, 0)), s.getEnd())))
                .forEach(tr -> {
                    g.fillRoundRect(fn.getX(tr.getStart()), fn.getY(tr.getStart()), fn.getX(tr.getEnd()) - fn.getX(tr.getStart()), 8, 2, 2);
                });
    }
}
