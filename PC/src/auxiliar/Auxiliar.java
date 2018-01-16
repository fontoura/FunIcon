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

package auxiliar;

import java.io.*;

/**
 * Classe com métodos estáticos auxiliares.
 * @author Felipe Michels Fontoura
 */
public final class Auxiliar {
    private Auxiliar() {
    }

    /**
     * Copia um trecho de um vetor para um novo vetor.
     * @param vetor Vetor de origem.
     * @param inicio Posição de onde começar a ler o vetor original.
     * @param tamanho Número de bytes a ler.
     * @param completar Byte para completar as lacunas faltantes.
     * @return Cópia do trecho do vetor.
     */
    public static byte[] copiarTrecho(byte[] vetor, int inicio, int tamanho, byte completar) {
        byte[] retorno = new byte[tamanho];
        if (vetor == null) {
            // não há vetor, então cria um vetor vazio.
            for (int i = 0; i < tamanho; i ++)
                retorno[i] = completar;
        } else {
            // há vetor. copia os valores e completa o que faltar com vazio.
            for (int i = 0; i < tamanho; i ++)
                retorno[i] = (i + inicio >= 0 && i + inicio < vetor.length)
                               ? vetor[i + inicio]
                               : completar;
        }
        return retorno;
    }

    /**
     * Obtém o código RGBA de determinada cor.
     * @param A Canal alfa.
     * @param R Canal vermelho.
     * @param G Canal verde.
     * @param B Canal azul.
     * @return Código RGBA da cor.
     */
    public static int obterCor(byte A, byte R, byte G, byte B) {
        return (A << 24) | (R << 16) | (G << 8) | B;
    }

    /**
     * Obtém a extensão de um arquivo a partir de seu nome. Retorna nulo se não houver extensão.
     * @param nome Nome do arquivo cuja extensão deve ser obtida.
     */
    public static String obterExtensao(String nome) {
        int indice_extensao = nome.lastIndexOf('.');
        int indice_limite = nome.length() - 1;
        if (indice_extensao > 0 &&  indice_extensao < indice_limite)
            return nome.substring(indice_extensao + 1).toLowerCase();
        return null;
    }

    /**
     * Obtém a extensão de um arquivo a partir de seu descritor de arquivo. Retorna nulo se não houver extensão.
     * @param arquivo Arquivo cuja extensão deve ser obtida.
     */
    public static String obterExtensao(File arquivo) {;
        return obterExtensao(arquivo.getName());
    }
}
