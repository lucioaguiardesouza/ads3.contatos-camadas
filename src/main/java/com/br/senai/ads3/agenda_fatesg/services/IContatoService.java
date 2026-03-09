/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.br.senai.ads3.agenda_fatesg.services;

import com.br.senai.ads3.agenda_fatesg.domains.Contato;
import java.util.List;

/**
 *
 * @author CLAYTON.MARQUES
 */
public interface IContatoService{
    boolean inserir(final Contato contato) throws Exception;
    boolean alterar(final Contato contato) throws Exception;
    boolean excluir(final Contato contato) throws Exception;
    boolean excluir(final String nome) throws Exception;
    List<Contato> buscarTodos() throws Exception;
    boolean contatoExiste(final Contato contato) throws Exception;
    boolean reativaContato(final Contato contato) throws Exception;
    Contato findByName(String name) throws Exception;
    List<Contato> searchName(String name) throws Exception;
}
