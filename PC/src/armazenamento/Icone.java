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

package armazenamento;

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Classe representante de um ícone.
 * @author Felipe Michels Fontoura
 * @author Marlon Subtil Marçal
 */
public class Icone {
    /**
     * Grade representando a imagem monocromática.
     */
    private boolean[][] grade;

    /**
     * Cria um novo ícone vazio.
     */
    public Icone() {
        // cria a grade
        grade = new boolean[8][8];

        // limpa a grade
        for (int x = 0; x < 8; x ++) for (int y = 0; y < 8; y ++) grade[x][y] = false;
    }

    /**
     * Cria um novo ícone, copiando os conteúdos de uma matriz.
     * @param grade Matriz representando a imagem (monocromática).
     */
    public Icone(boolean[][] grade) {
        this();

        // copia a grade
        substituirGrade(grade);
    }

    /**
     * Cria um novo ícone a partir de um ícone existente.
     * @param icone Ícone a clonar.
     */
    public Icone(Icone icone) {
        this();

        // copia a grade
        if (icone == null) for (int x = 0; x < 8; x ++) for (int y = 0; y < 8; y ++) grade[x][y] = false;
        else substituirGrade(icone.grade);
    }

    /**
     * Copia a grade de imagem para uma matriz externa.
     * @param grade Grade de destino.
     */
    public void extrairGrade(boolean[][] grade) {
        if (grade == null) return;
        if (grade.length < 8) return;
        for (int x = 0; x < 8; x ++) {
            if (grade[x] == null) return;
            if (grade[x].length < 8) return;
        }
        for (int x = 0; x < 8; x ++) for (int y = 0; y < 8; y ++) grade[x][y] = this.grade[x][y];
    }

    /**
     * Substitui o conteúdo da matriz desse ícone com os conteúdos de uma grade externa.
     * @param grade Grade de origem.
     */
    public void substituirGrade(boolean[][] grade) {
        if (grade == null) return;
        if (grade.length < 8) return;
        for (int x = 0; x < 8; x ++) {
            if (grade[x] == null) return;
            if (grade[x].length < 8) return;
        }
        for (int x = 0; x < 8; x ++) for (int y = 0; y < 8; y ++) this.grade[x][y] = grade[x][y];
    }

    /**
     * Processa uma imagem não monocromática, e aproxima uma matriz monocromática a partir dela. Retorna um
     * descritor de ícone relativo a essa grade.
     * @param imagem Imagem a importar.
     * @return Objeto com o ícone.
     */
    public static Icone importarImagem(BufferedImage imagem) {
        boolean[][] grade = new boolean[8][8];
        BufferedImage imgRedimensionada = new BufferedImage(
          /*        largura: */ 8,
          /*         altura: */ 8,
          /* tipo de imagem: */ BufferedImage.TYPE_4BYTE_ABGR
        );
        imgRedimensionada.getGraphics().drawImage(
          /* imagem de origem: */ imagem,
          /*                x: */ 0,
          /*                y: */ 0,
          /*          largura: */ 8,
          /*           altura: */ 8,
          /*       observador: */ null
        );
        Color c;
        int grey;
        int acumulador = 0;
        int media_grey;
        for (int contI = 0; contI < 8; contI++) {
            for (int contJ = 0; contJ < 8; contJ++) {
                c = new Color(imgRedimensionada.getRGB(contI, contJ));
                grey = (int)((0.2125 * c.getRed()) + (0.7154 * c.getGreen()) + (0.0721 * c.getBlue()));
                acumulador += grey;
            }
        }
        media_grey = acumulador / (8 * 8);
        // System.out.println("La media: " + media_grey);
        for (int contI = 0; contI < 8; contI++) {
            for (int contJ = 0; contJ < 8; contJ++) {
                c = new Color(imgRedimensionada.getRGB(contI, contJ));
                grey = (int)((0.2125 * c.getRed()) + (0.7154 * c.getGreen()) + (0.0721 * c.getBlue()));
                // grey = (int) (c.getRed() +  c.getGreen() + c.getBlue()) / 4;
                // System.out.print(grey + "\t");
                if (grey >= media_grey)
                    grade[contI][contJ] = true;
                else
                    grade[contI][contJ] = false;
            }
            // System.out.println("");
        }
        return new Icone(grade);
    }

    /**
     * Cria uma imagem de tamanho adequado para armazenar um ícone, e copia o conteúdo desse ícone
     * para a imagem.
     * @return Imagem gerada
     */
    public BufferedImage exportarImagem() {
        // cores utilizadas no desenho do ícone (ARGB)
        int cor_nao = 0xFF000000; // preto opaco
        int cor_sim  = 0xFFFF0000; // vermelho opaco

        // cria a imagem
        BufferedImage imagem = new BufferedImage(
          /*        largura: */ 8,
          /*         altura: */ 8,
          /* tipo de imagem: */ BufferedImage.TYPE_4BYTE_ABGR
        );

        // desenha a imagem, pixel a pixel.
        for (int x = 0; x < 8; x ++) for (int y = 0; y < 8; y ++) {
            imagem.setRGB(x, y, grade[x][y] ? cor_sim : cor_nao);
        }

        // retorna a imagem gerada.
        return imagem;
    }

    /**
     * Obtém a célula em determinada posição da matriz desse ícone.
     * @param x Posição horizontal da célula.
     * @param y Posição vertical da célula.
     * @return Célula na posição especificada.
     */
    public boolean obterCelula(int x, int y) {
        return grade[x][y];
    }

    /**
     * Define a célula em determinada posição da matriz desse ícone.
     * @param x Posição horizontal da célula.
     * @param y Posição vertical da célula.
     * @param valor Valor a inserir na célula na posição especificada.
     * @return Valor antigo contido nessa célula.
     */
    public boolean definirCelula(int x, int y, boolean valor) {
        boolean retorno = grade[x][y];
        grade[x][y] = valor;
        return retorno;
    }

    /**
     * Lê um objeto descritor de ícone de um fluxo de entrada.
     * @param fluxo Fluxo de entrada.
     * @return Objeto com o descritor de ícone.
     * @throws IOException Caso haja algum problema na leitura dos dados.
     */
    public static Icone lerIcone(InputStream fluxo) throws IOException {
        // cria a grade do ícone.
        boolean[][] grade = new boolean[8][8];

        // lê cada linha, bit a bit.
        for (int y = 0; y < 8; y ++) {
            int linha = fluxo.read();
            int shift = 1;
            for (int x = 0; x < 8; x ++) {
                grade[x][y] = (linha & shift) != 0;
                shift = shift << 1;
            }
        }

        // cria o ícone a partir da grade.
        return new Icone(grade);
    }

    /**
     * Escreve o objeto descritor de ícone para um fluxo de saída.
     * @param fluxo Fluxo de saída.
     * @throws IOException Caso haja algum problema na leitura dos dados.
     */
    public void escreverIcone(OutputStream fluxo) throws IOException {
        for (int y = 0; y < 8; y ++) {
            int linha = 0;
            int shift = 1;
            for (int x = 0; x < 8; x ++) {
                if (grade[x][y]) linha = linha | shift;
                shift = shift << 1;
            }
            fluxo.write(linha);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean equals(Object obj) {
        if (obj instanceof Icone) {
            Icone icone = (Icone) obj;
            for (int x = 0; x < 8; x ++) for (int y = 0; y < 8; y ++)
                if (icone.grade[x][y] != grade[x][y])
                    return false;
            return true;
        } else return false;
    }
}
