package com.br.senai.ads3.agenda_fatesg.controllers;

import com.br.senai.ads3.agenda_fatesg.domains.Contato;
import com.br.senai.ads3.agenda_fatesg.dtos.ContatoCadastroDTO;

public interface IContatoCadastroController {
    Contato criar(ContatoCadastroDTO dto) throws Exception;
    Contato alterar(String originalName, ContatoCadastroDTO dto) throws Exception;
}