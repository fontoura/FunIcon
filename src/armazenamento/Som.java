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

package armazenamento;

import audio.*;
import auxiliar.Auxiliar;

import java.io.*;

import javax.sound.sampled.*;

/**
 * Classe representante de um som amostrado.
 * @author Felipe Michels Fontoura
 */
public class Som implements ConstantesArmazenamento {
    /**
     * Amostras do som (sinalizadas).
     */
    private byte[] amostras;

    /**
     * Cria um descritor de som vazio.
     */
    public Som() {
        this(null, 0, SOM_AMOSTRAS, true);
    }

    /**
     * Cria um descritor de som a partir de uma série de amostras.
     * @param amostras Amostras de som.
     */
    public Som(byte[] amostras, boolean sinalizado) {
        this(amostras, 0, (amostras == null) ? SOM_AMOSTRAS : amostras.length, sinalizado);
    }

    /**
     * Cria um descritor de som a partir de uma série de amostras.
     * @param amostras Amostras de som.
     * @param inicio Amostra inicial.
     * @param tamanho Número total de amostras.
     */
    public Som(byte[] amostras, int inicio, int tamanho, boolean sinalizado) {
        this.amostras = Auxiliar.copiarTrecho(amostras, inicio, tamanho, AMOSTRA_VAZIA);
        if (!sinalizado)
            for (int i = 0; i < amostras.length; i ++) {
                amostras[i] = (byte) (0x80 ^ amostras[i]);
            }
    }

    /**
     * Cria um descritor de som a partir de outro descritor de som existente.
     * @param som Descritor de som a copiar.
     */
    public Som(Som som) {
        this(som.amostras, 0, som.amostras.length, true);
    }

    /**
     * Obtém um nvo descritor de som, relativo a um trecho do som original.
     * @param inicio Amostra inicial.
     * @param tamanho Número total de amostras.
     * @return Novo descritor de som, relativo a um trecho do som original.
     */
    public Som obterTrecho(int inicio, int tamanho) {
        return new Som(amostras, inicio, tamanho, true);
    }

    /**
     * Obtém a contagem de amostras desse descritor de som.
     * @return Contagem de amostras desse descritor de som.
     */
    public int contagemAmostras() {
        return amostras.length;
    }

    /**
     * Retorna um fluxo de áudio relativo a esse descritor de som.
     * @return Fluxo de áudio relativo a esse descritor.
     */
    public AudioInputStream obterAudio() {
        // define o formato de áudio.
        AudioFormat formato_audio = obterFormatoAudio();

        // cria o fluxo de bytes.
        ByteArrayInputStream fluxo_bytes = new ByteArrayInputStream(amostras);

        // cria o fluxo de áudio.
        // lembrete: "frame" é uma amostra de cada canal; para o caso particular
        //           dessa aplicação, cada frame equivale a uma amostra.
        AudioInputStream fluxo_audio = new AudioInputStream(
          /*   fluxo de bytes: */ fluxo_bytes,
          /* formato de áudio: */ formato_audio,
          /* número de frames: */ amostras.length
        );

        // retorna o fluxo de áudio.
        return fluxo_audio;
    }

    /**
     * Retorna um objeto executável para a criação de uma thread de reprodução de som.
     * @return Objeto executável para reprodução do som.
     */
    public Reprodutor criarReprodutor() {
        return new Reprodutor(obterAudio());
    }

    /**
     * Copia as amostras de áudio para um vetor externo.
     * @param destino Vetor de destino.
     */
    public void extrairAmostras(byte[] destino, boolean sinalizado) {
        if (destino == null) return;
        int l = amostras.length;
        if (l > destino.length) l = destino.length;
        if (sinalizado) {
            for (int i = 0; i < l; i ++)
                destino[i] = amostras[i];
        } else {
            for (int i = 0; i < l; i ++)
                destino[i] = (byte) (amostras[i] ^ 0x80);
        }
    }

    /**
     * Lê um objeto descritor de som de um fluxo de entrada.
     * @param fluxo Fluxo de entrada.
     * @return Descritor de som construído.
     * @throws IOException Caso hava problema na leitura dos dados.
     */
    public static Som lerSom(InputStream fluxo) throws IOException {
        // cria um fluxo de dados.
        DataInputStream fluxo_dados = new DataInputStream(fluxo);

        // lê o tamanho do som.
        int tamanho = fluxo_dados.readUnsignedShort();

        // lê as amostras não sinalizadas (e as converte para sinalizadas).
        byte[] amostras = new byte[tamanho];
        for (int i = 0; i < tamanho; i ++)
            amostras[i] = (byte)(fluxo_dados.readUnsignedByte() ^ 0x80);

        // retorna o descritor de som.
        return new Som(amostras, true);
    }

    /**
     * Escreve o objeto descritor de som em um fluxo de saída.
     * @param fluxo Fluxo de saída.
     * @throws IOException Caso hava problema na leitura dos dados.
     */
    public void escreverSom(OutputStream fluxo) throws IOException {
        // cria um fluxo de dados.
        DataOutputStream fluxo_dados = new DataOutputStream(fluxo);

        // escreve o tamanho do som.
        fluxo_dados.writeShort(amostras.length);

        // escreve as amostras não sinalizadas (as converte de sinalizadas).
        for (int i = 0; i < amostras.length; i ++)
            fluxo_dados.write((byte)(amostras[i] ^ 0x80));
    }

    /**
     * Retorna um descritor do formato de áudio utilizado.
     * @return Descritor do formato de áudio.
     */
    public static AudioFormat obterFormatoAudio() {
        AudioFormat formato_audio = new AudioFormat(
          /*           taxa de amostragem: */ 8000,
          /* tamanho da amostra (em bits): */ 8,
          /*              canais de áudio: */ 1,
          /*                   sinalizado: */ true,
          /*                    BigEndian: */ false
        );
        return formato_audio;
    }

    /**
     * Exporta o áudio para um arquivo wave.
     * @param arquivo Descritor de arquivo de destino.
     * @throws IOException Caso haja algum problema na escrita dos dados.
     */
    public void exportarArquivo(File arquivo) throws IOException {
        AudioSystem.write(obterAudio(), AudioFileFormat.Type.WAVE, arquivo);
    }

    /**
     * Tenta importar o áudio de um arquivo. Suporta-se apenas o formato wave.
     * @param arquivo Arquivo de áudio a ler.
     * @return Objeto descritor de som relativo ao som desse arquivo.
     * @throws UnsupportedAudioFileException Caso o formato de áudio não seja suportado.
     * @throws IOException Caso haja um erro na leitura do arquivo.
     */
    public static Som importarArquivo(File arquivo) throws UnsupportedAudioFileException, IOException {
        // abre um arquivo de áudio.
        AudioInputStream entrada_audio = AudioSystem.getAudioInputStream(arquivo);

        // obtém o formato do áudio.
        AudioFormat formato_audio = entrada_audio.getFormat();

        // verifica se o formato é válido.
        if (formato_audio.getFrameRate() == AudioSystem.NOT_SPECIFIED)
            throw new UnsupportedAudioFileException("Frame rate inválido.");
        if (formato_audio.getFrameSize() == AudioSystem.NOT_SPECIFIED)
            throw new UnsupportedAudioFileException("Frame size inválido.");
        if (formato_audio.getChannels() == AudioSystem.NOT_SPECIFIED)
            throw new UnsupportedAudioFileException("Número de canais inválido.");

        // obtém o número de canais.
        int canais = formato_audio.getChannels();

        // verifica se o tamanho das amostras é múltiplo de 8.
        int tamanho_amostra = formato_audio.getFrameSize() / formato_audio.getChannels();
        System.out.println(tamanho_amostra);
        if (tamanho_amostra != 1 && tamanho_amostra != 2 && tamanho_amostra != 3 && tamanho_amostra != 4)
            throw new UnsupportedAudioFileException();

        // verifica se não há bytes desalinhados.
        if (formato_audio.getFrameSize() != canais * tamanho_amostra)
            throw new UnsupportedAudioFileException();

        // verifica se o som era originalmente sinalizado.
        boolean originalmente_sinalizado = formato_audio.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED;

        // máscara a aplicar às amostras, caso fossem originalmente sinalizadas.
        int mascara_sinalizado = originalmente_sinalizado ? 0x00 : 0x80;

        // determina o número de amostras do som.
        long tamanho = (long) ((entrada_audio.getFrameLength() * Som.SOM_AMOSTRAGEM) / formato_audio.getFrameRate());
        if (tamanho > SOM_MAX_AMOSTRAS) tamanho = SOM_MAX_AMOSTRAS;

        // determina quais os frames do som original que devem ser lidos
        long frame_inicial = 0;
        long frame_final = (long) ((tamanho * formato_audio.getFrameRate()) / Som.SOM_AMOSTRAGEM);
        // System.out.println(frame_inicial + ", " + frame_final);

        // cria o vetor de amostras.
        byte[] amostras = new byte[(int) tamanho];
        byte[] frame = new byte[formato_audio.getFrameSize()];

        // lê os frames e converte em amostras de 8 KHz.
        for (int i = 0; i < amostras.length; i ++) {
            long frame_local_inicial = frame_inicial + ((frame_final - frame_inicial) * i) / amostras.length;
            long frame_local_final = frame_inicial + ((frame_final - frame_inicial) * (i + 1)) / amostras.length - 1;
            long acumulador = 0;
            for (long j = frame_local_inicial; j <= frame_local_final; j ++) {
                // lê um frame.
                int leitor = entrada_audio.read(frame);
                while (leitor < frame.length)
                    leitor += entrada_audio.read(frame, leitor, frame.length - leitor);

                // soma o valor das amostras dos canais no acumulador.
                if (tamanho_amostra == 1) {
                    for (int canal = 0; canal < canais; canal ++) {
                        acumulador = (byte)(0xFF & frame[canal]) ^ mascara_sinalizado;
                    }
                } else if (tamanho_amostra == 2) {
                    for (int canal = 0; canal < canais; canal ++) {
                        acumulador += ((byte)((0xFF & frame[2*canal + ((formato_audio.isBigEndian()) ? 0 : 1)]) ^ mascara_sinalizado) << 8)
                                     | (byte)((0xFF & frame[2*canal + ((formato_audio.isBigEndian()) ? 1 : 0)]) << 0);
                    }
                } else if (tamanho_amostra == 3) {
                    for (int canal = 0; canal < canais; canal ++) {
                        acumulador += (((byte)(0xFF & frame[3*canal + ((formato_audio.isBigEndian()) ? 0 : 2)]) ^ mascara_sinalizado) << 16)
                                     | ((byte)(0xFF & frame[3*canal + 1]) <<  8)
                                     | ((byte)(0xFF & frame[3*canal + ((formato_audio.isBigEndian()) ? 2 : 0)]) <<  0);
                    }
                } else if (tamanho_amostra == 4) {
                    for (int canal = 0; canal < canais; canal ++) {
                        acumulador += (((byte)(0xFF & frame[2*canal + ((formato_audio.isBigEndian()) ? 0 : 3)]) ^ mascara_sinalizado) << 24)
                                     | ((byte)(0xFF & frame[2*canal + ((formato_audio.isBigEndian()) ? 1 : 2)]) << 16)
                                     | ((byte)(0xFF & frame[2*canal + ((formato_audio.isBigEndian()) ? 2 : 1)]) <<  8)
                                     | ((byte)(0xFF & frame[2*canal + ((formato_audio.isBigEndian()) ? 3 : 0)]) <<  0);
                    }
                }
            }
            // divide o acumulador (efetivamente, tira a média).
            acumulador /= canais * (1 + frame_local_final - frame_local_inicial);

            // ajusta os bytes do acumulador.
            acumulador = acumulador >> (tamanho_amostra * 8 - 8);

            // define a amostra atual
            amostras[i] = (byte) acumulador;
        }

        // cria o objeto de som.
        return new Som(amostras, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean equals(Object obj) {
        if (obj instanceof Som) {
            Som som = (Som) obj;
            if (som.amostras.length != amostras.length) return false;
            for (int i = 0; i < amostras.length; i ++)
                if (som.amostras[i] != amostras[i])
                    return false;
            return true;
        } else return false;
    }
}
