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

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;

/**
 * Componente de exibição de uma faixa de texto deslizável.
 * @author Felipe Michels Fontoura
 */
@SuppressWarnings("serial")
public class CmpTextoDeslizavel extends JComponent {
    /**
     * Componente com a faixa de texto original.
     */
    private JLabel label;

    /**
     * Flag indicando se a faixa de texto está deslizando.
     */
    private boolean deslizando;

    /**
     * Posição horizontal da faixa de texto.
     */
    private int x = 0;

    /**
     * Constrói uma faixa de texto deslizável a partir de uma string.
     * @param texto String que deve ser escrita na faixa de texto deslizável.
     */
    public CmpTextoDeslizavel(String texto) {
        if (texto == null) texto = " ";
        if (texto.length() == 0) texto = " ";

        setLayout(null);

        label = new JLabel(texto);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        add(label);

        this.setPreferredSize(new Dimension(1, label.getPreferredSize().height));

        definirDeslizando(false);

        addComponentListener(new ComponentListener() {
            @Override public void componentHidden(ComponentEvent cEvt) {}
            @Override public void componentMoved(ComponentEvent cEvt) {}
            @Override public void componentResized(ComponentEvent cEvt) { evtRedimensionou(); }
            @Override public void componentShown(ComponentEvent cEvt) {}
        });
    }

    /**
     * Método que trata o evento de o componente ser redimensionado.
     */
    private void evtRedimensionou() {
        ajustarTamanhos();
    }

    /**
     * Gatilho da movimentação dos textos deslizantes.
     * @param reiniciar Se o movimento dos textos deve ser reiniciado.
     */
    public void gatilhoDeslizamento(boolean reiniciar) {
        if (deslizando) {
            if (reiniciar) x = getWidth();
            else x --;
        } else{
            x = 0;
        }
        label.setLocation(x, 0);
    }

    /**
     * Verifica se o texto está dentro da área visível da tela.
     * @return Verifica se o texto está dentro da área visível da tela.
     */
    public boolean dentro() {
        if (x < -label.getPreferredSize().width) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Define se o texto deve ou não deslizar na tela.
     * @param deslizando Se o texto deve ou não deslizar na tela.
     */
    public void definirDeslizando(boolean deslizando) {
        this.deslizando = deslizando;
        gatilhoDeslizamento(true);
        evtRedimensionou();
    }

    /**
     * Define o texto em exibição no componente.
     * @param texto Novo texto a exibir no componente.
     */
    public void definirTexto(String texto) {
        label.setText(texto);
        ajustarTamanhos();
    }

    /**
     * Ajusta o tamanho do componente.
     */
    private void ajustarTamanhos() {
        if (deslizando) {
            label.setSize(label.getPreferredSize().width, getSize().height);
        } else {
            label.setSize(getSize());
        }
    }
}
