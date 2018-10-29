package com.gomezrondon.reactordemo2.service;

import reactor.core.publisher.Flux;

public interface TextProcessorService {

    Flux<String> dataLoadStatFormat(String line);

}
