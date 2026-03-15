/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.br.senai.ads3.agenda_fatesg.services;

import com.br.senai.ads3.agenda_fatesg.domains.Contato;
import java.util.List;
import com.br.senai.ads3.agenda_fatesg.exceptions.CoreException;

/**
 *
 * @author CLAYTON.MARQUES
 */
public interface IContatoService{
    boolean inserir(final Contato contato) throws CoreException;
    boolean alterar(final Contato contato) throws CoreException;
    boolean excluir(final Contato contato) throws CoreException;
    boolean excluir(final String nome) throws CoreException;
    List<Contato> buscarTodos() throws CoreException;
    List<Contato> buscarTodosAtivos() throws CoreException;
    List<Contato> buscarTodosInativos() throws CoreException;
    boolean contatoExiste(final Contato contato) throws CoreException;
    boolean reativaContato(final Contato contato) throws CoreException;
    Contato buscarPorNome(String name) throws CoreException;
    List<Contato> listarPorNome(String name) throws CoreException;
}
