package com.br.senai.ads3.agenda_fatesg.dtos;

/**
 * Data Transfer Object (DTO) imutável para a captura de formulários de Cadastro e Edição.
 * Utilizado para isolar e transitar os dados vindos do View para Controller de forma controlada.
 * Utiliza records do Java 14+ para garantir código limpo e imutável.
 * 
 * @author Antigravity
 */
public record ContatoCadastroDTO(
    String nome,
    String telefone,
    String email
) {
}
