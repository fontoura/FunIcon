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
import audio.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Componente gráfico que mostra a forma de uma onda sonora e um seletor deslizante.
 * @author Felipe Michels Fontoura
 */
@SuppressWarnings("serial")
public class CmpEditorSom extends JComponent {
    /**
     * Constante indicando que o mouse não está sobre nada.
     */
    private static final int SOBRE_NADA = 0;

    /**
     * Constante indicando que o mouse está sobre o início do seletor de trecho.
     */
    private static final int SOBRE_INICIO_SELETOR = 1;

    /**
     * Constante indicando que o mouse está sobre o final do seletor de trecho.
     */
    private static final int SOBRE_FINAL_SELETOR = 2;

    /**
     * Constante indicando que o mouse está sobre o centro do seletor de trecho.
     */
    private static final int SOBRE_MEIO_SELETOR = 3;

    /**
     * Constante simbólica do estado de espera.
     */
    private static final int ESTADO_ESPERA = 0;

    /**
     * Constante simbólica do estado de arrastar o início do seletor de trecho.
     */
    private static final int ESTADO_MOVENDO_INICIO_SELETOR = 1;

    /**
     * Constante simbólica do estado de arrastar o final do seletor de trecho.
     */
    private static final int ESTADO_MOVENDO_FINAL_SELETOR = 2;

    /**
     * Constante simbólica do estado de arrastar o  seletor de trecho inteiro.
     */
    private static final int ESTADO_MOVENDO_SELETOR = 3;

    /**
     * Estado atual do componente.
     */
    private int estado;

    /**
     * Posição horizontal do cursor no último evento de mouse.
     */
    private int mouse_x_anterior;

    /**
     * Constante indicando a margem da borda do seletor, em pixels.
     */
    private static final int MARGEM_BORDA_SELETOR = 5;

    /**
     * Descritor do som associado a esse componente.
     */
    private Som som;

    /**
     * Amostra inicial do seletor de trecho de som.
     */
    private int amostra_inicial_seletor;

    /**
     * Amostra final do seletor de trecho de som.
     */
    private int amostra_final_seletor;

    /**
     * Posição horizontal inicial do seletor de trecho de som.
     */
    private int x_inicial_seletor;

    /**
     * Posição horizontal final do seletor de trecho de som.
     */
    private int x_final_seletor;

    /**
     * Imagem com a onda sonora associada a esse componente.
     */
    private BufferedImage imagem_onda;

    /**
     * Tamanho máximo do seletor de som.
     */
    private int amostras_maximas_seletor;

    /**
     * Constrói um novo componente editor de onda sonora.
     * @param som Descritor de som para inicializar esse componente. Pode ser <b>null</b>.
     */
    public CmpEditorSom(Som som) {
        // define o som associado ao componente.
        definirSom(som);

        // define o tamanho preferencial.
        this.setPreferredSize(new Dimension(1, 75));

        // adiciona o ouvinte de movimentação do cursor.
        addMouseMotionListener(new MouseMotionListener () {
            @Override public void mouseDragged(MouseEvent mEvt) { evtArrastouMouse(mEvt.getX(), mEvt.getY()); mouse_x_anterior = mEvt.getX(); }
            @Override public void mouseMoved(MouseEvent mEvt) { evtMoveuMouse(mEvt.getX(), mEvt.getY()); mouse_x_anterior = mEvt.getX(); }
        });

        // adiciona o ouvinte de cliques do cursor.
        addMouseListener(new MouseListener () {
            @Override public void mouseClicked(MouseEvent mEvt) {}
            @Override public void mouseEntered(MouseEvent mEvt) {}
            @Override public void mouseExited(MouseEvent mEvt) {}
            @Override public void mousePressed(MouseEvent mEvt) { evtApertouMouse(mEvt.getX(), mEvt.getY()); mouse_x_anterior = mEvt.getX(); }
            @Override public void mouseReleased(MouseEvent mEvt) { evtSoltouMouse(mEvt.getX(), mEvt.getY()); mouse_x_anterior = mEvt.getX(); }
        });
    }

    /**
     * Define o som associado a esse componente.
     * @param som Descritor de som para associar a esse componente. Pode ser <b>null</b>.
     */
    public void definirSom(Som som) {
        // define o som associado a esse componente.
        if (som == null) som = new Som();
        this.som = som;

        // define o número máximo de amostras selecionável com o seletor.
        amostras_maximas_seletor = Math.min(Som.SOM_AMOSTRAS, som.contagemAmostras());

        // define o número da amostra final do seletor.
        amostra_final_seletor = amostras_maximas_seletor - 1;

        // define o número da amostra inicial do seletor.
        amostra_inicial_seletor = 0;

        // atualiza a imagem de onda.
        imagem_onda = AuxiliarGUI.desenharOndaSonora(
          /* largura da imagem: */ getWidth(),
          /*  altura da imagem: */ getHeight(),
          /*  descritor de som:*/ som
        );

        // redesenha o componente.
        repaint();
    }

    /**
     * Obtém todo o som associado a esse componente.
     * @return Descritor de todo o som associado a esse componente.
     */
    public Som obterSomCompleto() {
        return som;
    }

    /**
     * Obtém o trecho do som associado a esse componente marcado com o seletor.
     * @return Descritor do trecho do som associado a esse componente marcado com o seletor.
     */
    public Som obterSomSelecionado() {
        return som.obterTrecho(amostra_inicial_seletor, amostra_final_seletor - amostra_inicial_seletor + 1);
    }

    /**
     * Obtém um reprodutor de som associado a esse componente.
     * @return Reprodutor de som associado a esse componente.
     */
    public Reprodutor obterReprodutor() {
        return som.obterTrecho(amostra_inicial_seletor, amostra_final_seletor - amostra_inicial_seletor + 1).criarReprodutor();
    }

    /**
     * Obtém o número da amostra associada a determinada posição horizontal.
     * @param x Posição horizontal.
     * @return Número da amostra.
     */
    private int posicaoParaAmostra(int x) {
        if (getWidth() == 0) return 0;
        return ((x + 1) * som.contagemAmostras()) / (getWidth() + 1);
    }

    /**
     * Obtém a posição horizontal associada a determinado número de amostra.
     * @param amostra Número de amostra.
     * @return Posição horizontal.
     */
    private int amostraParaPosicao(int amostra) {
        if (som.contagemAmostras() == 0) return 0;
        return ((amostra + 1) * getWidth()) / (som.contagemAmostras() + 1);
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

        // se necessário, atualiza a imagem com a forma de onda.
        if (largura !=  imagem_onda.getWidth() || altura != imagem_onda.getHeight())
            imagem_onda = AuxiliarGUI.desenharOndaSonora(
              /* largura da imagem: */ largura,
              /*  altura da imagem: */ altura,
              /*  descritor de som:*/ som
            );

        // desenha a forma de onda.
        g.drawImage(imagem_onda, 0, 0, null);

        // calcula a posição inicial do seletor na tela.
        x_inicial_seletor = amostraParaPosicao(amostra_inicial_seletor);
        x_final_seletor = amostraParaPosicao(amostra_final_seletor);

        // define as cores do seletor
        Color fundo_seletor = new Color(0, 0, 0, 128);
        Color borda_seletor = new Color(0, 0, 0, 255);

        // desenha o seletor
        g.setColor(fundo_seletor);
        g.fillRect(x_inicial_seletor, 0, x_final_seletor - x_inicial_seletor, altura);
        g.setColor(borda_seletor);
        g.drawRect(x_inicial_seletor, 0, x_final_seletor - x_inicial_seletor, altura - 1);
    }

    /**
     * Verifica sobre qual parte do componente determinada posição está.
     * @param posicao_x Posição horizontal a testar.
     * @param posicao_y Posição vertical a testar.
     * @return Constante de sobreposição indicando sobre qual parte do componente essa posição está.
     */
    private int obterSobre(int posicao_x, int posicao_y) {
        int dist_inicio_seletor = posicao_x - x_inicial_seletor;
        int dist_final_seletor = x_final_seletor - posicao_x;

        // verifica se está sobre o início do seletor
        boolean sobre_inicio_seletor = -MARGEM_BORDA_SELETOR <= dist_inicio_seletor && MARGEM_BORDA_SELETOR >= dist_inicio_seletor;

        // verifica se está sobre o final do seletor
        boolean sobre_final_seletor = -MARGEM_BORDA_SELETOR <= dist_final_seletor && MARGEM_BORDA_SELETOR >= dist_final_seletor;

        // verifica se está sobre o seletor em si
        boolean sobre_seletor = dist_inicio_seletor >= 0 && dist_final_seletor >= 0;

        if (sobre_seletor) {
            // está sobre o seletor de trecho do som
            if (sobre_inicio_seletor || sobre_final_seletor) {
                // está sobre o um dos seletores laterais, determina qual.
                if (dist_inicio_seletor == dist_final_seletor) return SOBRE_MEIO_SELETOR;
                else if (dist_inicio_seletor < dist_final_seletor) return SOBRE_INICIO_SELETOR;
                else return SOBRE_FINAL_SELETOR;
            } else {
                // está sobre o meio do seletor
                return SOBRE_MEIO_SELETOR;
            }
        } else if (sobre_inicio_seletor || sobre_final_seletor) {
            // está sobre o um dos seletores laterais, determina qual.
            if (dist_inicio_seletor < dist_final_seletor) return SOBRE_INICIO_SELETOR;
            else return SOBRE_FINAL_SELETOR;
        } else return SOBRE_NADA;
    }

    /**
     * Método que processa o evento de o mouse ser solto.
     * @param mouse_x Posição horizontal do cursor.
     * @param mouse_y Posição vertical do cursor.
     */
    private void evtSoltouMouse(int mouse_x, int mouse_y) {
        if (isEnabled()) {
            // o componente está habilitado.

            if (estado != ESTADO_ESPERA) {
                // termina qualquer atividade diferente da espera.
                setCursor(Cursor.getDefaultCursor());
                estado = ESTADO_ESPERA;
            }
        } else {
            // o componente está desabilitado
            setCursor(Cursor.getDefaultCursor());
            estado = ESTADO_ESPERA;
        }
    }

    /**
     * Método que processa o evento de o mouse ser pressionado.
     * @param mouse_x Posição horizontal do cursor.
     * @param mouse_y Posição vertical do cursor.
     */
    private void evtApertouMouse(int mouse_x, int mouse_y) {
        if (isEnabled()) {
            // o componente está habilitado.

            if (estado == ESTADO_ESPERA) {
                // verifica onde está o cursor.
                int sobre = obterSobre(mouse_x, mouse_y);

                if (sobre == SOBRE_MEIO_SELETOR) {
                    // está sobre o meio do seletor, começa a arrastar o seletor inteiro.
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    estado = ESTADO_MOVENDO_SELETOR;
                } else if (sobre == SOBRE_INICIO_SELETOR) {
                    // está sobre o início do seletor, começa a arrastá-lo.
                    setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                    estado = ESTADO_MOVENDO_INICIO_SELETOR;
                } else if (sobre == SOBRE_FINAL_SELETOR) {
                    // está sobre o final do seletor, começa a arrastá-lo.
                    setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                    estado = ESTADO_MOVENDO_FINAL_SELETOR;
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            } else {
                // não deve chegar nesse ponto.
                setCursor(Cursor.getDefaultCursor());
                estado = ESTADO_ESPERA;
            }
        } else {
            // o componente está desabilitado
            setCursor(Cursor.getDefaultCursor());
            estado = ESTADO_ESPERA;
        }
    }

    /**
     * Método que processa o evento de o mouse ser movimentado.
     * @param mouse_x Posição horizontal do cursor.
     * @param mouse_y Posição vertical do cursor.
     */
    private void evtMoveuMouse(int mouse_x, int mouse_y) {
        if (isEnabled()) {
            // o componente está habilitado.

            if (estado == ESTADO_ESPERA) {
                // verifica onde está o cursor.
                int sobre = obterSobre(mouse_x, mouse_y);

                if (sobre == SOBRE_MEIO_SELETOR) // está sobre o meio do seletor.
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                else if (sobre == SOBRE_INICIO_SELETOR) // está sobre o início do seletor.
                    setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                else if (sobre == SOBRE_FINAL_SELETOR) // está sobre o final do seletor.
                    setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                else setCursor(Cursor.getDefaultCursor());
            } else {
                // não deve chegar nesse ponto.
                setCursor(Cursor.getDefaultCursor());
                estado = ESTADO_ESPERA;
            }
        } else {
            // o componente está desabilitado
            setCursor(Cursor.getDefaultCursor());
            estado = ESTADO_ESPERA;
        }
    }

    /**
     * Método que processa o evento de o mouse ser arrastado.
     * @param mouse_x Posição horizontal do cursor.
     * @param mouse_y Posição vertical do cursor.
     */
    private void evtArrastouMouse(int mouse_x, int mouse_y) {
        if (isEnabled()) {
            // o componente está habilitado.

            if (estado == ESTADO_ESPERA) {
                // verifica onde está o cursor.
                int sobre = obterSobre(mouse_x, mouse_y);

                if (sobre == SOBRE_MEIO_SELETOR) // está sobre o meio do seletor.
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                else if (sobre == SOBRE_INICIO_SELETOR) // está sobre o início do seletor.
                    setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                else if (sobre == SOBRE_FINAL_SELETOR) // está sobre o final do seletor.
                    setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                else setCursor(Cursor.getDefaultCursor());
            } else if (estado == ESTADO_MOVENDO_INICIO_SELETOR) {
                // verifica qual deve ser a nova amostra inicial.
                int nova_amostra_inicial_seletor = posicaoParaAmostra(mouse_x);

                // verifica se a nova amostra inicial é posterior à amostra final.
                if (nova_amostra_inicial_seletor >= amostra_final_seletor)
                    nova_amostra_inicial_seletor = amostra_final_seletor - 1;

                // verifica se a nova amostra inicial é anterior demais da amostra final.
                if (amostra_final_seletor - nova_amostra_inicial_seletor >= amostras_maximas_seletor)
                    nova_amostra_inicial_seletor = amostra_final_seletor - amostras_maximas_seletor + 1;

                // verifica se a nova amostra inicial é anterior à amostra zero.
                if (nova_amostra_inicial_seletor < 0)
                    nova_amostra_inicial_seletor = 0;

                // atualiza a amostra inicial.
                amostra_inicial_seletor = nova_amostra_inicial_seletor;

                // redesenha a tela.
                setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                repaint();
            } else if (estado == ESTADO_MOVENDO_FINAL_SELETOR) {
                // verifica qual deve ser a nova amostra inicial.
                int nova_amostra_final_seletor = posicaoParaAmostra(mouse_x);

                // verifica se a nova amostra final é anterior à amostra inial.
                if (nova_amostra_final_seletor <= amostra_inicial_seletor)
                    nova_amostra_final_seletor = amostra_inicial_seletor + 1;

                // verifica se a nova amostra final é posterior demais da amostra inicial.
                if (nova_amostra_final_seletor - amostra_inicial_seletor >= amostras_maximas_seletor)
                    nova_amostra_final_seletor = amostra_inicial_seletor + amostras_maximas_seletor - 1;

                // verifica se a nova amostra final é posterior à última amostra.
                if (nova_amostra_final_seletor >= som.contagemAmostras())
                    nova_amostra_final_seletor = som.contagemAmostras() - 1;

                // atualiza a amostra final.
                amostra_final_seletor = nova_amostra_final_seletor;

                // redesenha a tela.
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                repaint();
            } else if (estado == ESTADO_MOVENDO_SELETOR) {
                // verifica qual a variação de amostra.
                int variacao_amostra = posicaoParaAmostra(mouse_x) - posicaoParaAmostra(mouse_x_anterior);

                // verifica quais devem ser as novas amostras inicial e final.
                int nova_amostra_inicial_seletor = amostra_inicial_seletor + variacao_amostra;
                int nova_amostra_final_seletor = amostra_final_seletor + variacao_amostra;

                // verifica se a nova amostra inicial é anterior à amostra zero.
                if (nova_amostra_inicial_seletor < 0) {
                    nova_amostra_final_seletor = nova_amostra_final_seletor - nova_amostra_inicial_seletor;
                    nova_amostra_inicial_seletor = 0;
                }

                // verifica se a nova amostra final é posterior à última amostra.
                if (nova_amostra_final_seletor >= som.contagemAmostras()) {
                    nova_amostra_inicial_seletor = nova_amostra_inicial_seletor - (1 + nova_amostra_final_seletor - som.contagemAmostras());
                    nova_amostra_final_seletor = som.contagemAmostras() - 1;
                }

                // atualiza as amostras inicial e final.
                amostra_inicial_seletor = nova_amostra_inicial_seletor;
                amostra_final_seletor = nova_amostra_final_seletor;

                // redesenha a tela.
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                repaint();
            }
        } else {
            // o componente está desabilitado
            setCursor(Cursor.getDefaultCursor());
            estado = ESTADO_ESPERA;
        }
    }
}
