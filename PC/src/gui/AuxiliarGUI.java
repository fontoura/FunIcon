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

import armazenamento.Faixa;
import armazenamento.Som;
import armazenamento.Icone;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.PrintStream;

/**
 * Classe com funções auxiliares das classes da interface gráfica.
 * @author Felipe Michels Fontoura
 *
 */
class AuxiliarGUI {
    /**
     * Terminal de impressão (verbose).
     */
    private static PrintStream fluxo = null;

    private AuxiliarGUI() {}

    /**
     * Gera uma imagem contendo o desenho de uma onda sonora.
     * @param largura Largura da imagem a gerar.
     * @param altura Altura da imagem a gerar.
     * @param som Descritor do som cuja onda deve ser desenhada.
     * @return Imagem gerada com o desenho da onda.
     */
    public static BufferedImage desenharOndaSonora(int largura, int altura, Som som) {
        // cores utilizadas no desenho da onda (ARGB)
        int cor_fundo = 0x00000000; // preto, 0% opaco
        int cor_onda  = 0xFF00FF00; // verde, 100% opaco

        if (largura <= 0) largura = 1;
        if (altura <= 0) altura = 1;

        // cria a imagem
        BufferedImage imagem = new BufferedImage(
          /*        largura: */ largura,
          /*         altura: */ altura,
          /* tipo de imagem: */ BufferedImage.TYPE_4BYTE_ABGR
        );

        // extrai as amostras do som.
        byte[] amostras = new byte[som.contagemAmostras()];
        som.extrairAmostras(amostras, true);

        if (fluxo != null) fluxo.println("## ONDA ## desenho iniciado.");

        // desenha o gráfico, pixel a pixel.
        for (int x = 0; x < largura; x ++) {
            // determina quais amostras nessa posição.
            Faixa<Integer> faixa = amostrasSomEm(largura, x, amostras.length);
            int primeira_amostra = faixa.obterPrimeiro();//((amostras.length - 1) * x) / (largura + 1);
            int ultima_amostra = faixa.obterUltimo();//1 + (((amostras.length - 1) * (x + 1)) / (largura + 1));

            // determina os valores máximo e mínimo nessa posição.
            int valor_minimo = amostras[primeira_amostra];
            int valor_maximo = amostras[primeira_amostra];
            for (int i = primeira_amostra + 1; i < ultima_amostra; i ++) {
                valor_minimo = Math.min(valor_minimo, amostras[i]);
                valor_maximo = Math.max(valor_maximo, amostras[i]);
            }

            // determina os pontos mínimo e máximo no gráfico.
            valor_minimo = ((altura - 1) * (128 - valor_minimo)) / 256;
            valor_maximo = ((altura - 1) * (128 - valor_maximo)) / 256;

            if (fluxo != null) fluxo.println("## ONDA ##   x = " + x + " ---> amostras[" + primeira_amostra + ":" + ultima_amostra + "] ---> y = [" + valor_minimo + ":" + valor_maximo + "]");

            // desenha os pontos no gráfico
            for (int y = 0; y < valor_maximo; y ++)
                imagem.setRGB(x, y, cor_fundo);
            for (int y = valor_maximo; y <= valor_minimo; y ++)
                imagem.setRGB(x, y, cor_onda);
            for (int y = valor_minimo + 1; y < altura; y ++)
                imagem.setRGB(x, y, cor_fundo);
        }

        if (fluxo != null) fluxo.println("## ONDA ## desenho completo.");
        return imagem;
    }

    /**
     * Gera uma imagem contendo o desenho da grade de um ícone.
     * @param largura Largura da imagem a gerar.
     * @param altura Altura da imagem a gerar.
     * @param icone Icone a desenhar.
     * @param quadrado Se o ícone deve ser forçadamente ser quadrado.
     * @return Imagem gerada com o desenho da grade do ícone.
     */
    public static BufferedImage desenharGradeIcone(int largura, int altura, Icone icone, boolean quadrado, boolean habilitado) {
        // cores utilizadas no desenho do ícone (ARGB).
        int cor_nao = 0xFF000000; // preto opaco.
        int cor_sim  = habilitado ? 0xFFFF0000 : 0xFF808080; // vermelho opaco.
        int cor_fora = 0x00000000; // preto transparente.

        // ajusta as dimensões.
        if (largura <= 0) largura = 1;
        if (altura <= 0) altura = 1;

        if (fluxo != null) fluxo.println("## ICONE ## desenho iniciado (" + (habilitado ? "habilitado" : "desabilitado") + ")");
        if (fluxo != null) fluxo.println("## ICONE ## (largura, altura) = (" + largura + ", " + altura + ")");

        // cria a imagem
        BufferedImage imagem = new BufferedImage(
          /*        largura: */ largura,
          /*         altura: */ altura,
          /* tipo de imagem: */ BufferedImage.TYPE_4BYTE_ABGR
        );

        // cria os offsets.
        int x0 = 0;
        int y0 = 0;
        int x1 = largura;
        int y1 = altura;

        // ajuda para o desenho quadrado, se necessário.
        if (quadrado && (largura != altura)) {
            int minimo = Math.min(largura, altura);

            x0 = (largura - minimo) / 2;
            x1 = largura - x0;

            y0 = (altura - minimo) / 2;
            y1 = altura - y0;
        }

        if (fluxo != null) fluxo.println("## ICONE ## (x0, y0)-(x1,y1) = (" + x0 + ", " + y0 + ")-(" + x1 + ", " + y1 + ")");

        // desenha o gráfico, pixel a pixel.
        for (int x = 0; x < largura; x ++) for (int y = 0; y < altura; y ++) {
            if (x >= x0 && x < x1 && y >= y0 && y < y1) {
                // determina quais amostras nessa posição.
                int grade_x = ((x - x0) * 8) / (x1 - x0);
                int grade_y = ((y - y0) * 8) / (y1 - y0);

                // define o pixel.
                imagem.setRGB(x, y, icone.obterCelula(grade_x, grade_y) ? cor_sim : cor_nao);
            } else {
                imagem.setRGB(x, y, cor_fora);
            }
        }

        return imagem;
    }

    /**
     * Obtém a faixa de amostras de som relativa a uma posição da imagem de onda.
     * @param largura Largura da imagem com a onda.
     * @param x Posição horizontal a verificar.
     * @param amostras Número total de amostras do som.
     * @return Faixa de amostras relativa a essa posição.
     */
    public static Faixa<Integer> amostrasSomEm(int largura, int x, int amostras) {
        if (largura > amostras) {
            int a = (x * amostras) / largura;
            return new Faixa<Integer>(a, a);
        } else if (largura < amostras) {
            int a0 = (x * amostras) / largura;
            int a1 = ((x + 1) * amostras) / largura - 1;
            return new Faixa<Integer>(a0, a1);
        } else {
            return new Faixa<Integer>(x, x);
        }
    }

    /**
     * Retorna a célula do ícone relativa a determinada posição na tela.
     * @param largura Largura da imagem.
     * @param altura Altura da imagem.
     * @param x Posição horizontal na imagem.
     * @param y Posição vertical na imagem.
     * @param quadrado Se a imagem do ícone deve forçadamente ser quadrada.
     * @return Célula relativa a essa posição na tela (nulo se a posição for fora do ícone).
     */
    public static Point posicaoEm(int largura, int altura, int x, int y, boolean quadrado) {
        // ajusta as dimensões.
        if (largura <= 0) largura = 1;
        if (altura <= 0) altura = 1;

        // cria os offsets.
        int x0 = 0;
        int y0 = 0;
        int x1 = largura;
        int y1 = altura;

        // ajuda para o desenho quadrado, se necessário.
        if (quadrado && (largura != altura)) {
            int minimo = Math.min(largura, altura);

            x0 = (largura - minimo) / 2;
            x1 = largura - x0;

            y0 = (altura - minimo) / 2;
            y1 = altura - y0;
        }
        if (x >= x0 && x < x1 && y >= y0 && y < y1) {
            int grade_x = ((x - x0) * 8) / (x1 - x0);
            int grade_y = ((y - y0) * 8) / (y1 - y0);
            return new Point(grade_x, grade_y);
        } else {
            return null;
        }
    }
}
