package com.kmakrutin.producer.service;

public interface Sender {
    String PROPERTY_CODE_HEADER = "propertyCode";

    void send(Integer num, String propertyCode);
}
