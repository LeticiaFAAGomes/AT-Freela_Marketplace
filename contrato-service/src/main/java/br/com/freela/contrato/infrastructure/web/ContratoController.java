package br.com.freela.contrato.infrastructure.web;

import br.com.freela.contrato.application.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/contratos")
public class ContratoController {
    private static final Logger log = LoggerFactory.getLogger(ContratoController.class);

    @Autowired
    private CriarContratoService service;

    @Autowired
    private CriarEntregaService criarEntregaService;

    @Autowired
    private ConcluirContratoService concluirContratoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratoResponse criar(@RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
                                  @Valid @RequestBody CriarContratoRequest request) {
        log.info("http.contrato.criar correlationId={} clienteId={} freelancerId={} titulo={}", correlationId, request.clienteId(), request.freelancerId(), request.titulo());
        var c = service.criar(new CriarContratoCommand(request.clienteId(), request.freelancerId(), request.titulo(), request.valor()));
        log.info("http.contrato.criar.response correlationId={} contratoId={} status={}", correlationId, c.id(), c.status());
        return ContratoResponse.from(c);
    }

    @PutMapping("/{id}/entrega")
    public ContratoResponse registrarEntrega(@PathVariable("id") UUID id) {
        String correlationId = MDC.get("correlationId");
        log.info("http.contrato.entrega.inicio correlationId={} contratoId={}", correlationId, id);
        var contrato = criarEntregaService.registrar(id);
        log.info("http.contrato.entrega.sucesso correlationId={} contratoId={} status={}",
                correlationId, id, contrato.status());

        return ContratoResponse.from(contrato);
    }

    @PutMapping("/{id}/concluir")
    public ContratoResponse concluir(@PathVariable("id") UUID id) {
        String correlationId = MDC.get("correlationId");
        log.info("http.contrato.concluir.inicio correlationId={} contratoId={}", correlationId, id);
        var contrato = concluirContratoService.concluir(id);
        log.info("http.contrato.concluir.sucesso correlationId={} contratoId={} status={}",
                correlationId, id, contrato.status());

        return ContratoResponse.from(contrato);
    }
}