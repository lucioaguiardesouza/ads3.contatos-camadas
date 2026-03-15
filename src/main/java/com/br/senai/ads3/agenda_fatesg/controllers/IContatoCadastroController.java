package com.br.senai.ads3.agenda_fatesg.controllers;

import com.br.senai.ads3.agenda_fatesg.domains.Contato;
import com.br.senai.ads3.agenda_fatesg.dtos.ContatoCadastroDTO;

import com.br.senai.ads3.agenda_fatesg.exceptions.CoreException;

public interface IContatoCadastroController {
    Contato criar(ContatoCadastroDTO dto) throws CoreException;
    Contato alterar(String originalName, ContatoCadastroDTO dto) throws CoreException;
}