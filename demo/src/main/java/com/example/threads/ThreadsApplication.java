package com.example.threads;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
public class ThreadsApplication {

    @Bean
    RestClient restClient(RestClient.Builder builder,
                          @Value("${httpbin.url}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> httpEndpoints(RestClient restClient) {
        var log = LoggerFactory.getLogger(getClass());
        return route()
                .GET("/{seconds}", request -> {
                    var seconds = request.pathVariable("seconds");
                    var requestToHttpBin = restClient
                            .get()
                            .uri("/delay/" + seconds)
                            .retrieve()
                            .toEntity(String.class);
                    log.info("{} on {}", requestToHttpBin.getStatusCode(),
                            Thread.currentThread());

                    return ServerResponse.ok().body(Map.of("done", true));
                })
                .build();
    }


    //    @Bean
    ApplicationRunner threads1Demo() {
        return args -> {

            //
            // José Paumard
            // Oracle
            //
            var executorService = Executors.newVirtualThreadPerTaskExecutor();
            var threads = new ArrayList<Thread>();
            var names = new ConcurrentSkipListSet<String>();
            for (var i = 0; i < 1_000; i++) {

                var first = i == 0;

                var thread = Thread.ofVirtual().unstarted(() -> {
                    try {


                        if (first) names.add(Thread.currentThread().toString());
                        Thread.sleep(1_00);

                        if (first) names.add(Thread.currentThread().toString());
                        Thread.sleep(1_00);

                        if (first) names.add(Thread.currentThread().toString());
                        Thread.sleep(1_00);

                        if (first) names.add(Thread.currentThread().toString());
                        Thread.sleep(1_00);


                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                threads.add(thread);

            }

            for (var t : threads) t.start();

            for (var t : threads) t.join();

            System.out.println(names);

        };
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ThreadsApplication.class, args);
    }
}
