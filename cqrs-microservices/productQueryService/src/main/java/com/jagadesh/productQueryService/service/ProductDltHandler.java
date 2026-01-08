package com.jagadesh.productQueryService.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductDltHandler {

    @KafkaListener( topics = "product-event.dlt", groupId = "product-dlt-group")
    public void consumeDlt(
            Object payload,
            @Headers Map<String, Object> headers
    ) {

        System.err.println("🚨 DLT EVENT RECEIVED 🚨");
        System.err.println("Payload: " + payload);

        System.err.println("---- Headers ----");
        headers.forEach((k, v) ->
                System.err.println(k + " = " + v)
        );
    }
}
