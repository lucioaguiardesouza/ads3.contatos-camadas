/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.br.senai.ads3.agenda_fatesg.repositories;

import com.br.senai.ads3.agenda_fatesg.domains.Contato;
import java.util.List;

/**
 *
 * @author CLAYTON.MARQUES
 */
public interface IContatoRepository {
    boolean inserir(final Contato contato) throws Exception;
    boolean alterar(final Contato contato) throws Exception;
    boolean desativar(final Contato contato) throws Exception;
    boolean reativar(final Contato contato) throws Exception;
    boolean contatoExiste(final Contato contato) throws Exception;
    List<Contato> buscarTodos() throws Exception;
}
