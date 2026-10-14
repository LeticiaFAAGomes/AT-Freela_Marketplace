package br.com.freela.auditoria.consumer;

import br.com.freela.auditoria.AuditoriaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ContratoCriadoConsumer {
 private static final Logger log =
            LoggerFactory.getLogger(ContratoCriadoConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuditoriaService auditoriaService;

    @KafkaListener(
            topics = "${topics.contrato-eventos:contrato.eventos}",
            groupId = "auditoria-service"
    )
    public void consumir(String mensagem) throws Exception {
        JsonNode evento = objectMapper.readTree(mensagem);

        UUID eventId = UUID.fromString(evento.path("eventId").asText());
        UUID contratoId = UUID.fromString(evento.path("contratoId").asText());

        String eventType = evento.path("eventType").asText("ContratoCriado");
        String correlationId = evento.path("correlationId").asText(null);

        log.info(
                "kafka.auditoria.evento.recebido eventId={} contratoId={}",
                eventId, contratoId
        );

        auditoriaService.registrar(
                eventId,
                contratoId,
                eventType,
                correlationId,
                mensagem
        );
    }
}
