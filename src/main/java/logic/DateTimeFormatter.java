package logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

public class DateTimeFormatter {

    public LocalDateTime checkFileNameForDate(String name) {
        boolean approved = false;
        int year = 0;
        int month = 0;
        int day = 0;
        String[] seperatedName = name.split("-");
        if(seperatedName[0].equals("IMG")) {
            if (seperatedName[1].length() == 8) {
                try {
                    System.out.println("substring: " + seperatedName[1]);
                    System.out.println("year: " + seperatedName[1].substring(0, 4));
                    System.out.println("month: " + seperatedName[1].substring(4, 6));
                    System.out.println("day: " + seperatedName[1].substring(6, 8));
                    year = Integer.parseInt(seperatedName[1].substring(0, 4));
                    month = Integer.parseInt(seperatedName[1].substring(4, 6));
                    day = Integer.parseInt(seperatedName[1].substring(6, 8));

                    boolean approvedYear = false;
                    boolean approvedMonth = false;
                    boolean approvedDay = false;

                    LocalDate dateNow = LocalDate.now();
                    if (year > 1990 && year <= dateNow.getYear()) {
                        System.out.println("year passed: " + year);
                        approvedYear = true;
                    } else System.out.println("year NOT passed: " + year);

                    if (month > 0 && month <= 12) {
                        System.out.println("month passed: " + month);
                        approvedMonth = true;
                    } else System.out.println("month NOT passed: " + month);

                    if (day > 0 && day <= 31) {
                        System.out.println("day passed: " + day);
                        approvedDay = true;
                    } else System.out.println("fay NOT passed: " + day);

                    if (approvedYear && approvedMonth && approvedDay) approved = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(approved) {
            return convertFilenNameToDate(year, month, day);
        } else return null;
    }

    private LocalDateTime convertFilenNameToDate(int year, int month, int day) {
        return LocalDateTime.of(year, month, day,0,0);
    }

    public LocalDateTime formate(String str, String name) {
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(str, formatter);
            //System.out.println("1. date: " + date);
            return date;
        } catch(Exception e) {
            //e.printStackTrace();
            //System.out.println("first date pattern not working");
            return dateFormatter2(str, name);

        }
    }

    private LocalDateTime dateFormatter2(String str, String name) {
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            LocalDateTime date = LocalDateTime.parse(str, formatter);
            //System.out.println("2. date: " + date);
            return date;
        } catch(Exception e2) {
            //e2.printStackTrace();
            //System.out.println("bei " + name + " hat Datumsformatierung (" + str + ") nicht geklappt");
            return dateFormatter3(str, name);
        }
    }

    private LocalDateTime dateFormatter3(String str, String name) {
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("EE MMM dd HH:mm:ss XXX yyyy");
            LocalDateTime date = LocalDateTime.parse(str, formatter);
            //System.out.println("3. date: " + date);
            return date;
        } catch(Exception e2) {
            e2.printStackTrace();
            //System.out.println("bei " + name + " hat Datumsformatierung (" + str + ") nicht geklappt");
            System.out.println("WARNING NO DATE :___________________________________________________________________-----------------------");
        }
        return null;
    }

}
