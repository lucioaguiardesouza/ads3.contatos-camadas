/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.br.senai.ads3.agenda_fatesg.repositories;

import com.br.senai.ads3.agenda_fatesg.domains.Contato;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author CLAYTON.MARQUES
 */
public class ContatoRepository implements IContatoRepository {
    
    private final Path storagePath;

    public ContatoRepository() {
        this.storagePath = Paths.get("agenda.txt");
    }  

    public ContatoRepository(Path storage) {
        this.storagePath = storage;
    }
    

    @Override
    public boolean inserir(Contato contato) {
        String linha = this.toCsvLine(contato, "ativo");
        return insereRegistro(linha);
    }

    @Override
    public boolean alterar(Contato contato) {
        String linha = this.toCsvLine(contato, "ativo");
        return alteraRegistro(linha);
    }

    @Override
    public boolean desativar(Contato contato) {
        String linha = this.toCsvLine(contato, "inativo");
        return alteraRegistro(linha);
    }

    @Override
    public boolean reativar(Contato contato) {
        String linha = this.toCsvLine(contato, "ativo");
        return alteraRegistro(linha);
    }

    @Override
    public boolean contatoExiste(Contato contato) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Contato> buscarTodos() {
        List<String> linhas = linhasAtivas();
        if (linhas == null || linhas.isEmpty()) {
            return List.of(); 
        }
        return linhas.stream()
                 .map(linha -> toObject(linha))
                 .toList();

    }   
    
    // Manipulação do arquivo

    private String toCsvLine(final Contato c, final String status) {
        String nome = c.getNome() == null ? "" : c.getNome();
        String email = c.getEmail() == null ? "" : c.getEmail();
        String tel = c.getTelefone() == null ? "" : c.getTelefone();
        return nome.concat(";").concat(email).concat(";").concat(tel).concat(";").concat(status).concat(System.lineSeparator());
    }
    
    private Contato toObject(String linha) {
        String[] d = linha.split(";");
        String nome = d.length > 0 ? d[0] : "";
        String email = d.length > 1 ? d[1] : "";
        String tel = d.length > 2 ? d[2] : "";
        return new Contato(nome, email, tel);
    }
    
    private void ensureStorage() throws IOException {
        if (!Files.exists(storagePath)) {
            Files.createFile(storagePath);
        }
    }
    
    private boolean insereRegistro(final String registro) {
        try {
            ensureStorage();
            Files.write(storagePath, Collections.singleton(registro), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException ex) {
            System.getLogger(ContatoRepository.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);            
        }
        return false;
    }
    
    private boolean alteraRegistro(final String registro) {
        try {
            ensureStorage();
            List<String> linhas = Files.readAllLines(storagePath, StandardCharsets.UTF_8);
            Files.write(storagePath, linhas, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            return true;
        } catch (IOException ex) {
            System.getLogger(ContatoRepository.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);            
        }
        return false;
    }
    
    private List<String> linhasAtivas(){
        try {
            ensureStorage();
            List<String> lines = Files.readAllLines(storagePath, StandardCharsets.UTF_8);
            List<String> result = new ArrayList<>();
            for (String l : lines) {
                String[] d = l.split(";");
                String status = d.length > 3 ? d[3] : "ativo";
                if ("ativo".equalsIgnoreCase(status)) {                  
                    result.add(l);
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }    
    }
}
