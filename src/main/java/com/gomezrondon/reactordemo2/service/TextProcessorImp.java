package com.gomezrondon.reactordemo2.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TextProcessorImp implements TextProcessorService {

    public Flux<String> dataLoadStatFormat(String line){

        List<String> tarfilesProcessed = new ArrayList<>();

        String[] var = line.split(" ");
        String dataLoadDate = var[0]+var[1].substring(0,8);;
        String pattern1 = "\\d{14}";
        String pattern2 = "(?<=download\\/).*";
        String pattern3 = "\\s(\\d*)\\s";
        String dataFeedString = getPattern(line, pattern1); //\s(\d*)\s
        String tarFileName = getPattern(line, pattern2);
        String tranCode = getPattern(line, pattern3).trim();
        String paddedTranCode = String.format("%8s", tranCode);
        String datePatter1 = "yyyMMddHHmmss";
        String datePatter2 = "yyy-MM-ddHH:mm:ss";
        LocalDateTime dateTime = convertStringToDate(dataLoadDate, datePatter2);
        LocalDateTime dateTime2 = convertStringToDate(dataFeedString, datePatter1);
        String paddeddataLoadDate = addPaddingToDate(dateTime);
        String paddeddataFeedDate = addPaddingToDate(dateTime2);
        String differenceInHourMinutes = getDifferenceBetweenDates(dateTime, dateTime2);
        String paddedDifference = String.format("%5s", differenceInHourMinutes);
        //		System.out.println(">> "+dataFeedTime[0]);
        String duplicated = isDuplicated(tarfilesProcessed, tarFileName);
        tarfilesProcessed.add(tarFileName);
        String paddedSize = getIndex(tarfilesProcessed);

        return Flux.just(String.join("~",paddedSize,paddeddataLoadDate,paddedTranCode,paddeddataFeedDate,paddedDifference,tarFileName,duplicated));
    }

    private String getIndex(List<String> tarfilesProcessed) {
        int size = tarfilesProcessed.size();
        return String.format("%4s", size);
    }

    private String isDuplicated(List<String> tarfilesProcessed, String tarFileName) {
        String duplicated = "";
        if(tarfilesProcessed.contains(tarFileName)){
            duplicated = "*";
        }
        return duplicated;
    }

    private String getDifferenceBetweenDates(LocalDateTime dateTime, LocalDateTime dateTime2) {
        int t = (int) ChronoUnit.MINUTES.between(dateTime2, dateTime);
        int hours = t / 60; //since both are ints, you get an int
        int minutes = t % 60;
        return formatHourMinute(hours, minutes);
    }

    private String formatHourMinute(int hours, int minutes) {
        return String.format("%d:%02d", hours, minutes);
    }

    private String addPaddingToDate(LocalDateTime localDateTime){
        return String.format("%-19s", localDateTime);
    }

    private LocalDateTime convertStringToDate(String dataFeedString, String datePatter1) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePatter1);
        return LocalDateTime.parse(dataFeedString, formatter);
    }

    private String getPattern(String line, String pattern) {
        Pattern MY_PATTERN = Pattern.compile(pattern);
        Matcher m = MY_PATTERN.matcher(line);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

}
