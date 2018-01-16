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

import armazenamento.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Componente gráfico que mostra uma grade de ícone editável na tela.
 * @author Felipe Michels Fontoura
 */
@SuppressWarnings("serial")
public class CmpEditorIcone extends JComponent {
    /**
     * Imagem com a grade do ícone associado a esse componente.
     */
    private BufferedImage imagem_grade;

    /**
     * Descritor do ícone associado a esse componente.
     */
    private Icone icone;

    /**
     * Se a grade foi alterada, e é preciso redesenhar.
     */
    private boolean grade_alterada;

    /**
     * Se a grade foi alterada, e é preciso redesenhar.
     */
    private boolean componente_habilitado;

    /**
     * Constrói um novo componente editor de grade de ícone.
     * @param icone Descritor de ícone para inicializar esse componente. Pode ser <b>null</b>.
     */
    public CmpEditorIcone(Icone icone) {
        grade_alterada = false;

        // define o ícone associado ao componente.
        definirIcone(icone);

        // adiciona o ouvinte de cliques do cursor.
        addMouseListener(new MouseListener () {
            @Override public void mouseClicked(MouseEvent mEvt) {}
            @Override public void mouseEntered(MouseEvent mEvt) {}
            @Override public void mouseExited(MouseEvent mEvt) {}
            @Override public void mousePressed(MouseEvent mEvt) {}
            @Override public void mouseReleased(MouseEvent mEvt) { evtSoltouMouse(mEvt.getX(), mEvt.getY()); }
        });
    }

    /**
     * Define o ícone associado a esse componente.
     * @param icone Descritor de ícone para associar a esse componente. Pode ser <b>null</b>.
     */
    public void definirIcone(Icone icone) {
        // define o ícone associado a esse componente.
        if (icone == null) icone = new Icone();
        this.icone = icone;

        componente_habilitado = isEnabled();

        // atualiza a imagem de grade.
        imagem_grade = AuxiliarGUI.desenharGradeIcone(
          /* largura da imagem: */ getWidth(),
          /*  altura da imagem: */ getHeight(),
          /*  ícone a desenhar: */ icone,
          /*    grade quadrada: */ true,
          /*  grade habilitada: */ componente_habilitado
        );

        // redesenha o componente.
        repaint();
    }

    /**
     * Obtém o descritor de ícone associado a esse componente.
     * @return Descritor de ícone associado a esse componente.
     */
    public Icone obterIcone() {
        return icone;
    }

    /**
     * Método que processa o evento de o mouse ser solto.
     * @param mouse_x Posição horizontal do cursor.
     * @param mouse_y Posição vertical do cursor.
     */
    private void evtSoltouMouse(int mouse_x, int mouse_y) {
        if (isEnabled()) {
            Point posicao = AuxiliarGUI.posicaoEm(getWidth(), getHeight(), mouse_x, mouse_y, true);
            if (posicao != null) {
                grade_alterada = true;
                icone.definirCelula(posicao.x, posicao.y, ! icone.obterCelula(posicao.x, posicao.y));
            }
            repaint();
        }
    }

    /**
     * Desenha o componente num determinado contexto gráfico. Esse método é chamado de forma
     * automática quando é necessário redesenhar a tela.
     * @param g Contexto gráfico de destino.
     */
    @Override public void paint(Graphics g) {
        super.paint(g);

        int largura = getWidth();
        int altura = getHeight();

        // verifica se o tamanho do componente mudou.
        if (largura !=  imagem_grade.getWidth() || altura != imagem_grade.getHeight())
            grade_alterada = true;

        // verifica se a habilitação do componente mudou.
        if (componente_habilitado != isEnabled()) {
            componente_habilitado = isEnabled();
            grade_alterada = true;
        }

        if (grade_alterada)
            imagem_grade = AuxiliarGUI.desenharGradeIcone(
              /* largura da imagem: */ largura,
              /*  altura da imagem: */ altura,
              /*  ícone a desenhar: */ icone,
              /*    grade quadrada: */ true,
              /*  grade habilitada: */ componente_habilitado
            );

        grade_alterada = false;

        // desenha a grade.
        g.drawImage(imagem_grade, 0, 0, null);
    }
}
