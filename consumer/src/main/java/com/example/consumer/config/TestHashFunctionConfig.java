package com.example.consumer.config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("test-hash")
public class TestHashFunctionConfig implements CommandLineRunner {
    public static final String EXCHANGE = "e2";
    private static final String EXCHANGE_TYPE = "x-consistent-hash";
    private final ConnectionFactory connectionFactory;

    public TestHashFunctionConfig(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void run(String... asd) throws Exception {

        try (Connection conn = connectionFactory.createConnection();
             Channel ch = conn.createChannel(false)) {
            for (String q : Arrays.asList("q1", "q2", "q3", "q4")) {
                ch.queueDeclare(q, true, false, true, null);
                ch.queuePurge(q);
            }

            Map<String, Object> args = new HashMap<>();
            args.put("hash-header", "hash-on");
            ch.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE, true, false, args);

            for (String q : Arrays.asList("q1", "q2")) {
                ch.queueBind(q, EXCHANGE, "1");
            }

            for (String q : Arrays.asList("q3", "q4")) {
                ch.queueBind(q, EXCHANGE, "2");
            }

            ch.confirmSelect();


            for (int i = 0; i < 100000; i++) {
                AMQP.BasicProperties.Builder bldr = new AMQP.BasicProperties.Builder();
                Map<String, Object> hdrs = new HashMap<>();
                hdrs.put("hash-on", String.valueOf(i));
                ch.basicPublish(EXCHANGE, "", bldr.headers(hdrs).build(), "".getBytes(StandardCharsets.UTF_8));
            }

            ch.waitForConfirmsOrDie(10000);

            System.out.println("Done publishing!");
            System.out.println("Evaluating results...");
            // wait for one stats emission interval so that queue counters
            // are up-to-date in the management UI
            Thread.sleep(5);

            System.out.println("Done.");
        }
    }
}
