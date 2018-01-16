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
 * Classe que representa uma faixa de valores.
 * @author Felipe Michels Fontoura
 * @param <tipo> Tipo comparável dos valores.
 */
@SuppressWarnings("rawtypes")
public class Faixa<tipo extends Comparable>{
    /**
     * Primeiro valor da faixa.
     */
    private tipo primeiro = null;

    /**
     * Último valor da faixa.
     */
    private tipo ultimo = null;

    /**
     * Cria uma representação de uma faixa de valores.
     * @param primeiro Primeiro valor dessa faixa.
     * @param ultimo Último valor dessa faixa.
     */
    public Faixa(tipo primeiro, tipo ultimo) {
        this.primeiro = primeiro;
        this.ultimo = ultimo;
    }

    /**
     * Verifica se determinado valor está contido nessa faixa de valores.
     * @param valor Valor a testar.
     * @return Se o valor está nessa faixa de valores.
     */
    @SuppressWarnings("unchecked")
    public boolean contem(tipo valor) {
        return primeiro.compareTo(valor) <= 0 && ultimo.compareTo(valor) >= 0;
    }

    /**
     * Define o primeiro valor dessa faixa de valores.
     * @param primeiro Novo primeiro valor dessa faixa de valores.
     * @return Antigo primeiro valor dessa faixa de valores.
     */
    public tipo definirPrimeiro(tipo primeiro) {
        tipo retorno = this.primeiro;
        this.primeiro = primeiro;
        return retorno;
    }

    /**
     * Define o último valor dessa faixa de valores.
     * @param ultimo Novo último valor dessa faixa de valores.
     * @return Antigo último valor dessa faixa de valores.
     */
    public tipo definirUltimo(tipo ultimo) {
        tipo retorno = this.ultimo;
        this.ultimo = ultimo;
        return retorno;
    }

    /**
     * Obtém o primeiro valor dessa faixa de valores.
     * @return Primeiro valor dessa faixa de valores.
     */
    public tipo obterPrimeiro() {
        return primeiro;
    }

    /**
     * Obtém o último valor dessa faixa de valores.
     * @return Último valor dessa faixa de valores.
     */
    public tipo obterUltimo() {
        return ultimo;
    }
}
