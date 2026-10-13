package br.com.freela.contrato.infrastructure.web;

import br.com.freela.contrato.application.CriarContratoService;
import br.com.freela.contrato.application.ListarContratoService;
import br.com.freela.contrato.application.CriarContratoCommand;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contratos")
public class ContratoController {
    private static final Logger log = LoggerFactory.getLogger(ContratoController.class);

    @Autowired
    private CriarContratoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratoResponse criar(@RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
                                  @Valid @RequestBody CriarContratoRequest request) {
        log.info("http.contrato.criar correlationId={} clienteId={} freelancerId={} titulo={}", correlationId, request.clienteId(), request.freelancerId(), request.titulo());
        var c = service.criar(new CriarContratoCommand(request.clienteId(), request.freelancerId(), request.titulo(), request.valor()));
        log.info("http.contrato.criar.response correlationId={} contratoId={} status={}", correlationId, c.id(), c.status());
        return ContratoResponse.from(c);
    }

}
