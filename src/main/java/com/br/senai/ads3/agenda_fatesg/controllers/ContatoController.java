package com.br.senai.ads3.agenda_fatesg.controllers;

import com.br.senai.ads3.agenda_fatesg.domains.Contato;
import com.br.senai.ads3.agenda_fatesg.services.ContatoService;
import com.br.senai.ads3.agenda_fatesg.services.IContatoService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ContatoController implements FormController, ListController {
    
    private final IContatoService service;

    public ContatoController() {
       this.service = new ContatoService();
    }    

    ContatoController(Path storage) {
        this.service = new ContatoService(storage);
    }
    
    @Override
    public Contato create(Contato dto) throws Exception {
        if(this.service.inserir(dto)){
            return dto;
        }
        return null;
    }

    @Override
    public Contato update(String originalName, Contato dto) throws Exception {
        if(this.service.alterar(dto)){
            return dto;
        }
        return null;
    }

    @Override
    public List<Contato> listAll() throws Exception  {
        return this.service.buscarTodos();
    }

    @Override
    public boolean markInactiveByName(String name) throws Exception {
        return this.service.excluir(name);
    }

    @Override
    public List<Contato> searchName(String name) throws Exception {
        List<Contato> all = listAll();
        List<Contato> filtered = new ArrayList<>();
        for (Contato c : all) {
            if (c.getNome() != null && c.getNome().toLowerCase().contains(name.toLowerCase())) {
                filtered.add(c);
            }
        }
        return filtered;
    }
}
