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

import java.util.List;
import java.util.Vector;

/**
 * Thread de comunicação usando o protocolo.
 * @author Felipe Michels Fontoura
 */
public class ThreadProtocolo extends Thread {
    /**
     * Identificador de tipo associado à mensagem de início de comunicação (handshake).
     */
    private static final int TIPO_PC_INICIO = 0x03;

    /**
     * Identificador de tipo associado à mensagem de envio de segmento.
     */
    private static final int TIPO_PC_DADOS = 0x0C;

    /**
     * Identificador de tipo associado à mensagem de envio de segmento.
     */
    private static final int TIPO_PC_ABORTAR = 0x0F;

    /**
     * Identificador de tipo associado à mensagem da prancha permitir o envio dos dados.
     */
    private static final int TIPO_PRANCHA_PERMISSAO = 0xB3;

    /**
     * Identificador de tipo associado à mensagem da prancha permitir o envio dos dados.
     */
    private static final int TIPO_PRANCHA_NEGACAO = 0xBC;

    /**
     * Identificador de tipo associado à mensagem da prancha confirmar o recebimento de dados.
     */
    private static final int TIPO_PRANCHA_CONFIRMACAO = 0xB6;

    /**
     * Identificador de tipo associado à mensagem da prancha abortar a comunicação.
     */
    private static final int TIPO_PRANCHA_ABORTAR = 0xBF;

    /**
     * Identificador de tipo associado à mensagem da prancha ter gravado os dados com sucesso.
     */
    private static final int TIPO_PRANCHA_GRAVADO = 0xB0;

    /**
     * Espera máxima pelo início do recebimento de uma resposta.
     */
    private static final int ESPERA_MAXIMA_POR_RESPOSTA = 2000; // 2 s

    /**
     * Espera máxima pelo início do recebimento de confirmação de gravação.
     */
    private static final int ESPERA_MAXIMA_POR_RESPOSTA_GRAVACAO = 30000; // 30 s

    /**
     * Espera máxima entre o recebimento de dois caracteres.
     */
    private static final int ESPERA_MAXIMA_ENTRE_CARACTERES = 20; // 20 ms

    /**
     * Lista de ouvintes de thread associados a essa thread
     */
    List<OuvinteThread> ouvintes;

    /**
     * Flag indicando se o envio ocorreu com sucesso.
     */
    private boolean enviou;

    /**
     * Flag indicando se o houve falha na comunicação.
     */
    private boolean comunicacao_falhou;

    /**
     * Valor indicando o tipo da mensagem recebida.
     */
    private int recebida_tipo;

    /**
     * Valor indicando o tamanho da mensagem recebida.
     */
    private int recebida_tamanho;

    /**
     * Comunicador a utilizar para realizar o envio.
     */
    private Comunicador comunicador;

    /**
     * Dados crus (vetor de bytes) do botão a enviar.
     */
    private byte[] dados_botao;

    /**
     * Número do botão a enviar.
     */
    private int numero_botao;

    /**
     * Instancia uma nova thread de comunicação.
     * @param comunicador Comunicador a utilizar.
     * @param dados_botao Vetor de dados do botão a enviar.
     * @param numero_botao Número do botão a enviar.
     */
    public ThreadProtocolo(Comunicador comunicador, byte[] dados_botao, int numero_botao) {
        this.comunicador = comunicador;
        this.dados_botao = dados_botao;
        this.numero_botao = numero_botao;
        ouvintes = new Vector<OuvinteThread>();
        enviou = false;
    }

    @Override public void run() {
        comunicacao_falhou = false;
        enviou = false;
        enviou = enviarBotao();
        dispararTermino();
        return;
    }

    /**
     * Adiciona um ouvinte de evento nessa thread de comunicação.
     * @param ouvinte Ouvinte de evento.
     */
    public void adicionarOuvinteEvento(OuvinteThread ouvinte) {
        ouvintes.add(ouvinte);
    }

    /**
     * Remove um ouvinte de evento dessa thread de comunicação.
     * @param ouvinte Ouvinte de evento.
     * @return Se a remoção ocorreu com sucesso.
     */
    public boolean removerOuvinteEvento(OuvinteThread ouvinte) {
        return ouvintes.remove(ouvinte);
    }

    /**
     * Tenta enviar um botão para a prancha usando o comunicador.
     * @return Se o envio ocorreu com sucesso.
     */
    private boolean enviarBotao() {
        int dados_enviados = 0;
        int dados_por_vez = 256;
        receberMensagem(ESPERA_MAXIMA_POR_RESPOSTA);
        comunicacao_falhou = false;
        enviarMensagemInicio();
        receberMensagem(ESPERA_MAXIMA_POR_RESPOSTA);
        if (comunicacao_falhou) return false;
        System.out.println("PRANCHA RESPONDEU: " + Integer.toHexString(0xFF & recebida_tipo));
        if (recebida_tipo == TIPO_PRANCHA_NEGACAO) {
            return false;
        } else if (recebida_tipo == TIPO_PRANCHA_ABORTAR) {
            comunicacao_falhou = true;
            return false;
        } else if (recebida_tipo != TIPO_PRANCHA_PERMISSAO) {
            comunicacao_falhou = true;
            enviarMensagemAbortar();
            return false;
        }
        while (dados_enviados < dados_botao.length) {
            for (OuvinteThread ouvinte : ouvintes)
                ouvinte.avancouExecucao(((double)dados_enviados)/dados_botao.length);
            enviarMensagemDados(dados_enviados, dados_por_vez);
            System.out.println("ENVIOU DADOS");
            receberMensagem(ESPERA_MAXIMA_POR_RESPOSTA);
            System.out.println("PRANCHA RESPONDEU: " + Integer.toHexString(0xFF & recebida_tipo));
            if (comunicacao_falhou) return false;
            if (recebida_tipo != TIPO_PRANCHA_CONFIRMACAO) {
                if (recebida_tipo != TIPO_PC_ABORTAR)
                    enviarMensagemAbortar();
                System.out.println("ENVIOU MENSAGEM FINAL");
                return false;
            }
            dados_enviados += dados_por_vez;

        }
        enviarMensagemFinal();
        System.out.println("ENVIOU MENSAGEM FINAL");
        receberMensagem(ESPERA_MAXIMA_POR_RESPOSTA_GRAVACAO + ESPERA_MAXIMA_POR_RESPOSTA);
        if (comunicacao_falhou) return false;
        System.out.println("PRANCHA RESPONDEU: " + Integer.toHexString(0xFF & recebida_tipo));
        if (recebida_tipo == TIPO_PRANCHA_GRAVADO) {
            return true;
        } else {
            if (recebida_tipo != TIPO_PC_ABORTAR)
                enviarMensagemAbortar();
            return false;
        }
    }

    /**
     * Tenta receber uma mensagem pelo comunicador.
     * @param maximo Espera máxima pelo primeiro carácter da mensagem.
     */
    private void receberMensagem(int maximo) {
        int checksum = 0;
        int checksum_esperado = 0;
        byte recebido;
        try {
            recebido = comunicador.receberByte(maximo);
            recebida_tipo = 0xFF & recebido;
            checksum = 0xFF & recebido;
        } catch (Exception e) {
            e.printStackTrace();

            comunicacao_falhou = true;
            return;
        }
        try {
            recebido = comunicador.receberByte(ESPERA_MAXIMA_ENTRE_CARACTERES);
            recebida_tamanho = 0x00FF & recebido;
            checksum += 0xFF & recebido;
            recebido = comunicador.receberByte(ESPERA_MAXIMA_ENTRE_CARACTERES);
            recebida_tamanho |= 0xFF00 & (recebido << 8);
            checksum += 0xFF & recebido;

            for (int i = 0; i < recebida_tamanho; i ++)
                checksum += 0xFF & comunicador.receberByte(ESPERA_MAXIMA_ENTRE_CARACTERES);

            recebido = comunicador.receberByte(ESPERA_MAXIMA_ENTRE_CARACTERES);
            checksum_esperado = 0xFF00 & (recebido << 8);
            recebido = comunicador.receberByte(ESPERA_MAXIMA_ENTRE_CARACTERES);
            checksum_esperado |= 0xFF00 & (recebido << 8);

            /*if (checksum_esperado != checksum) {
                System.out.println(checksum_esperado + " " + checksum);
                throw new Exception();
            }*/
        } catch (Exception e) {
            e.printStackTrace();

            enviarMensagemAbortar();
            comunicacao_falhou = true;
            return;
        }
    }

    private void dispararTermino() {
        for (OuvinteThread o : ouvintes) o.terminouExecucao();
    }

    /**
     * Retorna flag indicando se a comunicação falhou.
     * @return Flag indicando se a comunicação falhou.
     */
    public boolean comunicacaoFalhou() {
        return comunicacao_falhou;
    }

    /**
     * Retorna flag indicando se houve envio dos dados.
     * @return Flag indicando se houve envio dos dados.
     */
    public boolean enviouDados() {
        return enviou;
    }

    /**
     * Tenta enviar uma mensagem de início de comunicação.
     */
    private void enviarMensagemInicio() {
        byte[] mensagem = criarMensagemInicio();
        try {
            comunicador.enviarDados(mensagem, 0, mensagem.length);
        } catch (Exception e) {
            e.printStackTrace();
            comunicacao_falhou = true;
        }
    }

    /**
     * Cria uma mensagem de início de comunicação.
     * @return Vetor de bytes com a mensagem codificada.
     */
    private byte[] criarMensagemInicio() {
        int checksum = 0, i = 0;
        byte[] mensagem = new byte[7];
        // tipo da mensagem (1 byte)
        mensagem[0] = TIPO_PC_INICIO;
        // tamanho da mensagem (2 bytes)
        mensagem[1] = 0x00;
        mensagem[2] = 0x02;
        // número do botão (1 byte)
        mensagem[3] = (byte)(0x03 & numero_botao);
        // versão (1 byte)
        mensagem[4] = 0x00;
        // checksum
        for (i = 0; i <= 4; i ++) checksum += 0xFF & mensagem[i];
        // checksum (2 bytes)
        mensagem[5] = (byte)(0xFF & (checksum >> 8));
        mensagem[6] = (byte)(0xFF & checksum);
        return mensagem;
    }

    /**
     * Tenta enviar uma mensagem de bloco de dados.
     * @param inicio Número do primeiro byte a enviar.
     * @param tamanho Tamanho dos dados a enviar.
     */
    private void enviarMensagemDados(int inicio, int tamanho) {
        byte[] mensagem = criarMensagemDados(inicio, tamanho);
        try {
            comunicador.enviarDados(mensagem, 0, mensagem.length);
        } catch (Exception e) {
            e.printStackTrace();
            comunicacao_falhou = true;
        }
    }

    /**
     * Cria uma mensagem de envio de bloco de dados.
     * @return Vetor de bytes com a mensagem codificada.
     */
    private byte[] criarMensagemDados(int inicio, int tamanho) {
        int checksum = 0, i = 0;
        if (dados_botao.length - inicio < tamanho) tamanho = dados_botao.length - inicio;
        byte[] mensagem = new byte[3 + tamanho + 2];
        // tipo da mensagem (1 byte)
        mensagem[0] = TIPO_PC_DADOS;
        // tamanho da mensagem (2 bytes)
        mensagem[1] = (byte)(0xFF & (tamanho >> 8));
        mensagem[2] = (byte)(0xFF & tamanho);
        checksum = (0xFF & mensagem[0]) + (0xFF & mensagem[1]) + (0xFF & mensagem[2]);
        // número do botão (1 byte)
        for (i = 0; i < tamanho; i ++) {
            mensagem[3 + i] = dados_botao[i + inicio];
            checksum += 0xFF & mensagem[i + 3];
        }
        // checksum (2 bytes)
        mensagem[mensagem.length - 2] = (byte)(0xFF & (checksum >> 8));
        mensagem[mensagem.length - 1] = (byte)(0xFF & checksum);
        return mensagem;
    }

    /**
     * Tenta enviar uma mensagem de término forçado de comunicação.
     */
    private void enviarMensagemAbortar() {
        byte[] mensagem = criarMensagemAbortar();
        try {
            comunicador.enviarDados(mensagem, 0, mensagem.length);
        } catch (Exception e) {
            e.printStackTrace();
            comunicacao_falhou = true;
        }
    }

    /**
     * Cria uma mensagem de término forçado da comunicação.
     * @return Vetor de bytes com a mensagem codificada.
     */
    private byte[] criarMensagemAbortar() {
        byte[] mensagem = new byte[5];
        // tipo da mensagem (1 byte)
        mensagem[0] = TIPO_PC_ABORTAR;
        mensagem[1] = 0x00;
        mensagem[2] = 0x00;
        // checksum (2 bytes)
        mensagem[3] = 0x00;
        mensagem[4] = TIPO_PC_ABORTAR;
        return mensagem;
    }

    /**
     * Tenta enviar uma mensagem de término de comunicação.
     */
    private void enviarMensagemFinal() {
        byte[] mensagem = criarMensagemFinal();
        try {
            comunicador.enviarDados(mensagem, 0, mensagem.length);
        } catch (Exception e) {
            e.printStackTrace();
            comunicacao_falhou = true;
        }
    }

    /**
     * Cria uma mensagem de término de comunicação.
     * @return Vetor de bytes com a mensagem codificada.
     */
    private byte[] criarMensagemFinal() {
        byte[] mensagem = new byte[5];
        // tipo da mensagem (1 byte)
        mensagem[0] = TIPO_PC_DADOS;
        mensagem[1] = 0x00;
        mensagem[2] = 0x00;
        // checksum (2 bytes)
        mensagem[3] = 0x00;
        mensagem[4] = TIPO_PC_DADOS;
        return mensagem;
    }
}
