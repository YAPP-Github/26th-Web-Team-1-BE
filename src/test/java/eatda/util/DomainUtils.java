package eatda.util;

import eatda.domain.AuditingEntity;
import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

public final class DomainUtils {

    private static final String CREATED_AT_FIELD = "createdAt";

    private DomainUtils() {
    }

    public static <T extends AuditingEntity> void setCreatedAt(T entity, LocalDateTime createdAt) {
        try {
            ReflectionTestUtils.setField(entity, CREATED_AT_FIELD, createdAt);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to set createdAt field", e);
        }
    }
}
