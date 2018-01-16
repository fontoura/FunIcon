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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JLabel;

import javax.swing.border.EmptyBorder;

import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Classe representante da janela de seleção da porta serial.
 * @author Felipe Michels Fontoura
 */
@SuppressWarnings("serial")
public class JanSeletorSerial extends JDialog {
    /**
     * String representando a porta serial selecionada.
     */
    private String retorno;

    /**
     * Lista com as portas seriais disponíveis.
     */
    private JList lista_portas;

    /**
     * Botão de cancelar a seleção de porta serial.
     */
    private JButton botao_cancelar;

    /**
     * Botão de confirmar a seleção de porta serial.
     */
    private JButton botao_ok;

    /**
     * Processa o evento de o botão de OK ser pressionado.
     */
    private void atualizarBotoes() {
        botao_ok.setEnabled(lista_portas.getSelectedIndex() != -1);
    }

    /**
     * Processa o evento de o botão de canceçar ser pressionado.
     */
    private void evtCancelar() {
        retorno = null;
        dispose();
    }

    /**
     * Processa o evento de o botão de OK ser pressionado.
     */
    private void evtOk() {
        retorno = lista_portas.getSelectedValue().toString();
        dispose();
    }

    /**
     * Exibe uma janela de seleção da porta serial.
     * @param pai Janela-pai dessa janela de seletor de porta serial.
     * @return Nome da porta selecionada.
     */
    public static String selecionarPorta(JFrame pai, String[] portas) {
        // cria a janela de edição de botão.
        JanSeletorSerial janela = new JanSeletorSerial(pai, portas);

        // mostra a janela de edição de botão.
        janela.setVisible(true);

        // retorna se o botão foi alterado.
        return janela.retorno;
    }

    /**
     * Cria uma nova janela de seletor de serial.
     */
    private JanSeletorSerial(JFrame pai, String[] portas) {
        super(pai, true);
        setTitle("FunIconGUI - Seletor de serial");
        inserirComponentes(portas);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent wEvt) {
                evtCancelar();
            }
        });
    }

    /**
     * Insere os componentes na janela.
     */
    private void inserirComponentes(String[] portas) {
        JPanel painel_janela = new JPanel();
        painel_janela.setBorder(new EmptyBorder(5, 5, 5, 5));

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 240, 360);
        setContentPane(painel_janela);

        GridBagLayout gbl_painel_janela = new GridBagLayout();

        JLabel label_selecione = new JLabel("Selecione a porta serial a usar:");

        lista_portas = new JList(portas);
        lista_portas.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lEvt) {
                atualizarBotoes();
            }
        });
        lista_portas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel painel_botoes = new JPanel();

        botao_cancelar = new JButton("Cancelar");
        botao_cancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) {
                evtCancelar();
            }
        });

        botao_ok = new JButton("Ok");
        botao_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) {
                evtOk();
            }
        });
        botao_ok.setEnabled(false);

        painel_botoes.setLayout(new GridLayout(1, 0, 5, 5));
        painel_botoes.add(botao_cancelar);
        painel_botoes.add(botao_ok);

        gbl_painel_janela.columnWidths = new int[]{0};
        gbl_painel_janela.rowHeights = new int[]{0, 0, 0};
        gbl_painel_janela.columnWeights = new double[]{1.0};
        gbl_painel_janela.rowWeights = new double[]{0.0, 1.0, 0.0};
        painel_janela.setLayout(gbl_painel_janela);

        GridBagConstraints gbc_label_selecione = new GridBagConstraints();
        gbc_label_selecione.anchor = GridBagConstraints.WEST;
        gbc_label_selecione.insets = new Insets(0, 0, 5, 0);
        gbc_label_selecione.gridx = 0;
        gbc_label_selecione.gridy = 0;
        painel_janela.add(label_selecione, gbc_label_selecione);

        GridBagConstraints gbc_lista_portas = new GridBagConstraints();
        gbc_lista_portas.insets = new Insets(0, 0, 5, 0);
        gbc_lista_portas.gridwidth = 1;
        gbc_lista_portas.fill = GridBagConstraints.BOTH;
        gbc_lista_portas.gridx = 0;
        gbc_lista_portas.gridy = 1;
        painel_janela.add(lista_portas, gbc_lista_portas);

        GridBagConstraints gbc_painel_botoes = new GridBagConstraints();
        gbc_painel_botoes.gridwidth = 1;
        gbc_painel_botoes.fill = GridBagConstraints.BOTH;
        gbc_painel_botoes.gridx = 0;
        gbc_painel_botoes.gridy = 2;
        painel_janela.add(painel_botoes, gbc_painel_botoes);
    }
}
