package com.example.sitetoday;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url=jdbc:h2:mem:accounttest")
class AccountHttpTests {
    @LocalServerPort int port;
    final HttpClient client = HttpClient.newBuilder().cookieHandler(new CookieManager()).build();

    URI uri(String path) {
        return URI.create("http://localhost:" + port + "/api/" + path);
    }

    HttpResponse<String> get(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(uri(path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    int post(String path, String type, String body, String token) throws Exception {
        var request = HttpRequest.newBuilder(uri(path)).header("Content-Type", type)
                .header("X-CSRF-TOKEN", token).POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
    }

    @Test
    void signupAndLoginEstablishAnAuthenticatedSession() throws Exception {
        var csrf = get("auth/csrf");
        assertThat(csrf.statusCode()).isEqualTo(200);
        String token = csrf.body();
        String account = """
                {"email":"worker@example.org","password":"long-passphrase","role":"WORKER"}
                """;
        assertThat(post("members", "application/json", account.replace("WORKER", "OFFICE"), token)).isEqualTo(403);
        assertThat(post("members", "application/json", account, token)).isEqualTo(201);
        assertThat(post("members", "application/json", account, token)).isEqualTo(409);
        assertThat(get("auth/me").statusCode()).isEqualTo(401);
        String credentials = "email=worker%40example.org&password=";
        String form = "application/x-www-form-urlencoded";
        assertThat(post("auth/login", form, credentials + "incorrect", token)).isEqualTo(401);
        assertThat(post("auth/login", form, credentials + "long-passphrase", token)).isEqualTo(204);
        assertThat(get("auth/me").body()).contains("WORKER").doesNotContain("long-passphrase");
    }
}
