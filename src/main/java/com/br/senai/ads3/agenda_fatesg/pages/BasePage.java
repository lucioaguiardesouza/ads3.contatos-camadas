package com.br.senai.ads3.agenda_fatesg.pages;

import com.br.senai.ads3.agenda_fatesg.util.ThemeManager;
import javax.swing.JFrame;
import javax.swing.JComponent;

/**
 * Classe base para todas as janelas (Frames) da aplicação.
 * Encapsula a configuração de tema, centralização e padrões estéticos de 2026.
 */
public abstract class BasePage extends JFrame {

    public BasePage(String title) {
        super(title);
        initializeBase();
    }

    public BasePage() {
        super();
        initializeBase();
    }

    private void initializeBase() {
        // Garante que o tema e fontes globais sejam aplicados
        ThemeManager.setupTheme();
        
        // Padrão de fechamento
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            // Centraliza apenas quando for ficar visível e após o pack()
            setLocationRelativeTo(null);
        }
        super.setVisible(b);
    }

    /**
     * Aplica o estilo de arredondamento padrão do FlatLaf a um componente.
     * @param component O componente a ser estilizado.
     * @param arc O valor do arco (ex: 10, 30).
     */
    protected void applyArc(JComponent component, int arc) {
        component.putClientProperty("FlatLaf.style", "arc: " + arc);
    }
}
