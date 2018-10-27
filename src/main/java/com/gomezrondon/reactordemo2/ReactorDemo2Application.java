package com.gomezrondon.reactordemo2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class ReactorDemo2Application implements CommandLineRunner {

	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLACK = "\u001B[30m";

	public static void main(String[] args) {
		SpringApplication.run(ReactorDemo2Application.class, args);
	}


	private static Flux<String> fromPath(Path path) {
		return Flux.using(() -> Files.lines(path),
				Flux::fromStream,
				BaseStream::close
		);
	}


	private static List<Path> readFiles(String folder) throws IOException {

		try (Stream<Path> paths = Files.walk(Paths.get(folder))) {
			return paths
					.filter(Files::isRegularFile)
					//.peek(System.out::println)
					.collect(Collectors.toList());
		}
	}

    // how to build: gradle clean build -x test copyFile
	// how to run: java -jar reactor-demo-2.jar <folder> <String to search>
	// menu: ./option.sh 7 <folder> <String to search>
	@Override
	public void run(String... args) throws Exception {
		System.out.println(ANSI_YELLOW + "********************" + ANSI_YELLOW);
		String str = args[1];
		String folder = args[0];

		if(str != null){
			readFiles(folder).forEach(path ->{
				System.out.println(path.toFile().getName());
				searchStringFunction(str, path);
			});

		}

		System.out.println(ANSI_YELLOW + "********************" + ANSI_YELLOW);
		System.out.println(ANSI_BLACK + "" + ANSI_BLACK);
	}

	private void searchStringFunction(String str, Path path) {

		List<String> tarfilesProcessed = new ArrayList<>();

		fromPath(path)
				.windowWhile(s -> !s.contains("\uF189ip"))
				.flatMap(ventana ->
						ventana.skip(2))
				.filter(line -> line.length() > 20)
				.map(line ->{
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

					return String.join("~",paddedSize,paddeddataLoadDate,paddedTranCode,paddeddataFeedDate,paddedDifference,tarFileName,duplicated);
						}
				)
				.subscribe(System.out::println);
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
