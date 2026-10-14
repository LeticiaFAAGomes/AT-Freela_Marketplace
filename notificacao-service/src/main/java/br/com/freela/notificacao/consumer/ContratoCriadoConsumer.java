package br.com.freela.notificacao.consumer;

import br.com.freela.notificacao.NotificacaoService;
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
    private NotificacaoService notificacaoService;

    @KafkaListener(
            topics = "${app.kafka.topics.contrato-eventos:contrato.eventos}",
            groupId = "${spring.kafka.consumer.group-id:notificacao-service}"
    )
    public void consumir(String mensagem) throws Exception {
        JsonNode evento = objectMapper.readTree(mensagem);

        UUID eventoId = UUID.fromString(evento.path("eventId").asText());
        UUID contratoId = UUID.fromString(evento.path("contratoId").asText());

        UUID clienteId = extrairUuid(evento.path("clienteId"));
        UUID freelancerId = extrairUuid(evento.path("freelancerId"));

        notificacaoService.registrar(
                eventoId, contratoId, clienteId,
                "CONTRATO_CRIADO", "O contrato foi criado."
        );

        notificacaoService.registrar(
                eventoId, contratoId, freelancerId,
                "CONTRATO_CRIADO", "Novo contrato foi criado."
        );

        log.info("kafka.contrato.criado.processado contratoId={}", contratoId);
    }

    private UUID extrairUuid(JsonNode id) {
        if (id.isTextual()) {
            return UUID.fromString(id.asText());
        }

        if (id.hasNonNull("id")) {
            return UUID.fromString(id.get("id").asText());
        }

        throw new IllegalArgumentException("Identificador inválido no evento: " + id);
    }

}
