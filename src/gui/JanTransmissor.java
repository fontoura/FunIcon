/*
Copyright (c) 2010, Felipe Michels Fontoura
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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import javax.swing.border.EmptyBorder;

import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import comunicacao.Comunicador;
import comunicacao.ComunicadorSerial;
import comunicacao.OuvinteProtocolo;
import comunicacao.ProtocoloFunIcon;

import armazenamento.Botao;

/**
 * Classe representante da janela de transmissão de dados.
 * @author Felipe Michels Fontoura
 */
@SuppressWarnings("serial")
public class JanTransmissor extends JDialog {
    /**
     * Se a transmissão ocorreu com sucesso.
     */
    private boolean sucesso;

    /**
     * Botões que estão sendo enviados para a prancha.
     */
    private Botao[] botoes;

    /**
     * Componente com a barra de progresso.
     */
    private JProgressBar barra_progresso;

    /**
     * Número do botão que está sendo transmitido.
     */
    private int botao_atual;

    /**
     * Gerente de protocolo utilizado por essa janela de transmissão.
     */
    private ProtocoloFunIcon protocolo;

    /**
     * Tratador do evento de uma transmissão ter ocorrido com sucesso.
     */
    private void evtProximo() {
        botao_atual ++;
        if (botao_atual < botoes.length) {
            if (! protocolo.enviarBotao(botao_atual, botoes[botao_atual])) {
                sucesso = false;
                dispose();
            }
        } else {
            sucesso = true;
            protocolo.desconectar();
            dispose();
        }
    }

    /**
     * Tratador do evento da comunicação ser abortada ou negada.
     */
    private void evtFalhou() {
        sucesso = false;
        protocolo.desconectar();
        dispose();
    }

    /**
     * Tratador do evento de a transferência de um dos botões avaçar até determinada porcentagem.
     * @param porcentagem Porcentagem até a qual foi a transferência desse botão.
     */
    private void evtRolagem(double porcentagem) {
        barra_progresso.setValue((int)((barra_progresso.getMaximum()*(botao_atual + porcentagem))/botoes.length));
    }

    /**
     * Cria uma janela de transmissão modal.
     * @param pai Janela-pai dessa janela de transmissão de botão
     * @param protocolo Gerente de protocolo a utilizar para transmitir.
     * @param botoes Lista de botões a transmitir.
     */
    private JanTransmissor(JFrame pai, ProtocoloFunIcon protocolo, Botao[] botoes) {
        super(pai, true);
        setTitle("FunIconGUI - Enviando");
        inserirComponentes();

        // define o descritor de protocolo.
        this.protocolo = protocolo;

        // define a lista de botões.
        this.botoes = botoes;

        // a princípio, transmitiu com sucesso.
        sucesso = true;

        // adiciona o ouvinte de eventos do protocolo.
        protocolo.adicionarOuvinteEvento(new OuvinteProtocolo() {
            @Override public void protocoloEnviou() {
                evtProximo();
            }
            @Override public void protocoloAbortou() {
                evtFalhou();
            }
            @Override public void protocoloNegou() {
                evtFalhou();
            }
            @Override public void protocoloAvancou(double porcentagem) {
                evtRolagem(porcentagem);

            }
        });

        // tenta ativar o envio do primeiro botão.
        if (! protocolo.enviarBotao(botao_atual, botoes[botao_atual])) {
            sucesso = false;
            dispose();
        }
    }

    /**
     * Insere os componentes na janela.
     */
    private void inserirComponentes () {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JLabel label_aguarde = new JLabel("Aguarde enquanto a comunicação com a prancha acontece.");

        barra_progresso = new JProgressBar(0, 1000);
        barra_progresso.setStringPainted(true);
        GridBagLayout gbl_panel = new GridBagLayout();

        gbl_panel.columnWidths = new int[]{0, 0};
        gbl_panel.rowHeights = new int[]{0, 0, 0};
        gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        GridBagConstraints gbc_label_aguarde = new GridBagConstraints();
        gbc_label_aguarde.insets = new Insets(0, 0, 5, 0);
        gbc_label_aguarde.gridx = 0;
        gbc_label_aguarde.gridy = 0;
        panel.add(label_aguarde, gbc_label_aguarde);

        GridBagConstraints gbc_barra_progresso = new GridBagConstraints();
        gbc_barra_progresso.fill = GridBagConstraints.BOTH;
        gbc_barra_progresso.gridx = 0;
        gbc_barra_progresso.gridy = 1;
        panel.add(barra_progresso, gbc_barra_progresso);

        panel.setSize(panel.getPreferredSize());

        pack();
        setSize(getInsets().left + getInsets().right + panel.getWidth(),
                getInsets().top + getInsets().bottom + panel.getHeight());
        setContentPane(panel);

    }

    /**
     * Cria uma janela de transmissão e inicia o processo de transferência.
     * @param pai Janela-pai dessa janela de seletor de transmissão.
     * @param porta Porta serial a usar para transmitir.
     * @param botoes Lista de botões a transmitir.
     * @return Se a transmissão ocorreu com sucesso.
     */
    public static boolean transmitir(JFrame pai, String porta, Botao[] botoes) {
        try {
            // cria o comunicador pela porta serial.
            Comunicador comunicador = new ComunicadorSerial(porta);

            // cria o gerente de protocolo.
            ProtocoloFunIcon protocolo = new ProtocoloFunIcon(comunicador);

            // cria a janela de transmissão.
            JanTransmissor janela = new JanTransmissor(pai, protocolo, botoes);

            // exibe a janela de transmissão em modal.
            janela.setVisible(true);

            // retorna se foi bem-sucedido.
            return janela.sucesso;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
