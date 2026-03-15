/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */


package com.br.senai.ads3.agenda_fatesg;

import com.br.senai.ads3.agenda_fatesg.pages.Form_Listagem;
import com.br.senai.ads3.agenda_fatesg.util.ThemeManager;
import java.awt.EventQueue;

/**
 *
 * @author CLAYTON.MARQUES
 */
public class Agenda_Fatesg {

    public static void main(String[] args) {
        ThemeManager.setupTheme();
        
        EventQueue.invokeLater(()-> {
            new Form_Listagem().setVisible(true);
        });
        
    }
}
