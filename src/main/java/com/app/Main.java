package com.app;

import com.app.util.JsonParser;
import com.app.pojo.Ticket;
import com.app.util.DateTimeUtil;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final String DEPARTURE_CITY = "Владивосток";
    private static final String ARRIVAL_CITY = "Тель-Авив";
    private static final int PERCENTILE = 90;
    private static String fileName;

    public static void main(String[] args) {
        try {
            fileName = args[0];
            List<Long> arrayMinutes = getAllMinutesFromFilteredTickets();
            long averageMinutes = getAverageTimeForFlight(arrayMinutes);
            long percentileMinutes = getPercentile(arrayMinutes);

            System.out.printf("Cреднее время полета между городами %s и %s составляет %d часов %d минут(ы) (всего %d минут(ы))\n",
                    DEPARTURE_CITY, ARRIVAL_CITY,
                    DateTimeUtil.getHoursFromMinutes(averageMinutes),
                    DateTimeUtil.getOutputMinutesFromHours(averageMinutes), averageMinutes);


            System.out.printf("%d-й процентиль времени полета между городами %s и %s составляет %d часов %d минут(ы) (всего %d минут(ы))\n",
                    PERCENTILE, DEPARTURE_CITY, ARRIVAL_CITY,
                    DateTimeUtil.getHoursFromMinutes(percentileMinutes),
                    DateTimeUtil.getOutputMinutesFromHours(percentileMinutes), percentileMinutes);
        } catch (Exception e) {
            System.out.println("Не задан путь к JSON-файлу");
        }
    }


    private static List<Ticket> getAllTicketsFromJson() {
        try {
            JsonNode node = JsonParser.parse(new File(fileName));
            Ticket pojo = JsonParser.fromJson(node, Ticket.class);
            return pojo.getTickets();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Ticket> getFilteredTicketsFromJson() {
        return getAllTicketsFromJson().stream()
                .filter(ticket -> (ticket.getOriginName().equals(DEPARTURE_CITY)
                        && ticket.getDestinationName().equals(ARRIVAL_CITY))
                        || (ticket.getOriginName().equals(ARRIVAL_CITY)
                        && ticket.getDestinationName().equals(DEPARTURE_CITY)))
                .collect(Collectors.toList());
    }

    private static List<Long> getAllMinutesFromFilteredTickets() {
        List<Long> minutes = new ArrayList<>();
        for (Ticket ticket : getFilteredTicketsFromJson()) {
            long duration = DateTimeUtil.compare(ticket.getDepartureDate(), ticket.getDepartureTime(),
                    ticket.getArrivalDate(), ticket.getArrivalTime());
            minutes.add(duration);
        }
        return minutes;
    }

    private static long getSumOfMinutes(List<Long> minutes) {
        return minutes.stream().mapToLong(Long::longValue).sum();
    }

    private static long getAverageTimeForFlight(List<Long> minutes) {
        return getSumOfMinutes(minutes) / minutes.size();
    }

    private static long getPercentile(List<Long> minutes) {
        Collections.sort(minutes);
        if (minutes.isEmpty())
            return 0;
        int index = (int) Math.ceil(PERCENTILE / 100.0 * minutes.size());
        return minutes.get(index - 1);
    }
}
