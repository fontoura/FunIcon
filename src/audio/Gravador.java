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

package audio;

import java.util.*;
import javax.sound.sampled.*;

import armazenamento.ConstantesArmazenamento;
import armazenamento.Som;

/**
 * Classe executável para a criação de threads de gravação de sons.
 * @author Felipe Michels Fontoura
 */
public class Gravador extends Thread {
    private static AudioFormat formato_audio_padrao = new AudioFormat(
      /*           taxa de amostragem: */ ConstantesArmazenamento.SOM_AMOSTRAGEM,
      /* tamanho da amostra (em bits): */ ConstantesArmazenamento.SOM_BITS_AMOSTRA,
      /*              canais de áudio: */ 1,
      /*                   sinalizado: */ ConstantesArmazenamento.SOM_SINALIZADO,
      /*                    BigEndian: */ false
    );

    /**
     * Máximo número de frames que podem ser gravados.
     */
    private static final int MAXIMO_FRAMES_PADRAO = 6 * ConstantesArmazenamento.SOM_AMOSTRAS;

    /**
     * Máximo de frames lidos por vez.
     */
    private static final int MAXIMO_POR_VEZ = 200;

    /**
     * Linha de áudio para gravação.
     */
    private TargetDataLine linha_audio;

    /**
     * Tamanho do buffer de gravação.
     */
    private int tamanho_buffer;

    /**
     * Ouvintes de evento.
     */
    private List<OuvinteGravacao> ouvintes_evento;

    /**
     * Frames gravados.
     */
    private byte buffer_gravacao[];

    /**
     * Posição gravada atual.
     */
    private int posicao_atual;

    /**
     * Se a gravação está em execução.
     */
    private boolean executando;

    /**
     * Instancia um objeto de gravação de som.
     */
    public Gravador() {
        this(MAXIMO_FRAMES_PADRAO);
    }

    /**
     * Instancia um objeto de gravação de som.
     * @param maximo_frames_buffer Número máximo de frames no buffer.
     */
    public Gravador(int maximo_frames_buffer) {
        tamanho_buffer = maximo_frames_buffer * formato_audio_padrao.getFrameSize();
        if (tamanho_buffer <= 0)
            tamanho_buffer = MAXIMO_FRAMES_PADRAO;
        executando = false;
        ouvintes_evento = new Vector<OuvinteGravacao>(1, 1);
    }

    /**
     * Adiciona um ouvinte de evento nesse gravador.
     * @param ouvinte Ouvinte de evento.
     */
    public void adicionarOuvinteEvento(OuvinteGravacao ouvinte) {
        ouvintes_evento.add(ouvinte);
    }

    /**
     * Remove um ouvinte de evento desse gravador.
     * @param ouvinte Ouvinte de evento.
     * @return Se a remoção ocorreu com sucesso.
     */
    public boolean removerOuvinteEvento(OuvinteGravacao ouvinte) {
        return ouvintes_evento.remove(ouvinte);
    }

    /**
     * Obtém o objeto descritor do som gravado por esse gravador.
     * @return Objeto descritor do som gravado.
     */
    public Som obterSom() {
        return new Som(buffer_gravacao, 0, posicao_atual, true);
    }


    /**
     * Inicia o processo de parar a gravação forçadamente. A gravação não parará logo que esse método for chamado.
     */
    public void parar() {
        executando = false;
    }

    /**
     * Começa a gravar o som do fluxo de áudio.
     */
    @Override public void run() {
        try {
            // cria a linha de dados.
            DataLine.Info info_linha_dados = new DataLine.Info(
              /* classe da linha de dados: */ TargetDataLine.class,
              /*         formato de áudio: */ formato_audio_padrao
            );
            linha_audio = (TargetDataLine) AudioSystem.getLine(info_linha_dados);

            // abre a linha de dados.
            linha_audio.open(formato_audio_padrao);
            linha_audio.start();
            executando = true;

            // grava o som
            posicao_atual = 0;
            buffer_gravacao = new byte[tamanho_buffer];
            while (executando) {
                // lê dados do fluxo de áudio para o buffer.
                int diff = linha_audio.read(buffer_gravacao, posicao_atual, Math.min(buffer_gravacao.length - posicao_atual, MAXIMO_POR_VEZ));
                posicao_atual = posicao_atual + diff;

                // se foi possível ler dados para o buffer, reproduz.
                if (posicao_atual >= buffer_gravacao.length)
                    executando = false;
            }

            // espera até terminar de gravar.
            linha_audio.close();

            // dispara o evento "som terminado"
            for (OuvinteGravacao ouvinte : ouvintes_evento)
                if (ouvinte != null) ouvinte.gravacaoTerminou();
        } catch (Exception e) {
            // dispara o evento "reprodução falhou"
            for (OuvinteGravacao ouvinte : ouvintes_evento)
                if (ouvinte != null) ouvinte.gravacaoFalhou(e);
        };
    }
}
