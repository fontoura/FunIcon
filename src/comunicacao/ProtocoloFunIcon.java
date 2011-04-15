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

package comunicacao;

import java.io.ByteArrayOutputStream;

import java.util.List;
import java.util.Vector;

import armazenamento.Botao;
import armazenamento.ConstantesArmazenamento;

/**
 * Gerenciador do protocolo de comunicação com a prancha.
 * @author Felipe Michels Fontoura
 *
 */
public class ProtocoloFunIcon {
    /**
     * Lista de ouvintes de evento de protocolo.
     */
    private List<OuvinteProtocolo> ouvintes_protocolo;

    /**
     * Comunicador utilizado para conectar-se à prancha.
     */
    private Comunicador comunicador;

    /**
     * Ouvinte de eventos da thread de comunicacão.
     */
    private OuvinteThread ouvinte_thread;

    /**
     * Thread de comunicação.
     */
    private ThreadProtocolo thread_comunicacao;

    /**
     * Cria um gerenciador de protocolo de comunicação sobre um dado comunicador.
     * @param comunicador Comunicador a usar.
     */
    public ProtocoloFunIcon(Comunicador comunicador) {
        // define o comunicador.
        this.comunicador = comunicador;

        // cria o vetor de ouvintes de protocolo.
        ouvintes_protocolo = new Vector<OuvinteProtocolo>();

        // define que não há thread de comunicação ativa.
        thread_comunicacao = null;

        // cria o ouvinte de eventos da thread de comunicação.
        ouvinte_thread = new OuvinteThread() {
            @Override public void terminouExecucao() {
                if (thread_comunicacao.comunicacaoFalhou())
                    evtComunicacaoAbortada();
                else if (thread_comunicacao.enviouDados())
                    evtComunicacaoRealizada();
                else
                    evtComunicacaoRecusada();
            }
            @Override public void avancouExecucao(double parcela) {
                evtAvancouExecucaoThread(parcela);
            }
        };
    }

    /**
     * Tratador do evento de a thread de comunicação avançar até determinada
     * parcela de sua operação.
     * @param parecela Porcentagem da operação da thread que foi completa.
     */
    private void evtAvancouExecucaoThread(double parcela) {
        for (OuvinteProtocolo ouvinte : ouvintes_protocolo)
            ouvinte.protocoloAvancou(parcela);
    }

    /**
     * Tratador do evento de a comunicação com a prancha ter sido abortada.
     */
    private void evtComunicacaoAbortada() {
        thread_comunicacao = null;
        for (OuvinteProtocolo ouvinte : ouvintes_protocolo)
            ouvinte.protocoloAbortou();
    }

    /**
     * Tratador do evento de a comunicação com a prancha ter sido realizada
     * com sucesso.
     */
    private void evtComunicacaoRealizada() {
        thread_comunicacao = null;
        for (OuvinteProtocolo ouvinte : ouvintes_protocolo)
            ouvinte.protocoloEnviou();
    }

    /**
     * Tratador do evento de a comunicação com a prancha ter sido recusada.
     */
    private void evtComunicacaoRecusada() {
        thread_comunicacao = null;
        for (OuvinteProtocolo ouvinte : ouvintes_protocolo)
            ouvinte.protocoloNegou();
    }

    /**
     * Adiciona um ouvinte de evento nesse gerente de protocolo.
     * @param ouvinte Ouvinte de evento.
     */
    public void adicionarOuvinteEvento(OuvinteProtocolo ouvinte) {
        ouvintes_protocolo.add(ouvinte);
    }

    /**
     * Remove um ouvinte de evento desse gerente de protocolo.
     * @param ouvinte Ouvinte de evento.
     * @return Se a remoção ocorreu com sucesso.
     */
    public boolean removerOuvinteEvento(OuvinteProtocolo ouvinte) {
        return ouvintes_protocolo.remove(ouvinte);
    }

    /**
     * Tenta iniciar a comunicação da prancha para enviar um determinado botão para ela.
     * @param numero Número do botão a enviar.
     * @param botao Descritor do botão a enviar.
     * @return Se a comunicação foi iniciada com sucesso.
     */
    public synchronized boolean enviarBotao(int numero, Botao botao) {
        // verifica se está disponível para iniciar a comunicação.
        if (thread_comunicacao == null) {
            ByteArrayOutputStream fluxo = new ByteArrayOutputStream(ConstantesArmazenamento.ICONE_BYTES + ConstantesArmazenamento.SOM_AMOSTRAS);
            byte[] dados_botao;
            try {
                botao.obterIcone().escreverIcone(fluxo);
                botao.obterSom().escreverSom(fluxo);
                dados_botao = fluxo.toByteArray();
                thread_comunicacao = new ThreadProtocolo(comunicador, dados_botao, numero);
                thread_comunicacao.adicionarOuvinteEvento(ouvinte_thread);
                thread_comunicacao.start();
                return true;
            } catch (Exception e) {
                // houve um problema na escrita dos dados... aborta a comunicação.
                return false;
            }
        } else return false;
    }

    /**
     * Desconecta o gerente de protocolo. Faz com que o comunicador também seja desconectado.
     */
    public void desconectar() {
        comunicador.desconectar();
    }
}
