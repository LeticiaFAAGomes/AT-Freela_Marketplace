package br.com.freela.contrato.domain.model;

import java.util.Objects;
import java.util.UUID;

public record FreelancerId(UUID id) {

    public FreelancerId {
        Objects.requireNonNull(id, "O ID do freelancer do contrato não pode ser nulo.");
    }

    public static FreelancerId novo() {
        return new FreelancerId(UUID.randomUUID());
    }

    public static FreelancerId de(UUID id) {
        return new FreelancerId(id);
    }
}
