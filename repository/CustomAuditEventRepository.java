package com.lixar.apba.repository;

import com.lixar.apba.config.audit.AuditEventConverter;
import com.lixar.apba.domain.PersistentAuditEvent;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Wraps an implementation of Spring Boot's AuditEventRepository.
 */
@Repository
public class CustomAuditEventRepository {

    @Inject
    private PersistenceAuditEventRepository persistenceAuditEventRepository;

    @Bean
    public AuditEventRepository auditEventRepository() {
        return new AuditEventRepository() {

            private static final String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";

            private static final String ANONYMOUS_USER = "anonymousUser";

            @Inject
            private AuditEventConverter auditEventConverter;

            @Override
            public List<AuditEvent> find(String principal, Instant after, String type) {
                Iterable<PersistentAuditEvent> persistentAuditEvents;
                if (principal == null && after == null) {
                    persistentAuditEvents = persistenceAuditEventRepository.findAll();
                } else if (after == null) {
                    persistentAuditEvents = persistenceAuditEventRepository.findByPrincipal(principal);
                } else {
                    persistentAuditEvents =
                        persistenceAuditEventRepository.findByPrincipalAndAuditEventDateAfter(principal, LocalDateTime.from(after));
                }
                return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
            }

            @Override
            @Transactional
            public void add(AuditEvent event) {
                if (!AUTHORIZATION_FAILURE.equals(event.getType()) &&
                    !ANONYMOUS_USER.equals(event.getPrincipal().toString())) {

                    PersistentAuditEvent persistentAuditEvent = new PersistentAuditEvent();
                    persistentAuditEvent.setPrincipal(event.getPrincipal());
                    persistentAuditEvent.setAuditEventType(event.getType());
                    Instant instant = Instant.ofEpochMilli(event.getTimestamp().toEpochMilli());
                    persistentAuditEvent.setAuditEventDate(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
                    persistentAuditEvent.setData(auditEventConverter.convertDataToStrings(event.getData()));
                    persistenceAuditEventRepository.save(persistentAuditEvent);
                }
            }
        };
    }
}
