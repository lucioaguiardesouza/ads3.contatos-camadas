/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.br.senai.ads3.agenda_fatesg.validations;

import com.br.senai.ads3.agenda_fatesg.domains.Contato;
import com.br.senai.ads3.agenda_fatesg.exceptions.ExceptionValidationCampo;
import com.br.senai.ads3.agenda_fatesg.exceptions.ExceptionValidationRegra;
import com.br.senai.ads3.agenda_fatesg.repositories.IContatoRepository;

import java.util.regex.Pattern;

/**
 *
 * @author Clayton
 */
public class ContatoValidation implements IContatoValidation {

    private final IContatoRepository repository;
    
    // Expressões regulares
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String FONE_REGEX = "^[0-9]{10,11}$";

    public ContatoValidation(final IContatoRepository repository){
        this.repository = repository;
    }
    
    @Override
    public void validaCampo(Contato contato) throws ExceptionValidationCampo {
        if(contato == null) {
            throw new ExceptionValidationCampo("contato", "O objeto de contato não pode ser nulo.", "Erro de Validação", "error_icon");
        }
        if(contato.getNome() == null || contato.getNome().trim().length() < 3){
            throw new ExceptionValidationCampo("nome", "O nome do contato deve possuir pelo menos 3 caracteres.", "Campo Inválido", "error_icon");
        }
        if(contato.getEmail() == null || !Pattern.matches(EMAIL_REGEX, contato.getEmail())){
            throw new ExceptionValidationCampo("email", "O e-mail informado não possui um formato válido.", "Campo Inválido", "error_icon");
        }
        // Supondo que telefone apenas traga numeros do controller (sem máscaras de UI)
        String telefoneNumeros = contato.getTelefone() != null ? contato.getTelefone().replaceAll("[^0-9]", "") : "";
        if(telefoneNumeros.isEmpty() || !Pattern.matches(FONE_REGEX, telefoneNumeros)){
            throw new ExceptionValidationCampo("telefone", "O telefone deve possuir DDD + 8 ou 9 dígitos.", "Campo Inválido", "error_icon");
        }
    }

    @Override
    public void validaRegraInserir(Contato contato) throws ExceptionValidationRegra {
        boolean existe = false;
        try {
            existe = this.repository.contatoExiste(contato);
        } catch (Exception ex) {
            throw new ExceptionValidationRegra("io", "Erro ao verificar base de dados.", "Erro Crítico", "error_icon");
        }
        if(existe){
            throw new ExceptionValidationRegra("inserir_duplicado", "O contato " + contato.getNome() + " já está cadastrado.", "Regra de Negócio", "warning_icon");
        }
    }

    @Override
    public void validaRegraAlterar(Contato contato) throws ExceptionValidationRegra {
        boolean existe = false;
        try {
            existe = this.repository.contatoExiste(contato);
        } catch (Exception ex) {
            throw new ExceptionValidationRegra("io", "Erro ao verificar base de dados.", "Erro Crítico", "error_icon");
        }
        if(!existe){
            throw new ExceptionValidationRegra("alterar_inexistente", "Não é possível alterar o contato " + contato.getNome() + " pois ele não existe na base de dados.", "Registro Não Localizado", "warning_icon");
        }
    }

    @Override
    public void validaRegraAtivar(Contato contato) throws ExceptionValidationRegra {
        if(contato.getNome() == null || contato.getNome().isEmpty()){
            throw new ExceptionValidationRegra("nome", "Nome não informado para ativação.", "Erro de Sistema", "error_icon");
        }
    }

    @Override
    public void validaRegraInativar(Contato contato) throws ExceptionValidationRegra {
         // O método excluir chama este, apenas confere se existe.
         if(contato.getNome() == null || contato.getNome().isEmpty()){
            throw new ExceptionValidationRegra("nome", "Nome não informado para inativação.", "Erro de Sistema", "error_icon");
        }
    }
    
}
