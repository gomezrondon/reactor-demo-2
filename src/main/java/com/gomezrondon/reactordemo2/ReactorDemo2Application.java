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
	// how to run: java -jar reactor-demo-2.jar <folder>  // C:\tmp\comparacion\data-log.txt
	// menu: ./option.sh 7 <folder> <String to search>
	@Override
	public void run(String... args) throws Exception {
		System.out.println(ANSI_YELLOW + "********************" + ANSI_YELLOW);
		String folder = args[0];

		if(folder != null){
			searchStringFunction(service.readFile(folder));
		}

		System.out.println(ANSI_YELLOW + "********************" + ANSI_YELLOW);
		System.out.println(ANSI_BLACK + "" + ANSI_BLACK);
	}

	private void searchStringFunction(Flux<String> fileFlux ) {

		fileFlux
				.windowWhile(s -> !s.contains("\uF189ip"))
				.flatMap(ventana -> ventana.skip(2))
				.filter(line -> line.length() > 100)
				.flatMap(processor::dataLoadStatFormat)
				.subscribe(System.out::println);

		/* Output example
		   1~2018-11-08T00:03:46~ 9411399~2018-11-07T16:15:04~ 7:48~pw-dataload-20181107161504-uat-3.7.tar~
		   2~2018-11-08T00:08:19~ 9684080~2018-11-07T16:15:04~ 7:53~pw-dataload-20181107161504-uat-3.7.tar~*
		   3~2018-11-08T00:12:54~ 9958828~2018-11-07T17:49:45~ 6:23~pw-dataload-20181107174945-uat-3.7.tar~
		   4~2018-11-08T00:17:05~10209506~2018-11-07T17:29:45~ 6:47~pw-dataload-20181107172945-uat-3.7.tar~
		*/

	}

}
