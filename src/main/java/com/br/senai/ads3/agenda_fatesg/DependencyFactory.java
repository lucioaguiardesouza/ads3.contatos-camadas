package com.br.senai.ads3.agenda_fatesg;

import com.br.senai.ads3.agenda_fatesg.controllers.ContatoController;
import com.br.senai.ads3.agenda_fatesg.controllers.IContatoCadastroController;
import com.br.senai.ads3.agenda_fatesg.controllers.IContatoListaController;
import com.br.senai.ads3.agenda_fatesg.services.ContatoService;
import com.br.senai.ads3.agenda_fatesg.services.IContatoService;
import com.br.senai.ads3.agenda_fatesg.repositories.ContatoRepository;
import com.br.senai.ads3.agenda_fatesg.repositories.IContatoRepository;
import com.br.senai.ads3.agenda_fatesg.validations.ContatoValidation;
import com.br.senai.ads3.agenda_fatesg.validations.IContatoValidation;

/**
 * Padrão Factory / Service Locator responsável por garantir centralização da
 * Injeção de Dependências (IoC).
 * As views da aplicação devem utilizar esta classe para obter os Controllers
 * sempre bem injetados com seus devidos Services e repositórios.
 */
public class DependencyFactory {

    private static ContatoController contatoControllerSingleton;

    // Construtor privado para evitar instanciação
    private DependencyFactory() {
    }

    /**
     * Resgata o controlador de Contatos que obedece toda a Inversão das sub-camadas
     * (Service, Validation, Repositories).
     * Funciona em formato Singleton para evitar memory leaks caso muitas Views
     * peçam controladores ao mesmo tempo repetidamente.
     * 
     * @return ContatoController Singleton já configurado
     */
    private static ContatoController getContatoControllerInstance() {
        if (contatoControllerSingleton == null) {
            // "Montamos" as pecinhas tal qual um Spring Framework faria nos bastidores
            IContatoRepository repository = new ContatoRepository();
            IContatoValidation validation = new ContatoValidation(repository);
            IContatoService service = new ContatoService(repository, validation);
            contatoControllerSingleton = new ContatoController(service);
        }
        return contatoControllerSingleton;
    }

    /**
     * Prover a interface de Controlador focada apenas para janelas (Views) de
     * Cadastro. (Aplicando ISP do SOLID)
     * 
     * @return IContatoCadastroController devidamente implementado.
     */
    public static IContatoCadastroController getContatoCadastroController() {
        return getContatoControllerInstance();
    }

    /**
     * Prover a interface de Controlador focada apenas para janelas (Views) de
     * Listagens. (Aplicando ISP do SOLID)
     * 
     * @return IContatoListaController devidamente implementado.
     */
    public static IContatoListaController getContatoListaController() {
        return getContatoControllerInstance();
    }

}
