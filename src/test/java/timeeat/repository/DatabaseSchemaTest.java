package timeeat.repository;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

class DatabaseSchemaTest {

    // TODO : Flyway SQL 파일 관리 방법 및 Flyway 테스트 방법 논의
    @Nested
    @SpringBootTest(webEnvironment = WebEnvironment.NONE)
    @ActiveProfiles({"test", "flyway"})
    class ProductionDatabaseSchemaTest {

        @Autowired
        private Flyway flyway;

        @Test
        void 운영_데이터베이스_스키마가_정상적으로_동작한다() {
            assertThatCode(() -> flyway.migrate()).doesNotThrowAnyException();
        }
    }
}
