package com.example.threads;

import org.springframework.boot.SpringApplication;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;

public class TestThreadsApplication {

    public static void main(String[] args) {
        runWithVirtualThreads(true);
    }

    static void runWithVirtualThreads(boolean virtualThreads) {
        var port = 8080;
        var httpbin = new GenericContainer<>("mccutchen/go-httpbin")
                .withExposedPorts(port)
                .waitingFor(new WaitAllStrategy());
        httpbin.start();

        var threads = Integer.toString(Runtime.getRuntime().availableProcessors());

        System.setProperty("server.tomcat.threads.max", threads);
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", threads);

        System.setProperty("spring.threads.virtual.enabled", Boolean.toString(virtualThreads));

        System.setProperty("httpbin.url", "http://" + httpbin.getHost() + ":" + httpbin.getMappedPort(port));

        SpringApplication.run(ThreadsApplication.class);
    }
}
