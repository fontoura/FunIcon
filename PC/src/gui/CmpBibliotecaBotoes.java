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

import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.Timer;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;

import armazenamento.Botao;

import java.awt.GridLayout;

/**
 * Componente que exibe uma biblioteca de botões selecionáveis, com 4 colunas.
 * @author Felipe Michels Fontoura
 */
@SuppressWarnings("serial")
public class CmpBibliotecaBotoes extends JComponent implements Scrollable {
    /**
     * Lista de componentes de exibição dos botões.
     */
    private List<CmpMiniaturaBotao> cmp_botoes;

    /**
     * Ouvinte de eventos de cursor, comum a todos os componentes de exibição dos botões.
     */
    private MouseListener ouvinte_botoes;

    /**
     * Miniatura de botão selecionada. Se for nula, não há miniatura selecionada.
     */
    private CmpMiniaturaBotao selecionado;

    /**
     * Ouvintes de evento de biblioteca.
     */
    private List<OuvinteConjuntoBotoes> ouvintes_evento;

    /**
     * Constrói um novo componente de biblioteca de botões a partir de um vetor de botões.
     * @param botoes Botões colocar na biblioteca.
     */
    public CmpBibliotecaBotoes(List<Botao> botoes) {
        // define o layout como uma grade com 4 colunas.
        setLayout(new GridLayout(0, 4, 0, 0));

        // define que, a princípio, não já botão selecionado.
        selecionado = null;

        // cria uma lista de com os botões.
        cmp_botoes = new Vector<CmpMiniaturaBotao>((botoes != null) ? botoes.size() : 1, 1);

        // cria o ouvinte de eventos de cursor.
        ouvinte_botoes = new MouseListener() {
            @Override public void mouseClicked(MouseEvent mEvt) {}
            @Override public void mouseEntered(MouseEvent mEvt) {}
            @Override public void mouseExited(MouseEvent mEvt) {}
            @Override public void mousePressed(MouseEvent mEvt) {}
            @Override public void mouseReleased(MouseEvent mEvt) { evtClicouMiniatura((CmpMiniaturaBotao) mEvt.getSource()); };
        };

        // cria a lista de ouvintes de evento de biblioteca.
        ouvintes_evento = new Vector<OuvinteConjuntoBotoes>(1, 1);

        // adiciona os botões à lista.
        if (botoes != null)
            for (Botao botao : botoes)
                if (botao != null)
                    cmp_botoes.add(new CmpMiniaturaBotao(botao));

        // adiciona os ouvintes aos componentes e os componentes à tela.
        for (CmpMiniaturaBotao cmp_miniatura : cmp_botoes) {
            cmp_miniatura.addMouseListener(ouvinte_botoes);
            add(cmp_miniatura);
        }

        // inicia um temporizador do efeito de scroll.
        Timer timer = new Timer(50, new ActionListener() {
            @Override public void actionPerformed(ActionEvent aEvt) { evtTimer(); }
        });
        timer.start();
    }

    /**
     * Adiciona um ouvinte de evento nessa biblioteca de botões.
     * @param ouvinte Ouvinte de evento.
     */
    public void adicionarOuvinteEvento(OuvinteConjuntoBotoes ouvinte) {
        ouvintes_evento.add(ouvinte);
    }

    /**
     * Remove um ouvinte de evento dessa biblioteca de botões.
     * @param ouvinte Ouvinte de evento.
     * @return Se a remoção ocorreu com sucesso.
     */
    public boolean removerOuvinteEvento(OuvinteConjuntoBotoes ouvinte) {
        return ouvintes_evento.remove(ouvinte);
    }

    /**
     * Método que processa o evento do temporizador.
     */
    private void evtTimer() {
        if (selecionado != null) {
            if (selecionado.dentro()) {
                selecionado.deslizar(false);
            } else {
                selecionado.deslizar(true);
            }
        }
    }

    /**
     * Método que processa o evento de uma miniatura ser clicada.
     * @param miniatura Miniatura que foi clicada.
     */
    private void evtClicouMiniatura(CmpMiniaturaBotao miniatura) {
        if (isEnabled() == false) return;

        if (selecionado != null) {
            selecionado.selecionar(false);
            for (OuvinteConjuntoBotoes ouvinte : ouvintes_evento)
                ouvinte.botaoDesselecionado(selecionado.obterBotao());
        }

        if (selecionado != miniatura) {
            miniatura.selecionar(true);
            selecionado = miniatura;
            for (OuvinteConjuntoBotoes ouvinte : ouvintes_evento)
                ouvinte.botaoSelecionado(selecionado.obterBotao());
        } else {
            selecionado = null;
        }
    }

    /**
     * Adiciona um novo botão à biblioteca.
     * @param botao Botão a adicionar.
     */
    public void adicionarBotao(Botao botao) {
        if (botao != null) {
            CmpMiniaturaBotao novo = new CmpMiniaturaBotao(botao);
            novo.addMouseListener(ouvinte_botoes);
            cmp_botoes.add(novo);
            add(novo);
            revalidate();
            repaint();
            for (OuvinteConjuntoBotoes ouvinte : ouvintes_evento)
                ouvinte.botaoAdicionado(botao);
        };
    }

    /**
     * Obtém o botão selecionado.
     * @return Botão selecionado.
     */
    public Botao obterSelecionado() {
        if (selecionado != null) return selecionado.obterBotao();
        else return null;
    }

    /**
     * Remove o botão selecionado.
     */
    public void removerSelecionado() {
        if (selecionado != null) {
            CmpMiniaturaBotao removido = selecionado;
            cmp_botoes.remove(selecionado);
            remove(selecionado);
            selecionado = null;
            revalidate();
            repaint();
            for (OuvinteConjuntoBotoes ouvinte : ouvintes_evento)
                ouvinte.botaoRemovido(removido.obterBotao());
        }
    }

    /**
     * Força a interface a redesenhar o botão selecionado.
     */
    public void redesenharSelecionado() {
        if (selecionado != null)
            selecionado.notificarAlteracao();
    }

    /**
     * {@inheritDoc}
     */
    @Override public void setEnabled(boolean enabled) {
        for (CmpMiniaturaBotao miniatura : cmp_botoes)
            miniatura.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 32;
    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean getScrollableTracksViewportWidth() {
        return getParent().getWidth() > getPreferredSize().width;
    }

    /**
     * {@inheritDoc}
     */
    @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }
}
