package com.br.senai.ads3.agenda_fatesg.util;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import javax.swing.UIManager;

/**
 * Utilitário responsável por gerenciar e aplicar o Tema visual global
 * e a tipografia embutida no projeto (.jar).
 */
public class ThemeManager {

    public static void setupTheme() {
        try {
            // 1. Tenta carregar a fonte embarcada dentro do próprio projeto
            // A pasta resources estará dentro do classpath, então o caminho começa com /
            InputStream fontStream = ThemeManager.class.getResourceAsStream("/fonts/helvetica.ttf");

            if (fontStream != null) {
                // 2. Cria a fonte a partir do arquivo .ttf e registra no GraphicsEnvironment do
                // Java
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);

                // 3. Diz ao FlatLaf e ao UIManager global para usar essa fonte em TODOS os
                // componentes
                // (derivando para tamanho 14 que é padrão de leitura confortável)
                UIManager.put("defaultFont", customFont.deriveFont(14f));
                System.out.println("Fonte customizada (Helvetica) carregada com sucesso.");
            } else {
                // Se o desenvolvedor ainda não colocou o .ttf na pasta
                // src/main/resources/fonts/,
                // força a tipografia global buscando localmente no SO (fallback)
                System.err.println(
                        "Aviso: arquivo /fonts/helvetica.ttf não encontrado. Fazendo fallback para fonte do SO.");
                UIManager.put("defaultFont", new Font("Helvetica", Font.PLAIN, 14));
            }
        } catch (Exception ex) {
            System.err.println("Erro ao inicializar e embarcar Custom Font: " + ex.getMessage());
            // Se der algum erro (ex: fonte inválida), aplica a fonte de fallback padrão
            UIManager.put("defaultFont", new Font("Helvetica", Font.PLAIN, 14));
        }

        // 4. Depois de definir o UIManager.defaultFont, inicializamos o FlatLaf.
        // O FlatLaf aplicará essa fonte automaticamente a todos os painéis, botões,
        // labels, etc.
        FlatDarkLaf.setup();

        // 5. Aplicar a nova paleta de cores moderna sobrepondo os valores do DarkLaf
        UIManager.put("control", new java.awt.Color(17, 17, 17)); // #111111 Global sólido
        UIManager.put("info", new java.awt.Color(17, 17, 17));

        // Aplica leve transparência ao fundo dos painéis e tabelas para dar aspecto
        // sobreposto (Alpha 200/255)
        UIManager.put("Panel.background", new java.awt.Color(29, 29, 29, 200));
        UIManager.put("Table.background", new java.awt.Color(29, 29, 29, 200));

        // Gradiente suave para o botão (do escuro para um mais vivo)
        UIManager.put("Button.startBackground", new java.awt.Color(196, 62, 11)); // Escuro (#C43E0B)
        UIManager.put("Button.endBackground", new java.awt.Color(233, 84, 32)); // Claro (#E95420)

        UIManager.put("Button.hoverBackground", new java.awt.Color(242, 113, 71)); // Warm Orange sólido ou gradiente
        // Você também pode aplicar gradientes de hover se desejar, mas setar o
        // hoverBackground já dá um destaque agradável

        UIManager.put("Button.foreground", new java.awt.Color(255, 255, 255)); // Text Primary para Botões
        UIManager.put("Label.foreground", new java.awt.Color(255, 255, 255)); // Text Primary
        UIManager.put("TextField.foreground", new java.awt.Color(168, 168, 168)); // Text Secondary #A8A8A8
        UIManager.put("Table.foreground", new java.awt.Color(255, 255, 255));
    }
}
