package com.br.senai.ads3.agenda_fatesg.controllers;

import com.br.senai.ads3.agenda_fatesg.domains.Contato;
import com.br.senai.ads3.agenda_fatesg.dtos.ContatoCadastroDTO;
import com.br.senai.ads3.agenda_fatesg.services.ContatoService;
import com.br.senai.ads3.agenda_fatesg.services.IContatoService;
import java.nio.file.Path;
import java.util.List;

import com.br.senai.ads3.agenda_fatesg.exceptions.CoreException;

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
    public Contato criar(ContatoCadastroDTO dto) throws CoreException {
        Contato contato = new Contato(dto.nome(), dto.email(), dto.telefone());
        if(this.service.inserir(contato)){
            return contato;
        }
        return null;
    }

    @Override
    public Contato alterar(String originalName, ContatoCadastroDTO dto) throws CoreException {
        Contato contato = new Contato(dto.nome(), dto.email(), dto.telefone());
        if(this.service.alterar(contato)){
            return contato;
        }
        return null;
    }

    @Override
    public List<Contato> listarTodos() throws CoreException  {
        return this.service.buscarTodos();
    }
    @Override
    public List<Contato> listarTodosAtivos() throws CoreException  {
        return this.service.buscarTodosAtivos();
    }
    @Override
    public List<Contato> listaTodosInativos() throws CoreException  {
        return this.service.buscarTodosInativos();
    }

    @Override
    public boolean inativarPorNome(String name) throws CoreException {
        return this.service.excluir(name);
    }

    @Override
    public List<Contato> buscarPorNome(String name) throws CoreException {
        return this.service.listarPorNome(name);
    }
}
