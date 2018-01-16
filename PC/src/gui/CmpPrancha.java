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

import armazenamento.Botao;

import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.Timer;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.List;
import java.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Componente que exibe uma biblioteca de botões selecionáveis, com 4 colunas.
 * @author Felipe Michels Fontoura
 */
@SuppressWarnings("serial")
public class CmpPrancha extends JComponent implements Scrollable {
    /**
     * Componentes de exibição dos botões.
     */
    private CmpMiniaturaBotao[] cmp_botoes;

    /**
     * Botões colocados na tela.
     */
    private int contagem_botoes;

    /**
     * Ouvinte de eventos de cursor, comum a todos os componentes de exibição dos botões.
     */
    private MouseListener ouvinte_botoes;

    /**
     * Miniatura de botão selecionada. Se for nula, não há miniatura selecionada.
     */
    private int selecionado;

    /**
     * Ouvintes de evento de biblioteca.
     */
    private List<OuvinteConjuntoBotoes> ouvintes_evento;

    /**
     * Constrói um novo componente de biblioteca de botões a partir de um vetor de botões.
     */
    public CmpPrancha() {
        // define o layout como uma grade com 4 colunas.
        setLayout(new GridLayout(0, 4, 0, 0));

        // define que, a princípio, não já botão selecionado.
        selecionado = 4;

        // cria o ouvinte de eventos de cursor.
        ouvinte_botoes = new MouseListener() {
            @Override public void mouseClicked(MouseEvent mEvt) {}
            @Override public void mouseEntered(MouseEvent mEvt) {}
            @Override public void mouseExited(MouseEvent mEvt) {}
            @Override public void mousePressed(MouseEvent mEvt) {}
            @Override public void mouseReleased(MouseEvent mEvt) { evtClicouMiniatura((CmpMiniaturaBotao) mEvt.getSource()); };
        };

        // cria uma lista de com os botões.
        contagem_botoes = 0;
        cmp_botoes = new CmpMiniaturaBotao[4];
        for (int i = 0; i < 4; i ++) {
            cmp_botoes[i] = new CmpMiniaturaBotao(null);
            add(cmp_botoes[i]);
            cmp_botoes[i].setVisible(false);
            cmp_botoes[i].setEnabled(false);
            cmp_botoes[i].addMouseListener(ouvinte_botoes);
        }

        // cria a lista de ouvintes de evento de biblioteca.
        ouvintes_evento = new Vector<OuvinteConjuntoBotoes>(1, 1);

        // inicia um temporizador do efeito de scroll.
        Timer timer = new Timer(50, new ActionListener() {
            @Override public void actionPerformed(ActionEvent aEvt) { evtTimer(); }
        });
        timer.start();
    }

    /**
     * Obtém o total de botões colocados nessa prancha.
     * @return Total de botões colocados nessa prancha.
     */
    public int obterContagem() {
        return contagem_botoes;
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
        if (selecionado != 4) {
            if (cmp_botoes[selecionado].dentro()) {
                cmp_botoes[selecionado].deslizar(false);
            } else {
                cmp_botoes[selecionado].deslizar(true);
            }
        }
    }

    /**
     * Obtém um número de miniatura de botão a partir de seu componente.
     * @param miniatura Miniatura cujo número deve ser obtido.
     * @return Número da miniatura de botão.
     */
    private int numMiniatura(CmpMiniaturaBotao miniatura) {
        for (int i = 0; i < 4; i ++)
            if (cmp_botoes[i] == miniatura)
                return i;
        return 4;
    }

    /**
     * Método que processa o evento de uma miniatura ser clicada.
     * @param miniatura Miniatura que foi clicada.
     */
    private void evtClicouMiniatura(CmpMiniaturaBotao miniatura) {
        if (isEnabled() == false) return;
        if (miniatura.isVisible() == false) return;

        int numero_miniatura = numMiniatura(miniatura);

        if (selecionado != 4) {
            cmp_botoes[selecionado].selecionar(false);
            for (OuvinteConjuntoBotoes ouvinte : ouvintes_evento)
                ouvinte.botaoDesselecionado(cmp_botoes[selecionado].obterBotao());
        }

        if (selecionado != numero_miniatura) {
            miniatura.selecionar(true);
            selecionado = numero_miniatura;
            for (OuvinteConjuntoBotoes ouvinte : ouvintes_evento)
                ouvinte.botaoSelecionado(cmp_botoes[selecionado].obterBotao());
        } else {
            selecionado = 4;
        }
    }

    /**
     * Adiciona um botão à prancha.
     * @param botao Botão a adicionar.
     */
    public void adicionarBotao(Botao botao) {
        if (botao != null && contagem_botoes < 4) {
            Botao botao_novo = new Botao(botao);
            cmp_botoes[contagem_botoes].definirBotao(botao_novo);
            cmp_botoes[contagem_botoes].setVisible(true);
            cmp_botoes[contagem_botoes].setEnabled(true);
            contagem_botoes ++;
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
        if (selecionado != 4) return cmp_botoes[selecionado].obterBotao();
        else return null;
    }

    /**
     * Remove o botão selecionado.
     */
    public void removerSelecionado() {
        if (selecionado != 4) {
            CmpMiniaturaBotao removido = cmp_botoes[selecionado];
            for (int i = selecionado; i < (contagem_botoes - 1); i ++)
                cmp_botoes[i].definirBotao(cmp_botoes[i + 1].obterBotao());
            contagem_botoes --;
            cmp_botoes[contagem_botoes].setVisible(false);
            cmp_botoes[contagem_botoes].setEnabled(false);
            removido.selecionar(false);
            selecionado = 4;
            revalidate();
            repaint();
            for (OuvinteConjuntoBotoes ouvinte : ouvintes_evento)
                ouvinte.botaoRemovido(removido.obterBotao());
        }
    }

    /**
     * Força o redesenho da miniatura de botão selecionada.
     */
    public void redesenharSelecionado() {
        if (selecionado != 4)
            cmp_botoes[contagem_botoes].notificarAlteracao();
    }

    /**
     * Grava esse modelo de prancha num arquivo.
     * @param destino Arquivo de destino.
     * @return Se a gravação foi bem-sucedida ou não.
     */
    public boolean gravarArquivo(File destino) {
        // cria o fluxo de saída de arquivo.
        FileOutputStream fluxo = null;

        // a princípio, foi bem-sucedido.
        boolean sucesso = true;

        try {
            // abre o arquivo.
            fluxo = new FileOutputStream(destino);

            // escreve a versão (cstring).
            byte[] versao = new byte[] { 'F', 'I', 'P', '1', '.', '0', 0 };
            fluxo.write(versao);

            // escreve a contagem de botões.
            fluxo.write(contagem_botoes);

            // escreve os botões.
            for (int i = 0; i < contagem_botoes; i ++)
                cmp_botoes[i].obterBotao().escreverBotao(fluxo);
        } catch (Exception e) {
            // houve uma falha
            sucesso = false;
        }

        // tenta fechar o arquivo.
        try {
            if (fluxo != null)
                fluxo.close();
        } catch (Exception e) {}

        // retorna se foi bem sucedido ou não.
        return sucesso;
    }

    /**
     * Abre um modelo de prancha de um arquivo.
     * @param origem Arquivo a abrir.
     * @return Se a abertura do arquivo ocorreu com sucesso.
     */
    public boolean abrirArquivo(File origem) {
        // cria uma vetor de botões
        Botao[] botoes = new Botao[4];
        for (int i = 0; i < botoes.length; i ++)
            botoes[i] = null;

        // cria um fluxo de entrada de arquivo.
        FileInputStream fluxo = null;

        // a princípio, foi bem-sucedido.
        boolean sucesso = true;

        try {
            // abre o arquivo.
            fluxo = new FileInputStream(origem);

            // lê a versão (cstring).
            byte[] versao = new byte[7];
            int lidos = fluxo.read(versao);
            if (lidos < 7) {
                sucesso = false;
            } else {
                if (versao[0] != 'F' || versao[1] != 'I' || versao[2] != 'P'
                 || versao[3] != '1' || versao[4] != '.' || versao[5] != '0'
                 || versao[6] != 0) {
                    sucesso = false;
                }
            }

            // se a versão for correta, continua.
            if (sucesso) {
                // lê a contagem de botões.
                int contagem = fluxo.read();

                if (contagem >= 0 && contagem <= 4) {
                    // conta quantos botões leu.
                    lidos = 0;

                    // lê os botões.
                    for (int i = 0; i < contagem; i ++) {
                        Botao botao = Botao.lerBotao(fluxo);
                        if (botao != null) {
                            botoes[lidos] = botao;
                            lidos ++;
                        }
                    }

                    // desseleciona o botão selecionado.
                    if (selecionado != 4)
                        cmp_botoes[selecionado].selecionar(false);
                    selecionado = 4;

                    // atualiza os botões na tela.
                    contagem_botoes = lidos;
                    for (int i = 0; i < 4; i ++) {
                        if (botoes[i] != null) {
                            cmp_botoes[i].definirBotao(botoes[i]);
                            cmp_botoes[i].setVisible(true);
                            cmp_botoes[i].setEnabled(true);
                            cmp_botoes[i].repaint();
                        } else {
                            cmp_botoes[i].setVisible(false);
                            cmp_botoes[i].setEnabled(false);
                        }
                    }
                } else sucesso = false;
            }
        } catch (Exception e) {
            // houve uma falha.
            sucesso = false;
        }

        // tenta fechar o arquivo.
        try {
            if (fluxo != null)
                fluxo.close();
        } catch (Exception e) {}

        // retorna se foi bem sucedido ou não.
        return sucesso;
    }

    /**
     * Obtém um vetor com os botões dessa prancha.
     * @return Vetor com os botões da prancha.
     */
    public Botao[] obterBotoes() {
        Botao[] lista = new Botao[contagem_botoes];
        for (int i = 0; i < lista.length; i ++) {
            lista[i] = cmp_botoes[i].obterBotao();
        }
        return lista;
    }

    @Override public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (CmpMiniaturaBotao miniatura : cmp_botoes)
            miniatura.setEnabled(enabled);
    }

    @Override public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }

    @Override public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override public boolean getScrollableTracksViewportWidth() {
        return getParent().getWidth() > getPreferredSize().width;
    }

    @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }
}
