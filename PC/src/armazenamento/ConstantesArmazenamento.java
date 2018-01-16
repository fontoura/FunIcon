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

/**
 * Interface com constantes relativas ao armazenamento de dados.
 * @author Felipe Michels Fontoura
 */
public interface ConstantesArmazenamento {
    /**
     * Extensão dos arquivos de prancha.
     */
    String extensao_prancha = "fip";

    /**
     * Extensão dos arquivos de botão.
     */
    String extensao_botao = "fib";

    /**
     * Número de amostras por segundo do som.
     */
    int SOM_AMOSTRAGEM = 8000;

    /**
     * Número de bits por amostra de som.
     */
    int SOM_BITS_AMOSTRA = 8;

    /**
     * Número de amostras por segundo do som.
     */
    int SOM_SEGUNDOS = 4;

    /**
     * Define se o som é sinalizado.
     */
    boolean SOM_SINALIZADO = true;

    /**
     * Número de linhas de um ícone.
     */
    int ICONE_LINHAS = 8;

    /**
     * Número de colunas de um ícone.
     */
    int ICONE_COLUNAS = 8;

    /**
     * Número de bytes por icone
     */
    int ICONE_BYTES = (ICONE_COLUNAS * ICONE_LINHAS) >> 3;

    /**
     * Número de amostras de um som.
     */
    int SOM_AMOSTRAS = SOM_AMOSTRAGEM * SOM_SEGUNDOS - ICONE_BYTES;

    /**
     * Número máximo de amostras de um som
     */
    int SOM_MAX_AMOSTRAS = SOM_AMOSTRAS * 6;

    /**
     * Amostra de som vazia.
     */
    byte AMOSTRA_VAZIA = 0;
}
