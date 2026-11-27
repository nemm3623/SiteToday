package com.example.sitetoday;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

abstract class HttpTestSupport {
    @LocalServerPort int port;
    final HttpClient client = HttpClient.newBuilder().cookieHandler(new CookieManager()).build();

    URI uri(String path) {
        return URI.create("http://localhost:" + port + "/api/" + path);
    }

    HttpResponse<String> get(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(uri(path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> post(String path, String type, String body, String token) throws Exception {
        var request = HttpRequest.newBuilder(uri(path)).header("Content-Type", type)
                .header("X-CSRF-TOKEN", token).POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    void assertError(HttpResponse<String> response, int status, String code) {
        assertThat(response.statusCode()).isEqualTo(status);
        assertThat(response.headers().firstValue("Content-Type").orElse("")).startsWith("application/json");
        assertThat(response.body()).contains("\"code\":\"" + code + "\"", "\"message\":");
    }

}
