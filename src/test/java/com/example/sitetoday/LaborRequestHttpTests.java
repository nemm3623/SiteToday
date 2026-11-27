package com.example.sitetoday;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url=jdbc:h2:mem:laborrequests")
class LaborRequestHttpTests extends HttpTestSupport {
    @Autowired JdbcClient jdbc;
    @Autowired PasswordEncoder encoder;
    @Autowired ObjectMapper mapper;

    UUID member(String role, String email) {
        var id = UUID.randomUUID();
        jdbc.sql("insert into members (id, email, password_hash, role) values (?, ?, ?, ?)")
                .params(id, email, encoder.encode("test-password"), role).update();
        return id;
    }

    void login(String email) throws Exception {
        assertThat(post("auth/login", "application/x-www-form-urlencoded",
                "email=" + email + "&password=test-password", get("auth/csrf").body()).statusCode()).isEqualTo(204);
    }

    void addOffice(UUID office, UUID account, String name) {
        jdbc.sql("insert into offices (id, member_id, name) values (?, ?, ?)").params(office, account, name).update();
    }

    @Test
    void officeRequiresItsOwnOfficeRoleAccount() {
        var account = member("OFFICE", "dedicated@example.org");
        var office = UUID.randomUUID();
        addOffice(office, account, "전용 계정 검증 사무소");
        try {
            assertThatThrownBy(() -> addOffice(UUID.randomUUID(), account, "중복 계정 사무소"))
                    .isInstanceOf(DataIntegrityViolationException.class);
            var employer = member("EMPLOYER", "wrong-role@example.org");
            assertThatThrownBy(() -> addOffice(UUID.randomUUID(), employer, "잘못된 계정 사무소"))
                    .isInstanceOf(DataIntegrityViolationException.class);
        } finally {
            jdbc.sql("delete from offices where id = ?").param(office).update();
        }
    }

    @Test
    void employerSelectsListedOfficeAndRegistersTerms() throws Exception {
        var employer = member("EMPLOYER", "employer@example.org");
        login("employer@example.org");
        assertThat(get("offices").statusCode()).isEqualTo(200);
        var office = UUID.randomUUID();
        addOffice(UUID.randomUUID(), member("OFFICE", "another@example.org"), "가람인력");
        addOffice(office, member("OFFICE", "office@example.org"), "원주인력");
        var listing = mapper.readTree(get("offices").body());
        assertThat(listing.size()).isEqualTo(2);
        var selected = listing.get(1);
        assertThat(selected.get("name").asString()).isEqualTo("원주인력");
        assertThat(selected.get("officeId").asString()).isEqualTo(office.toString());
        var path = "offices/" + selected.get("officeId").asString() + "/labor-requests";
        var token = get("auth/csrf").body();
        var body = """
                {"startsAt":"2026-12-01T22:00","endsAt":"2026-12-02T06:00","workDescription":"자재 정리",
                 "workerCount":3,"dailyWage":120000}
                """;
        assertError(post(path, "application/json", body, ""), 403, "ACCESS_DENIED");
        assertError(post(path, "application/json", "{}", token), 400, "INVALID_REQUEST");
        assertError(post(path, "application/json", body.replace("12-02", "11-30"), token), 400, "INVALID_REQUEST");
        assertError(post("offices/" + UUID.randomUUID() + "/labor-requests", "application/json", body, token),
                404, "OFFICE_NOT_FOUND");
        var response = post(path, "application/json", body, token);
        assertThat(response.statusCode()).isEqualTo(201);
        var id = UUID.fromString(mapper.readTree(response.body()).get("requestId").asString());
        assertThat(jdbc.sql("""
                select count(*) from labor_requests where id = ? and employer_id = ? and office_id = ?
                  and starts_at = timestamp '2026-12-01 22:00:00' and ends_at = timestamp '2026-12-02 06:00:00'
                  and work_description = '자재 정리' and worker_count = 3 and daily_wage = 120000
                """).params(id, employer, office).query(Integer.class).single()).isOne();
        assertThat(jdbc.sql("select count(*) from labor_requests").query(Integer.class).single()).isOne();
    }

    @Test
    void bothRoutesRequireEmployerSession() throws Exception {
        String path = "offices/" + UUID.randomUUID() + "/labor-requests";
        assertError(get("offices"), 401, "AUTHENTICATION_REQUIRED");
        assertError(post(path, "application/json", "{}", get("auth/csrf").body()), 401, "AUTHENTICATION_REQUIRED");
        for (var role : new String[]{"WORKER", "OFFICE"}) {
            var email = role.toLowerCase() + "-denied@example.org";
            member(role, email);
            login(email);
            assertError(get("offices"), 403, "ACCESS_DENIED");
            assertError(post(path, "application/json", "{}", get("auth/csrf").body()), 403, "ACCESS_DENIED");
        }
    }
}
