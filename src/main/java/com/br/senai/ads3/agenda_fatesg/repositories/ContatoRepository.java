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
import com.br.senai.ads3.agenda_fatesg.exceptions.CoreException;

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
    public boolean inserir(Contato contato) throws CoreException {
        String linha = this.toCsvLine(contato, "ativo");
        return insereRegistro(linha);
    }

    @Override
    public boolean alterar(Contato contato) throws CoreException {
        String linha = this.toCsvLine(contato, "ativo");
        String linhaAntiga = buscaRegistro(contato.getNome());
        return alteraRegistro(linha, linhaAntiga);
    }

    @Override
    public boolean desativar(Contato contato) throws CoreException {
        String linha = this.toCsvLine(contato, "inativo");
        String linhaAntiga = buscaRegistro(contato.getNome());
        return alteraRegistro(linha, linhaAntiga);
    }

    @Override
    public boolean reativar(Contato contato) throws CoreException {
        String linha = this.toCsvLine(contato, "ativo");
        String linhaAntiga = buscaRegistro(contato.getNome());
        return alteraRegistro(linha, linhaAntiga);
    }

    @Override
    public boolean contatoExiste(Contato contato) throws CoreException {
    	List<Contato> contatos = buscarTodos();
		for (Contato c : contatos) {
			if (c.getNome().equalsIgnoreCase(contato.getNome())) {
				return true;
			}
		}
		return false;
    }

    @Override
    public List<Contato> buscarTodos() throws CoreException {
        List<String> linhas = linhasAtivas(true, true);
        if (linhas == null || linhas.isEmpty()) {
            return List.of(); 
        }
        return linhas.stream()
                 .map(linha -> toObject(linha))
                 .toList();

    }
    
    @Override
    public List<Contato> buscarTodos(boolean ativos) throws CoreException {
        List<String> linhas = linhasAtivas(ativos, false);
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
        return nome.concat(";").concat(email).concat(";").concat(tel).concat(";").concat(status);
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
    
    private boolean alteraRegistro(final String registro, String linhaAntiga) {
        try {
            ensureStorage();
            List<String> linhas = Files.readAllLines(storagePath, StandardCharsets.UTF_8);
            boolean alterou = false;
            for (int i = 0; i < linhas.size(); i++) {
                if (linhas.get(i).equals(linhaAntiga)) {
                    linhas.set(i, registro);
                    alterou = true;
                    break; 
                }
            }
            if (alterou) {
               Files.write(storagePath, linhas, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
               return true;
            }
        } catch (IOException ex) {
            System.getLogger(ContatoRepository.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);            
        }
        return false;
    }
    
    private List<String> linhasAtivas(final boolean ativos, final boolean todos){
        try {
            ensureStorage();
            try (java.util.stream.Stream<String> lines = Files.lines(storagePath, StandardCharsets.UTF_8)) {
                return lines
                        .filter(l -> !l.isBlank())
                        .filter(l -> {
                            String[] d = l.split(";");
                            String status = d.length > 3 ? d[3] : "ativo";
                            return ("ativo".equalsIgnoreCase(status) == ativos) || todos;
                        }).toList();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }    
    }

    private String buscaRegistro(String nome) {
        String result = "";
        List<String> linhas = linhasAtivas(true, true);
        if(linhas != null && !linhas.isEmpty()){
            for (String ln : linhas) {
                String vNome =ln.split(";")[0];
                if(vNome.equalsIgnoreCase(nome)){
                    result = ln;
                    break;
                }
            }
        }
        return result;
    }
}
