package com.gomezrondon.reactordemo2;

import com.gomezrondon.reactordemo2.service.LoadFileService;
import com.gomezrondon.reactordemo2.service.TextProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
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


	private final LoadFileService service;
	private final TextProcessorService processor;

	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLACK = "\u001B[30m";

	@Autowired
	public ReactorDemo2Application(LoadFileService service, TextProcessorService processor) {
		this.service = service;
		this.processor = processor;
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactorDemo2Application.class, args);
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
			searchStringFunction(service.readFile(folder));
		}

		System.out.println(ANSI_YELLOW + "********************" + ANSI_YELLOW);
		System.out.println(ANSI_BLACK + "" + ANSI_BLACK);
	}

	private void searchStringFunction(Flux<String> fileFlux ) {

		fileFlux
				.windowWhile(s -> !s.contains("\uF189ip"))
				.flatMap(ventana -> ventana.skip(2))
				.filter(line -> line.length() > 20)
				.map(processor::dataLoadStatFormat)
				.subscribe(System.out::println);
	}

}
