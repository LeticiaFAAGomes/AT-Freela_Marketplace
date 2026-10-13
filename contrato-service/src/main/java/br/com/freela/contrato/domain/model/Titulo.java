package br.com.freela.contrato.domain.model;

public record Titulo(String titulo) {
    public Titulo{
        if(titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título é obrigatório.");
        }
    }
}
