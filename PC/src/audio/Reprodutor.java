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

package audio;

import java.util.*;
import javax.sound.sampled.*;

/**
 * Classe executável para a criação de threads de reprodução de sons.
 * @author Felipe Michels Fontoura
 */
public class Reprodutor extends Thread {
    /**
     * Número máximo de frames no buffer de reprodução.
     */
    private static final int FRAMES_BUFFER_PADRAO = 1024;

    /**
     * Fluxo de áudio para reprodução.
     */
    private AudioInputStream fluxo_audio;

    /**
     * Tamanho do buffer de execução.
     */
    private int tamanho_buffer;

    /**
     * Ouvintes de evento.
     */
    private List<OuvinteReproducao> ouvintes_evento;

    /**
     * Se a reprodução está em execução.
     */
    private boolean executando;

    /**
     * Instancia um objeto de som a partir de um fluxo de áudio.
     * @param fluxo_audio Fluxo de áudio a reproduzir.
     */
    public Reprodutor(AudioInputStream fluxo_audio) {
        this(fluxo_audio, FRAMES_BUFFER_PADRAO);
    }

    /**
     * Instancia um objeto de som a partir de um fluxo de áudio.
     * @param fluxo_audio Fluxo de áudio a reproduzir.
     * @param frames_buffer Número de frames no buffer.
     */
    public Reprodutor(AudioInputStream fluxo_audio, int frames_buffer) {
        this.fluxo_audio = fluxo_audio;
        tamanho_buffer = frames_buffer * fluxo_audio.getFormat().getFrameSize();
        if (tamanho_buffer <= 0)
            tamanho_buffer = FRAMES_BUFFER_PADRAO;
        executando = false;
        ouvintes_evento = new Vector<OuvinteReproducao>(1, 1);
    }

    /**
     * Adiciona um ouvinte de evento nesse reprodutor.
     * @param ouvinte Ouvinte de evento.
     */
    public void adicionarOuvinteEvento(OuvinteReproducao ouvinte) {
        ouvintes_evento.add(ouvinte);
    }

    /**
     * Remove um ouvinte de evento desse reprodutor.
     * @param ouvinte Ouvinte de evento.
     * @return Se a remoção ocorreu com sucesso.
     */
    public boolean removerOuvinteEvento(OuvinteReproducao ouvinte) {
        return ouvintes_evento.remove(ouvinte);
    }

    /**
     * Inicia o processo de parar a reprodução forçadamente. A reprodução não parará logo que esse método for chamado.
     */
    public void parar() {
        executando = false;
    }

    /**
     * Reproduz o som do fluxo de áudio.
     */
    @Override public void run() {
        try {
            // cria a linha de dados.
            DataLine.Info info_linha_dados = new DataLine.Info(
              /* classe da linha de dados: */ SourceDataLine.class,
              /*         formato de áudio: */ fluxo_audio.getFormat()
            );
            SourceDataLine linha_dados = (SourceDataLine) AudioSystem.getLine(info_linha_dados);

            // reinicia o fluxo de áudio
            fluxo_audio.reset();

            // abre a linha de dados.
            linha_dados.open(fluxo_audio.getFormat());
            linha_dados.start();
            executando = true;

            // executa o som
            byte buffer_reproducao[] = new byte[tamanho_buffer];
            int bytes_no_buffer = 0;
            while (executando && (bytes_no_buffer != -1)) {
                // lê dados do fluxo de áudio para o buffer.
                bytes_no_buffer = fluxo_audio.read(buffer_reproducao, 0, buffer_reproducao.length);

                // se foi possível ler dados para o buffer, reproduz.
                if (bytes_no_buffer > 0)
                    linha_dados.write(buffer_reproducao, 0, bytes_no_buffer);
            }
            executando = false;

            // espera até terminar de reproduzir.
            linha_dados.drain();

            // fecha a linha de dados.
            linha_dados.close();

            // dispara o evento "som terminado"
            for (OuvinteReproducao ouvinte : ouvintes_evento)
                if (ouvinte != null) ouvinte.reproducaoTerminou();
        } catch (Exception e) {
            // dispara o evento "reprodução falhou"
            for (OuvinteReproducao ouvinte : ouvintes_evento)
                if (ouvinte != null) ouvinte.reproducaoFalhou(e);
        };
    }
}
