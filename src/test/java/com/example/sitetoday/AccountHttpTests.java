package com.example.sitetoday;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    HttpResponse<String> post(String path, String type, String body, String token) throws Exception {
        var request = HttpRequest.newBuilder(uri(path)).header("Content-Type", type)
                .header("X-CSRF-TOKEN", token).POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void loginReadsAnExistingJdbcMember(@Autowired JdbcClient jdbc, @Autowired PasswordEncoder encoder) throws Exception {
        var id = java.util.UUID.randomUUID();
        jdbc.sql("insert into members (id, email, password_hash, role) values (?, ?, ?, ?)")
                .params(id, "existing@example.org", encoder.encode("existing-password"), "EMPLOYER").update();

        var login = post("auth/login", "application/x-www-form-urlencoded",
                "email=existing%40example.org&password=existing-password", get("auth/csrf").body());
        assertThat(login.statusCode()).isEqualTo(204);
        assertThat(get("auth/me").body()).contains(id.toString(), "EMPLOYER");
    }

    @Test
    void signupAndLoginEstablishAnAuthenticatedSession() throws Exception {
        var csrf = get("auth/csrf");
        assertThat(csrf.statusCode()).isEqualTo(200);
        String token = csrf.body();
        String account = """
                {"email":"worker@example.org","password":"long-passphrase","role":"WORKER"}
                """;
        assertThat(post("members", "application/json", account.replace("WORKER", "OFFICE"), token).statusCode()).isEqualTo(403);
        assertThat(post("members", "application/json", account, token).statusCode()).isEqualTo(201);
        assertThat(post("members", "application/json", account, token).statusCode()).isEqualTo(409);
        assertThat(get("auth/me").statusCode()).isEqualTo(401);
        String credentials = "email=worker%40example.org&password=";
        String form = "application/x-www-form-urlencoded";
        assertThat(post("auth/login", form, credentials + "incorrect", token).statusCode()).isEqualTo(401);
        assertThat(post("auth/login", form, credentials + "long-passphrase", token).statusCode()).isEqualTo(204);
        assertThat(get("auth/me").body()).contains("WORKER").doesNotContain("long-passphrase");
    }
}
