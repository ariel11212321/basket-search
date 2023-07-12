package com.example.a12thproject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class WaitUntilNextHourTask implements Runnable {
    @Override
    public void run() {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());

                // Round up to the next hour
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                // Get the time remaining until the next rounded hour
                long timeRemaining = calendar.getTimeInMillis() - System.currentTimeMillis();

                try {
                    Thread.sleep(timeRemaining);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


    }
}