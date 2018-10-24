package com.gomezrondon.reactordemo2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
				fromPath(path).filter(l -> l.contains(str))
						.subscribe(System.out::println);
			});

		}

		System.out.println(ANSI_YELLOW + "********************" + ANSI_YELLOW);
		System.out.println(ANSI_BLACK + "" + ANSI_BLACK);
	}

}
