package br.com.freela.contrato.infrastructure.persistence.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public OutboxPublisher(
            OutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${topics.contrato-eventos:contrato.eventos}") String topic
    ) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Scheduled(fixedDelayString = "${outbox.intervalo-ms:1000}")
    @Transactional
    public void publicarPendentes() {
        var pendentes =
                outboxRepository.findTop100ByPublishedAtIsNullOrderByOccurredAtAsc();

        for (OutboxEvent evento : pendentes) {
            try {
                kafkaTemplate.send(
                        topic,
                        evento.getAggregateId().toString(),
                        evento.getPayload()
                ).get(10, TimeUnit.SECONDS);

                evento.marcarComoPublicado();

                log.info(
                        "outbox.evento.publicado contratoId={} eventId={} eventType={}",
                        evento.getAggregateId(),
                        evento.getEventId(),
                        evento.getEventType()
                );

            } catch (Exception e) {
                log.error(
                        "outbox.evento.falha contratoId={} eventId={} eventType={}",
                        evento.getAggregateId(),
                        evento.getEventId(),
                        evento.getEventType(),
                        e
                );
                break;
            }
        }
    }
}
