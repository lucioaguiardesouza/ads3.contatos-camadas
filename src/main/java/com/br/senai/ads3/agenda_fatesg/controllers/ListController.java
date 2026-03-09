package com.br.senai.ads3.agenda_fatesg.controllers;

import java.util.List;

public interface ListController {
    List listAll() throws Exception ;
    boolean markInactiveByName(String name) throws Exception;
    List searchName(String name) throws Exception;
}