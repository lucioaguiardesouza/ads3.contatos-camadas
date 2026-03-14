package com.br.senai.ads3.agenda_fatesg.controllers;

import com.br.senai.ads3.agenda_fatesg.domains.Contato;
import com.br.senai.ads3.agenda_fatesg.dtos.ContatoCadastroDTO;
import com.br.senai.ads3.agenda_fatesg.services.ContatoService;
import com.br.senai.ads3.agenda_fatesg.services.IContatoService;
import java.nio.file.Path;
import java.util.List;

public class ContatoController implements IContatoCadastroController, IContatoListaController {
    
    private final IContatoService service;

    public ContatoController() {
       this.service = new ContatoService();
    }    

    public ContatoController(Path storage) {
        this.service = new ContatoService(storage);
    }
    
    // Injeção de dependência explícita (D de SOLID)
    public ContatoController(IContatoService service) {
        this.service = service;
    }
    
    @Override
    public Contato criar(ContatoCadastroDTO dto) throws Exception {
        Contato contato = new Contato(dto.nome(), dto.email(), dto.telefone());
        if(this.service.inserir(contato)){
            return contato;
        }
        return null;
    }

    @Override
    public Contato alterar(String originalName, ContatoCadastroDTO dto) throws Exception {
        Contato contato = new Contato(dto.nome(), dto.email(), dto.telefone());
        if(this.service.alterar(contato)){
            return contato;
        }
        return null;
    }

    @Override
    public List<Contato> listarTodos() throws Exception  {
        return this.service.buscarTodos();
    }
    @Override
    public List<Contato> listarTodosAtivos() throws Exception  {
        return this.service.buscarTodosAtivos();
    }
    @Override
    public List<Contato> listaTodosInativos() throws Exception  {
        return this.service.buscarTodosInativos();
    }

    @Override
    public boolean inativarPorNome(String name) throws Exception {
        return this.service.excluir(name);
    }

    @Override
    public List<Contato> buscarPorNome(String name) throws Exception {
        return this.service.listarPorNome(name);
    }
}
