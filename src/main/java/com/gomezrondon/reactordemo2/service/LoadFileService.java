package com.gomezrondon.reactordemo2.service;


import reactor.core.publisher.Flux;

import java.io.IOException;

public interface LoadFileService {

    Flux<String> readFile(String file) throws IOException;
}
