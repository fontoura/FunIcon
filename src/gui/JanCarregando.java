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
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/**
 * Janela de carregamento.
 * @author Felipe Michels Fontoura
 */
@SuppressWarnings("serial")
public class JanCarregando extends JDialog {
    /**
     * Cria uma nova janela de carregamento.
     */
    public JanCarregando(String mensagem) {
        super();

        // define as características da janela.
        setTitle("FunIconGUI - Carregando");
        setResizable(false);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        pack();
        JPanel painel = new JPanel();
        painel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel label = new JLabel(mensagem);
        painel.add(label);

        // define o tamanho da janela.
        setBounds(
          /*       x: */ 100,
          /*       y: */ 100,
          /* largura: */ getInsets().left + painel.getPreferredSize().width + getInsets().right,
          /*   altura:*/ getInsets().top + painel.getPreferredSize().height + getInsets().bottom
        );

        // adiciona os conteúdos à janela.
        setContentPane(painel);
    }
}
