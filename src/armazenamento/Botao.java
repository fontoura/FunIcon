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

import java.io.*;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Classe representante de um modelo de botão.
 * @author Felipe Michels Fontoura
 */
public class Botao implements ConstantesArmazenamento {
    /**
     * Nome do botão.
     */
    private String nome_botao;

    /**
     * Ícone representante desse botão.
     */
    private Icone icone;

    /**
     * Nome do som associado a esse botão.
     */
    private String nome_som;

    /**
     * Som associado a esse botão.
     */
    private Som som;

    /**
     * Cria um novo botão vazio.
     */
    public Botao() {
        this("", null, "", null);
    }

    /**
     * Cria um novo botão a partir de determinados parâmetros.
     * @param nome_botao Nome do botão.
     * @param icone Ícone representante desse botão.
     * @param nome_som Nome do som associado a esse botão.
     * @param som Som associado a esse botão.
     */
    public Botao(String nome_botao, Icone icone, String nome_som, Som som) {
        // verifica se o nome do botão existe
        if (nome_botao == null) nome_botao = new String();
        this.nome_botao = nome_botao;

        // verifica se o ícone existe
        if (icone == null) icone = new Icone();
        this.icone = icone;

        // verifica se o nome do som existe
        if (nome_som == null) nome_som = new String();
        this.nome_som = nome_som;

        // verifica se existe o som e se o tamanho dele é adequado.
        if (som == null) som = new Som();
        //if (som.contagemAmostras() != Som.SOM_AMOSTRAS) som = som.obterTrecho(0, Som.SOM_AMOSTRAS);
        this.som = som;
    }

    /**
     * Cria um novo botão com conteúdos idênticos a outro.
     * @param botao Botão a clonar.
     */
    public Botao(Botao botao) {
        this.nome_botao = botao.nome_botao;
        this.icone = new Icone(botao.icone);
        this.nome_som = botao.nome_som;
        this.som = new Som(botao.som);
    }

    /**
     * Obtém o nome desse botão.
     * @return Nome associado a esse botão.
     */
    public String obterNomeBotao() {
        return nome_botao;
    }

    /**
     * Obtém o ícone desse botão.
     * @return Ícone associado a esse botão.
     */
    public Icone obterIcone() {
        return icone;
    }

    /**
     * Obtém o nome desse botão.
     * @return Nome associado a esse botão.
     */
    public String obterNomeSom() {
        return nome_som;
    }

    /**
     * Obtém o som associado a esse botão.
     * @return Som associado a esse botão.
     */
    public Som obterSom() {
        return som;
    }

    /**
     * Define o nome desse botão.
     * @return Antigo nome desse botão.
     */
    public String definirNomeBotao(String nome_botao) {
        String velho_nome_botao = this.nome_botao;
        this.nome_botao = nome_botao;
        return velho_nome_botao;
    }

    /**
     * Define o ícone desse botão.
     * @return Antigo ícone associado a esse botão.
     */
    public Icone definirIcone(Icone icone) {
        Icone velho_icone = this.icone;
        this.icone = icone;
        return velho_icone;
    }

    /**
     * Define o nome desse botão.
     * @return Antigo nome associado a esse botão.
     */
    public String definirNomeSom(String nome_som) {
        String velho_nome_som = this.nome_som;
        this.nome_som = nome_som;
        return velho_nome_som;
    }

    /**
     * Define o som associado a esse botão.
     * @return Antigo som associado a esse botão.
     */
    public Som definirSom(Som som) {
        Som velho_som = this.som;
        this.som = som;
        return velho_som;
    }

    /**
     * Lê um botão de um fluxo de entrada de dados.
     * @param fluxo Fluxo de entrada de dados.
     * @return Botão lido.
     * @throws IOException Caso haja problema com o fluxo de entrada.
     */
    public static Botao lerBotao(InputStream fluxo) throws IOException {
        // cria um fluxo de entrada de dados.
        DataInputStream fluxo_dados = new DataInputStream(fluxo);

        // lê a versão (cstring).
        byte[] versao = new byte[7];
        int lidos = fluxo_dados.read(versao);
        if (lidos < 7) {
            return null;
        } else {
            if (versao[0] != 'F' || versao[1] != 'I' || versao[2] != 'B'
             || versao[3] != '1' || versao[4] != '.' || versao[5] != '0'
             || versao[6] != 0) {
                return null;
            }
        }

        // lê os dados do botão.
        String nome_botao = fluxo_dados.readUTF();
        String nome_som = fluxo_dados.readUTF();

        // lê o ícone.
        Icone icone = Icone.lerIcone(fluxo_dados);

        // lê o som.
        Som som = Som.lerSom(fluxo_dados);

        // constrói o botão.
        return new Botao(nome_botao, icone, nome_som, som);
    }

    /**
     * Escreve um botão em um fluxo de saída de dados.
     * @param fluxo Fluxo de saída de dados.
     * @throws IOException Caso haja problema com o fluxo de saída.
     */
    public void escreverBotao(OutputStream fluxo) throws IOException {
        // cria um fluxo de saída de dados.
        DataOutputStream fluxo_dados = new DataOutputStream(fluxo);

        // escreve a versão (cstring).
        byte[] versao = new byte[] { 'F', 'I', 'B', '1', '.', '0', 0 };
        fluxo_dados.write(versao);

        // escreve os dados do botão.
        fluxo_dados.writeUTF(nome_botao);
        fluxo_dados.writeUTF(nome_som);

        // escreve o ícone.
        icone.escreverIcone(fluxo_dados);

        // escreve o som.
        som.escreverSom(fluxo_dados);

        // força o envio dos dados no fluxo de dados.
        fluxo_dados.flush();
    }

    /**
     * Lê o mapeamento arquivos-botões de uma determinada pasta.
     * @param pasta_botoes Pasta de onde ler os botões.
     * @return Mapeamento arquivos-botões dessa pasta.
     */
    public static Map<Botao, File> lerBotoes(File pasta_botoes) {
        // cria o mapeamento dos botões a seus arquivos.
        Map<Botao, File> arquivos_botoes = new IdentityHashMap<Botao, File>();
        // NOTA: escolheu-se o identity hash map pois ele compara referências; é isso que é preciso aqui.
        // NOTA 2: o uso dessa classe NÃO É GERALMENTE INDICADO, apenas em raros casos, como essa implementação.

        // verifica se a pasta é realmente uma pasta.
        if (pasta_botoes.isDirectory()) {
            // obtém os arquivos dessa pasta.
            File[] arquivos = pasta_botoes.listFiles(new FiltroFormato(extensao_botao));

            // tenta ler cada arquivo da pasta.
            for (File arquivo : arquivos) {
                FileInputStream fluxo_arquivo = null;
                try {
                    // tenta abrir o arquivo.
                    fluxo_arquivo = new FileInputStream(arquivo);

                    // tenta ler o botão.
                    Botao botao = Botao.lerBotao(fluxo_arquivo);
                    arquivos_botoes.put(botao, arquivo);
                } catch (Exception e) {}

                // fecha o arquivo.
                try {
                    if (fluxo_arquivo != null)
                        fluxo_arquivo.close();
                } catch (Exception e) {}
            }
        }

        // retorna o mapeamento dos botões aos seus arquivos.
        return arquivos_botoes;
    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean equals(Object obj) {
        if (obj instanceof Botao) {
            Botao botao = (Botao) obj;
            if (! botao.nome_botao.equals(nome_botao)) return false;
            if (! botao.nome_som.equals(nome_som)) return false;
            if (! botao.icone.equals(icone)) return false;
            if (! botao.som.equals(som)) return false;
            return true;
        } else return super.equals(obj);
    }
}
