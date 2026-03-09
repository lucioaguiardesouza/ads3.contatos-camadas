package com.br.senai.ads3.agenda_fatesg.controllers;

import com.br.senai.ads3.agenda_fatesg.domains.Contato;

public interface FormController {
    Contato create(Contato dto) throws Exception;
    Contato update(String originalName, Contato dto) throws Exception;
}