/*
Copyright (c) 2010, FunIcon Team
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list
      of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this
      list of conditions and the following disclaimer in the documentation and/or
      other materials provided with the distribution.
    * Neither the name of Universidade Tecnológica Federal do Paraná nor the names of its
      contributors may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package gui;

import javax.swing.*;

import armazenamento.*;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.border.EmptyBorder;
import java.awt.Color;

/**
 * Componente com a miniatura de um botão (títulos e imagem).
 * @author Felipe Michels Fontoura
 *
 */
@SuppressWarnings("serial")
public class CmpMiniaturaBotao extends JPanel {
    /**
     * Botão relativo a esse display.
     */
    private Botao botao;

    /**
     * Se esse display está selecionado.
     */
    private boolean selecionado = false;

    /**
     * Label deslizante com o título do botão.
     */
    private CmpTextoDeslizavel label_titulo;

    /**
     * Imagem em miniatura com a o ícone.
     */
    private CmpMiniaturaIcone miniatura;

    /**
     * Label deslizante com o título do som.
     */
    private CmpTextoDeslizavel label_som;

    /**
     * Cria uma miniatura de botão com esse botão.
     * @param botao Botão cuja miniatura deve ser criada.
     */
    public CmpMiniaturaBotao(Botao botao) {
        setBorder(new EmptyBorder(8, 16, 8, 16));
        if (botao == null) botao = new Botao();

        this.botao = botao;

        label_titulo = new CmpTextoDeslizavel(botao.obterNomeBotao());
        miniatura = new CmpMiniaturaIcone(botao.obterIcone());
        label_som = new CmpTextoDeslizavel(botao.obterNomeSom());

        label_titulo.setPreferredSize(new Dimension(miniatura.getPreferredSize().width, label_titulo.getPreferredSize().height));
        label_som.setPreferredSize(new Dimension(miniatura.getPreferredSize().width, label_som.getPreferredSize().height));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(label_titulo);
        add(miniatura);
        add(label_som);
    }

    /**
     * Altera o estado da seleção desse componente (seleciona ou desseleciona).
     * @param selecionado Se o componente deve ficar selecionado.
     */
    public void selecionar(boolean selecionado) {
        if (this.selecionado != selecionado) {
            this.selecionado = selecionado;
            label_titulo.definirDeslizando(selecionado ? true : false);
            label_som.definirDeslizando(selecionado ? true : false);
            repaint();
        }
    }

    /**
     * Verifica se os textos deslizáveis estão dentro da tela.
     * @return Se os textos deslizáveis estão dentro da tela.
     */
    public boolean dentro() {
        return label_titulo.dentro() || label_som.dentro();
    }

    /**
     * Desenha os componentes filhos desse componente num determinado contexto gráfico.
     * @param g Contexto gráfico de destino.
     */
    @Override protected void paintChildren(Graphics g) {
        // desenha os componentes filhos.
        super.paintChildren(g);

        // desenha o seletor.
        if (selecionado) {
            g.setColor(new Color(255, 255, 255, 128));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(0, 0, 0, 255));
            g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        }
    }

    /**
     * Desliza os textos deslizáveis, se necessário.
     * @param reiniciar Se deve reiniciar o deslizamento.
     */
    public void deslizar(boolean reiniciar) {
        label_titulo.gatilhoDeslizamento(reiniciar);
        label_som.gatilhoDeslizamento(reiniciar);
    }

    /**
     * Obtém o botão associado a essa miniatura de botão.
     * @return Botão associado a essa miniatura de botão.
     */
    public Botao obterBotao() {
        return botao;
    }

    /**
     * Define o botão associado a essa miniatura de botão.
     * @param botao Botão a associar a essa miniatura de botão.
     * @return Antigo botão associado a essa miniatura de botão.
     */
    public Botao definirBotao(Botao botao) {
        if (botao == null) return this.botao;

        Botao velho = this.botao;
        this.botao = botao;

        miniatura.definirIcone(botao.obterIcone());
        label_titulo.definirTexto(botao.obterNomeBotao());
        label_som.definirTexto(botao.obterNomeSom());

        repaint();

        return velho;
    }

    /**
     * Notifica esse componente da alteração de um parâmetro qualquer.
     */
    public void notificarAlteracao() {
        label_titulo.definirTexto(botao.obterNomeBotao());
        label_som.definirTexto(botao.obterNomeSom());
        miniatura.definirIcone(botao.obterIcone());
        miniatura.notificarAlteracao();
        repaint();
    }

    @Override public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        miniatura.setEnabled(enabled);
    }
}
