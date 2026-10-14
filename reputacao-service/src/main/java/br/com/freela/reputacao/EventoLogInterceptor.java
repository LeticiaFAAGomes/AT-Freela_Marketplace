package br.com.freela.reputacao;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class EventoLogInterceptor implements RecordInterceptor<Object, Object> {
    private static final Logger log = LoggerFactory.getLogger(EventoLogInterceptor.class);

    private static String campo(String json, String nome) {
        Matcher m = Pattern.compile("\"" + nome + "\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        return m.find() ? m.group(1) : "n/a";
    }

    @Override
    public ConsumerRecord<Object, Object> intercept(ConsumerRecord<Object, Object> r, Consumer<Object, Object> c) {
        String json = String.valueOf(r.value());
        MDC.put("eventId", campo(json, "eventId"));
        MDC.put("correlationId", campo(json, "correlationId"));
        MDC.put("contratoId", String.valueOf(r.key()));
        log.info("kafka.evento.recebido tipo={} topico={} particao={} offset={}",
                campo(json, "eventType"), r.topic(), r.partition(), r.offset());
        return r;
    }

    @Override
    public void success(ConsumerRecord<Object, Object> r, Consumer<Object, Object> c) {
        log.info("kafka.evento.processado resultado=OK");
    }

    @Override
    public void failure(ConsumerRecord<Object, Object> r, Exception ex, Consumer<Object, Object> c) {
        log.error("kafka.evento.falha resultado=ERRO motivo={}", ex.getMessage());
    }

    @Override
    public void afterRecord(ConsumerRecord<Object, Object> r, Consumer<Object, Object> c) {
        MDC.clear();
    }
}
