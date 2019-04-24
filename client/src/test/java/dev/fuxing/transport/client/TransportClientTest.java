package dev.fuxing.transport.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by: Fuxing
 * Date: 2019-04-24
 * Time: 22:36
 */
class TransportClientTest {
    TransportClient client;

    @BeforeEach
    void setUp() {
        client = new TransportClient("http://domain") {
        };
    }

    @Test
    void ending() {
        TransportRequest request = client.doGet("/pattern/:what");
        request.path("what", "1");
        assertEquals(request.getUrl(), "http://domain/pattern/1");
    }

    @Test
    void starting() {
        TransportRequest request = client.doGet("/:what/pattern");
        request.path("what", "start");
        assertEquals(request.getUrl(), "http://domain/start/pattern");
    }

    @Test
    void middle() {
        TransportRequest request = client.doGet("/pattern/:middle/what");
        request.path("middle", "hi");
        assertEquals(request.getUrl(), "http://domain/pattern/hi/what");
    }

    @Test
    void query() {
        TransportRequest request = client.doGet("/pattern/:middle/what");
        request.path("middle", "1");
        request.query("next.providerId");
        request.query("with.connection", "");

        System.out.println(request.getUrl());
    }
}