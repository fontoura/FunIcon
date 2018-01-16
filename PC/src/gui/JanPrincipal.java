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

import armazenamento.*;
import audio.OuvinteReproducao;
import audio.Reprodutor;
import auxiliar.Auxiliar;

import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;

import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import comunicacao.ComunicadorSerial;

/**
 * Classe representante da janela principal.
 * @author Felipe Michels Fontoura
 */
@SuppressWarnings("serial")
public class JanPrincipal extends JFrame implements ConstantesArmazenamento {
    /**
     * Constante indicando o estado normal (i.e. não gravando nem reproduzindo).
     */
    private static final int ESTADO_NORMAL = 0;

    /**
     * Constante indicando o estado de reprodução ativa.
     */
    private static final int ESTADO_SOANDO = 1;

    /**
     * Estado atual da janela.
     */
    private int estado;

    /**
     * Componente do painel que contém todos os componentes da janela.
     */
    private JPanel painel_janela;

    /**
     * Componente do painel com barras de rolagem que contém a biblioteca de botões.
     */
    private JScrollPane scroll_biblioteca;

    /**
     * Componente que contém a biblioteca de botões.
     */
    private CmpBibliotecaBotoes cmp_biblioteca;

    /**
     * Componente do painel que contém os botões relativos à biblioteca de botões.
     */
    private JPanel painel_botoesBiblioteca;

    /**
     * Componente do botão de criar botão.
     */
    private JButton btn_criarBotao;

    /**
     * Componente do botão de editar botão.
     */
    private JButton btn_editarBotao;

    /**
     * Componente do botão de apagar botão.
     */
    private JButton btn_apagarBotao;

    /**
     * Componente do botão de clonar botão.
     */
    private JButton btn_clonarBotao;

    /**
     * Componente do botão de ouvir botão da biblioteca.
     */
    private JButton btn_ouvirBiblioteca;

    /**
     * Componente do botão de adicionar botão ao modelo.
     */
    private JButton btn_adicionarAoModelo;

    /**
     * Componente do painel com barras de rolagem que contém a prancha.
     */
    private JScrollPane scroll_prancha;

    /**
     * Componente que contém a prancha.
     */
    private CmpPrancha cmp_prancha;

    /**
     * Componente do painel que contém os botões relativos à prancha.
     */
    private JPanel painel_botoesPrancha;

    /**
     * Componente do botão de remover botão do modelo.
     */
    private JButton btn_removerDoModelo;

    /**
     * Componente do botão de gravar modelo na prancha.
     */
    private JButton btn_gravarNaPrancha;

    /**
     * Componente do botão de copiar botão para a biblioteca.
     */
    private JButton btn_copiarParaBiblioteca;

    /**
     * Componente do botão de ouvir botão da prancha.
     */
    private JButton btn_ouvirPrancha;

    /**
     * Componente do botão de salvar modelo.
     */
    private JButton btn_salvarModelo;

    /**
     * Componente do botão de carregar modelo.
     */
    private JButton btn_carregarModelo;

    /**
     * Mapeamento dos botões para os arquivos em que eles estão salvos.
     */
    private Map<Botao, File> arquivos_botoes;

    /**
     * Pasta em que são salvos os botões.
     */
    private File pasta_botoes;

    /**
     * Thread de reprodução de som.
     */
    private Reprodutor reprodutor;

    /**
     * Ouvinte de eventos de reprodução de som (para adicionar à thread de reprodução).
     */
    private OuvinteReproducao ouvinte_reproducao;

    /**
     * Cria uma janela principal.
     * @param pasta_botoes Pasta onde estão armazenados os arquivos de botão.
     */
    public JanPrincipal(File pasta_botoes) {
        // lê os botões
        arquivos_botoes = Botao.lerBotoes(pasta_botoes);

        // define a pasta que contém os botões.
        this.pasta_botoes = pasta_botoes;

        // define o estado da janela.
        estado = ESTADO_NORMAL;

        // insere os componentes na tela.
        inserirComponentes();

        // adiciona o ouvinte de eventos de janela.
        addWindowListener(new WindowListener() {
            @Override public void windowActivated(WindowEvent wEvt) {}
            @Override public void windowClosed(WindowEvent wEvt) {}
            @Override public void windowClosing(WindowEvent wEvt) { evtFechar(); }
            @Override public void windowDeactivated(WindowEvent wEvt) {}
            @Override public void windowDeiconified(WindowEvent wEvt) {}
            @Override public void windowIconified(WindowEvent wEvt) {}
            @Override public void windowOpened(WindowEvent wEvt) {}
        });

        // adiciona o ouvinte de eventos da biblioteca de botões.
        cmp_biblioteca.adicionarOuvinteEvento(new OuvinteConjuntoBotoes () {
            @Override public void botaoSelecionado(Botao botao) { evtSelecionouBotaoBiblioteca(botao); }
            @Override public void botaoDesselecionado(Botao botao) { evtDesselecionouBotaoBiblioteca(botao); }
            @Override public void botaoAdicionado(Botao botao) { evtAdicionouBotaoBiblioteca(botao); }
            @Override public void botaoRemovido(Botao botao) { evtDeletouBotaoBiblioteca(botao); }
        });

        // adiciona o ouvinte de eventos do modelo de prancha.
        cmp_prancha.adicionarOuvinteEvento(new OuvinteConjuntoBotoes () {
            @Override public void botaoSelecionado(Botao botao) { evtSelecionouBotaoPrancha(botao); }
            @Override public void botaoDesselecionado(Botao botao) { evtDesselecionouBotaoPrancha(botao); }
            @Override public void botaoAdicionado(Botao botao) { evtAdicionouBotaoPrancha(botao); }
            @Override public void botaoRemovido(Botao botao) { evtDeletouBotaoPrancha(botao); }
        });

        // cria o ouvinte de eventos de reprodução.
        ouvinte_reproducao = new OuvinteReproducao() {
            @Override public void reproducaoTerminou() { evtReproducaoTerminou(); }
            @Override public void reproducaoFalhou(Exception e) { e.printStackTrace(); evtReproducaoTerminou(); }
        };
    }

    /**
     * Método que processa o evento de o botão de fechar da janela ser pressionado.
     */
    private void evtFechar() {
        // só pode fechar a janela se não estiver emitindo som.
        if (estado == ESTADO_NORMAL) System.exit(0);
    }

    /**
     * Método que processa o evento de o botão de criar botão ser pressionado.
     */
    private void evtCriarBotao() {
        // abre a janela de edição de botão.
        Botao botao = JanEditorBotao.criarBotao(this);

        // se o usuário criou o botão, o insere na biblioteca.
        if (botao != null)
            cmp_biblioteca.adicionarBotao(botao);
    }

    /**
     * Método que processa o evento de o botão de editar botão ser pressionado.
     */
    private void evtEditarBotao() {
        // se houver botão selecionado, abre a janela de edição.
        if (cmp_biblioteca.obterSelecionado() != null) {
            // abre a janela de edição de botão, e verifica se o usuário confirmou.
            boolean alterado = JanEditorBotao.editarBotao(cmp_biblioteca.obterSelecionado(), this);

            // de o usuário tiver alterado o botão, o redesenha na biblioteca.
            if (alterado) {
                evtEditouBotaoBiblioteca(cmp_biblioteca.obterSelecionado());
                cmp_biblioteca.redesenharSelecionado();
            }
        }
    }

    /**
     * Método que processa o evento de o botão de remover botão ser pressionado.
     */
    private void evtRemoverBotao() {
        // se houver botão selecionado, tenta remover.
        if (cmp_biblioteca.obterSelecionado() != null) {
            // pergunta para o usuário se ele quer mesmo remover.
            int resposta = JOptionPane.showConfirmDialog(
              /*   componente-pai: */ this,
              /*         mensagem: */ "Isso apagará o botão definitivamente. Essa ação não pode ser desfeita. Você tem certeza?",
              /*           título: */ "Apagar?",
              /* tipo de mensagem: */ JOptionPane.YES_NO_OPTION
            );

            // se o usuário assim desejar, remove o botão da biblioteca.
            if (resposta == JOptionPane.YES_OPTION)
                cmp_biblioteca.removerSelecionado();
        }
    }

    /**
     * Método que processa o evento de o botão de clonar botão ser pressionado.
     */
    private void evtClonarBotao() {
        // se houver botão selecionado, tenta clonar.
        if (cmp_biblioteca.obterSelecionado() != null) {
            // pergunta para o usuário se ele quer mesmo clonar o botão.
            int resposta = JOptionPane.showConfirmDialog(
              /*   componente-pai: */ this,
              /*         mensagem: */ "Isso criará uma cópia exata desse botão. Você tem certeza?",
              /*           título: */ "Clonar?",
              /* tipo de mensagem: */ JOptionPane.YES_NO_OPTION
            );

            // se o usuário assim desejar, clona o botão.
            if (resposta == JOptionPane.YES_OPTION) {
                // clona o botão.
                Botao botao = new Botao(cmp_biblioteca.obterSelecionado());
                botao.definirNomeBotao(botao.obterNomeBotao() + " (clonado)");

                // adiciona à biblioteca.
                cmp_biblioteca.adicionarBotao(botao);
            }
        }
    }

    /**
     * Método que processa o evento de o botão de ouvir botão da biblioteca ser pressionado.
     */
    private void evtOuvirBiblioteca() {
        // se houver botão selecionado reproduz o som.
        if (cmp_biblioteca.obterSelecionado() != null) {
            // prepara o objeto para reproduzir o som.
            reprodutor = cmp_biblioteca.obterSelecionado().obterSom().criarReprodutor();
            reprodutor.adicionarOuvinteEvento(ouvinte_reproducao);

            // altera o estado.
            estado = ESTADO_SOANDO;

            // deixa os componentes desabilitados durante a reprodução.
            cmp_biblioteca.setEnabled(false);
            btn_criarBotao.setEnabled(false);
            btn_ouvirBiblioteca.setEnabled(false);
            btn_editarBotao.setEnabled(false);
            btn_clonarBotao.setEnabled(false);
            btn_apagarBotao.setEnabled(false);
            btn_adicionarAoModelo.setEnabled(false);

            cmp_prancha.setEnabled(false);
            btn_removerDoModelo.setEnabled(false);
            btn_ouvirPrancha.setEnabled(false);
            btn_copiarParaBiblioteca.setEnabled(false);
            btn_gravarNaPrancha.setEnabled(false);
            btn_salvarModelo.setEnabled(false);
            btn_carregarModelo.setEnabled(false);

            // executa o som.
            reprodutor.start();
        }
    }

    /**
     * Método que processa o evento de o botão de adicionar botão ao modelo ser pressionado.
     */
    private void evtAdicionarAoModelo() {
        // se houver botão selecionado, tenta adicionar ao modelo de prancha.
        if (cmp_biblioteca.obterSelecionado() != null) {
            // verifica se há espaço na prancha.
            if (cmp_prancha.obterContagem() == 4) {
                // não há espaço na prancha.
                JOptionPane.showMessageDialog(
                  /*   componente-pai: */ this,
                  /*         mensagem: */ "Não há espaço na prancha (os quatro botões já foram ocupados).",
                  /*           título: */ "Erro!",
                  /* tipo de mensagem: */ JOptionPane.ERROR_MESSAGE
                );
            } else {
                cmp_prancha.adicionarBotao(cmp_biblioteca.obterSelecionado());
            }
        }
    }

    /**
     * Método que processa o evento de o botão de remover botão do modelo ser pressionado.
     */
    private void evtRemoverDoModelo() {
        // se houver botão selecionado, tenta remover do modelo de prancha.
        if (cmp_prancha.obterSelecionado() != null)
            cmp_prancha.removerSelecionado();
    }

    /**
     * Método que processa o evento de o botão de gravar modelo na prancha ser pressionado.
     */
    private void evtGravarNaPrancha() {
        String portas[] = ComunicadorSerial.listarSeriais();
        if (portas.length > 0) {
            String porta = JanSeletorSerial.selecionarPorta(this, portas);
            if (porta != null) {
                boolean resultado = JanTransmissor.transmitir(this, porta, cmp_prancha.obterBotoes());
                if (resultado) {
                    JOptionPane.showMessageDialog(
                      /*   componente-pai: */ this,
                      /*         mensagem: */ "A comunicação ocorreu com sucesso!",
                      /*           título: */ "Aviso!",
                      /* tipo de mensagem: */ JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                      /*   componente-pai: */ this,
                      /*         mensagem: */ "Houve uma falha na comunicação.",
                      /*           título: */ "Erro!",
                      /* tipo de mensagem: */ JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        } else {
            JOptionPane.showMessageDialog(
              /*   componente-pai: */ this,
              /*         mensagem: */ "Não há interfaces seriais disponíveis.",
              /*           título: */ "Erro!",
              /* tipo de mensagem: */ JOptionPane.ERROR_MESSAGE
            );

        }
    }

    /**
     * Método que processa o evento de o botão de copiar botão da prancha para a biblioteca ser pressionado.
     */
    private void evtCopiarParaBiblioteca() {
        // se houver botão selecionado, tenta copiar para a biblioteca.
        if (cmp_prancha.obterSelecionado() != null) {
            // clona o botão.
            Botao botao = new Botao(cmp_prancha.obterSelecionado());
            botao.definirNomeBotao(botao.obterNomeBotao() + " (importado)");

            // adiciona à biblioteca.
            cmp_biblioteca.adicionarBotao(botao);
        }

    }

    /**
     * Método que processa o evento de o botão de ouvir botão da prancha ser pressionado.
     */
    private void evtOuvirPrancha() {
        // se houver botão selecionado reproduz o som.
        if (cmp_prancha.obterSelecionado() != null) {
            // prepara o objeto para reproduzir o som.
            reprodutor = cmp_prancha.obterSelecionado().obterSom().criarReprodutor();
            reprodutor.adicionarOuvinteEvento(ouvinte_reproducao);

            // altera o estado.
            estado = ESTADO_SOANDO;

            // deixa os componentes desabilitados durante a reprodução.
            cmp_biblioteca.setEnabled(false);
            btn_criarBotao.setEnabled(false);
            btn_ouvirBiblioteca.setEnabled(false);
            btn_editarBotao.setEnabled(false);
            btn_clonarBotao.setEnabled(false);
            btn_apagarBotao.setEnabled(false);
            btn_adicionarAoModelo.setEnabled(false);

            cmp_prancha.setEnabled(false);
            btn_removerDoModelo.setEnabled(false);
            btn_ouvirPrancha.setEnabled(false);
            btn_copiarParaBiblioteca.setEnabled(false);
            btn_gravarNaPrancha.setEnabled(false);
            btn_salvarModelo.setEnabled(false);
            btn_carregarModelo.setEnabled(false);

            // executa o som.
            reprodutor.start();
        }
    }

    /**
     * Método que processa o evento de o botão de salvar modelo de prancha.
     */
    private void evtSalvarModelo() {
        // cria o filtro de arquivos para as imagens
        FileFilter filtro_imagem = new FileNameExtensionFilter("Modelo de prancha (." + extensao_prancha + ")", extensao_prancha);

        // cria a pasta de pranchas, se precisar.
        File pasta_pranchas = new File("pranchas" + File.separator);
        if (! pasta_pranchas.exists()) pasta_pranchas.mkdir();

        // cria o seletor de arquivo.
        JFileChooser seletor_arquivo = new JFileChooser(pasta_pranchas);
        seletor_arquivo.setFileFilter(filtro_imagem);
        seletor_arquivo.setMultiSelectionEnabled(false);

        // mostra o seletor de arquivo.
        int resultado = seletor_arquivo.showDialog(this, "Salvar");

        // verifica se foi selecionada a opção de "salvar".
        if (resultado == JFileChooser.APPROVE_OPTION) {
            boolean salvar = true;

            // verifica a extensão do arquivo selecionado.
            File selecionado = seletor_arquivo.getSelectedFile();
            String extensao = Auxiliar.obterExtensao(selecionado);
            if (extensao == null) selecionado = new File(selecionado.getAbsolutePath() + "." + extensao_prancha);
            else if (! extensao.equals(extensao_prancha)) selecionado = new File(selecionado.getAbsolutePath() + "." + extensao_prancha);

            // verifica se o arquivo existe.
            if (selecionado.exists()) {
                // pergunta para o usuário se deve substituir o arquivo existente.
                int resposta = JOptionPane.showConfirmDialog(
                  /*   componente-pai: */ this,
                  /*         mensagem: */ "O arquivo já existe. Deseja substituí-lo?",
                  /*           título: */ "Substituir?",
                  /* tipo de mensagem: */ JOptionPane.YES_NO_OPTION
                );

                // determina se o usuário escolheu "sim" ou "não".
                salvar = resposta == JOptionPane.YES_OPTION;
            }

            // se deve salvar, tenta salvar o arquivo.
            if (salvar) {
                boolean sucesso = cmp_prancha.gravarArquivo(selecionado);
                if (!sucesso) {
                    JOptionPane.showMessageDialog(
                      /*   componente-pai: */ this,
                      /*         mensagem: */ "Não foi possível salvar o arquivo de prancha.",
                      /*           título: */ "Erro!",
                      /* tipo de mensagem: */ JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    /**
     * Método que processa o evento de o botão de carregar modelo na prancha ser pressionado.
     */
    private void evtCarregarModelo() {
        // cria o filtro de arquivos para as imagens
        FileFilter filtro_imagem = new FileNameExtensionFilter("Modelo de prancha (." + extensao_prancha + ")", extensao_prancha);

        // cria a pasta de pranchas, se precisar.
        File pasta_pranchas = new File("pranchas" + File.separator);
        if (! pasta_pranchas.exists()) pasta_pranchas.mkdir();

        // cria o seletor de arquivo.
        JFileChooser seletor_arquivo = new JFileChooser(pasta_pranchas);
        seletor_arquivo.setFileFilter(filtro_imagem);
        seletor_arquivo.setMultiSelectionEnabled(false);

        // mostra o seletor de arquivo.
        int resultado = seletor_arquivo.showDialog(this, "Abrir");

        // verifica se foi selecionada a opção de "abrir".
        if (resultado == JFileChooser.APPROVE_OPTION) {
            // tenta abrir a prancha de um arquivo.
            boolean sucesso = cmp_prancha.abrirArquivo(seletor_arquivo.getSelectedFile());

            // verifica se teve sucesso.
            if (sucesso) {
                // se obteve sucesso, arruma os botões habilitados.
                btn_removerDoModelo.setEnabled(false);
                btn_ouvirPrancha.setEnabled(false);
                btn_copiarParaBiblioteca.setEnabled(false);
                if (cmp_prancha.obterContagem() != 0) {
                    btn_gravarNaPrancha.setEnabled(true);
                    btn_salvarModelo.setEnabled(true);
                } else {
                    btn_gravarNaPrancha.setEnabled(false);
                    btn_salvarModelo.setEnabled(false);
                }
            } else {
                JOptionPane.showMessageDialog(
                  /*   componente-pai: */ this,
                  /*         mensagem: */ "Não foi possível abrir o arquivo de prancha.",
                  /*           título: */ "Erro!",
                  /* tipo de mensagem: */ JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Método que processa o evento de o um modelo de botão da biblioteca ser selecionado.
     */
    private void evtSelecionouBotaoBiblioteca(Botao botao) {
        btn_editarBotao.setEnabled(true);
        btn_apagarBotao.setEnabled(true);
        btn_clonarBotao.setEnabled(true);
        btn_ouvirBiblioteca.setEnabled(true);
        btn_adicionarAoModelo.setEnabled(true);
    }

    /**
     * Método que processa o evento de o um modelo de botão da biblioteca ser desselecionado.
     */
    private void evtDesselecionouBotaoBiblioteca(Botao botao) {
        btn_editarBotao.setEnabled(false);
        btn_apagarBotao.setEnabled(false);
        btn_clonarBotao.setEnabled(false);
        btn_ouvirBiblioteca.setEnabled(false);
        btn_adicionarAoModelo.setEnabled(false);
    }

    /**
     * Método que processa o evento de o um modelo de botão ser adicionado à biblioteca.
     */
    private void evtAdicionouBotaoBiblioteca(Botao botao) {
        // procura um nome de arquivo livre.
        long numero = System.currentTimeMillis();
        boolean tentar_novamente = false;
        File arquivo = null;

        // enquanto não conseguir um nome de arquivo livre, tenta novamente.
        do {
            arquivo = new File(pasta_botoes, "btn_" + Long.toHexString(numero) + "." + extensao_botao);
            tentar_novamente = arquivo.exists();
            numero ++;
        } while (tentar_novamente);

        // tenta salvar o arquivo.
        FileOutputStream fluxo_saida = null;
        try {
            fluxo_saida = new FileOutputStream(arquivo);
            botao.escreverBotao(fluxo_saida);
            arquivos_botoes.put(botao, arquivo);
            fluxo_saida.close();
        } catch (Exception e) {};

        // fecha o arquivo à força, se necessário.
        try {
            if (fluxo_saida != null)
                fluxo_saida.close();
        } catch (Exception e) {};
    }

    /**
     * Método que processa o evento de o um modelo de botão da biblioteca ser editado.
     */
    private void evtEditouBotaoBiblioteca(Botao botao) {
        File arquivo = arquivos_botoes.get(botao);

        if (arquivo == null) return;

        // tenta salvar o arquivo.
        FileOutputStream fluxo_saida = null;
        try {
            fluxo_saida = new FileOutputStream(arquivo);
            botao.escreverBotao(fluxo_saida);
            fluxo_saida.close();
        } catch (Exception e) {};

        // fecha o arquivo à força, se necessário.
        try {
            if (fluxo_saida != null)
                fluxo_saida.close();
        } catch (Exception e) {};
    }

    /**
     * Método que processa o evento de o um modelo de botão ser deletado da biblioteca.
     */
    private void evtDeletouBotaoBiblioteca(Botao botao) {
        evtDesselecionouBotaoBiblioteca(botao);
        File arquivo = arquivos_botoes.get(botao);
        try {
            arquivo.delete();
            arquivos_botoes.remove(botao);
        } catch (Exception e) {}
    }

    /**
     * Método que processa o evento de o um modelo de botão da prancha ser selecionado.
     */
    private void evtSelecionouBotaoPrancha(Botao botao) {
        btn_removerDoModelo.setEnabled(true);
        btn_copiarParaBiblioteca.setEnabled(true);
        btn_ouvirPrancha.setEnabled(true);
    }

    /**
     * Método que processa o evento de o um modelo de botão da prancha ser desselecionado.
     */
    private void evtDesselecionouBotaoPrancha(Botao botao) {
        btn_removerDoModelo.setEnabled(false);
        btn_copiarParaBiblioteca.setEnabled(false);
        btn_ouvirPrancha.setEnabled(false);
    }

    /**
     * Método que processa o evento de o um modelo de botão ser adicionado à prancha.
     */
    private void evtAdicionouBotaoPrancha(Botao botao) {
        btn_gravarNaPrancha.setEnabled(true);
        btn_salvarModelo.setEnabled(true);
    }

    /**
     * Método que processa o evento de o um modelo de botão ser deletado da prancha.
     */
    private void evtDeletouBotaoPrancha(Botao botao) {
        evtDesselecionouBotaoPrancha(botao);
        if (cmp_prancha.obterContagem() == 0) {
            btn_gravarNaPrancha.setEnabled(false);
            btn_salvarModelo.setEnabled(false);
        }
    }

    /**
     * Método que processa o evento de a reprodução terminar.
     */
    private void evtReproducaoTerminou() {
        estado = ESTADO_NORMAL;
        reprodutor = null;

        boolean cmp_biblioteca_selecionado = cmp_biblioteca.obterSelecionado() != null;
        boolean cmp_prancha_selecionado = cmp_prancha.obterSelecionado() != null;
        boolean cmp_prancha_contem = cmp_prancha.obterContagem() > 0;

        cmp_biblioteca.setEnabled(true);
        btn_criarBotao.setEnabled(true);
        btn_ouvirBiblioteca.setEnabled(cmp_biblioteca_selecionado);
        btn_editarBotao.setEnabled(cmp_biblioteca_selecionado);
        btn_clonarBotao.setEnabled(cmp_biblioteca_selecionado);
        btn_apagarBotao.setEnabled(cmp_biblioteca_selecionado);
        btn_adicionarAoModelo.setEnabled(cmp_biblioteca_selecionado);

        cmp_prancha.setEnabled(true);
        btn_removerDoModelo.setEnabled(cmp_prancha_selecionado);
        btn_ouvirPrancha.setEnabled(cmp_prancha_selecionado);
        btn_copiarParaBiblioteca.setEnabled(cmp_prancha_selecionado);
        btn_gravarNaPrancha.setEnabled(cmp_prancha_contem);
        btn_salvarModelo.setEnabled(cmp_prancha_contem);
        btn_carregarModelo.setEnabled(true);
    }

    /**
     * Insere os componentes na janela.
     */
    private void inserirComponentes() {
        // define as características da janela.
        setTitle("FunIconGUI - Biblioteca de botões");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 480, 640);

        // cria os componentes da janela.
        painel_janela = new JPanel();
        painel_janela.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(painel_janela);

        scroll_biblioteca = new JScrollPane();
        scroll_biblioteca.setBorder(new TitledBorder(null, "Biblioteca de bot\u00F5es", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        scroll_biblioteca.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll_biblioteca.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        cmp_biblioteca = new CmpBibliotecaBotoes(new Vector<Botao>(arquivos_botoes.keySet()));
        scroll_biblioteca.setViewportView(cmp_biblioteca);

        painel_botoesBiblioteca = new JPanel();
        painel_botoesBiblioteca.setLayout(new GridLayout(0, 2, 5, 5));
        btn_criarBotao = new JButton("Criar botão");
        btn_criarBotao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtCriarBotao(); }
        });
        btn_criarBotao.setToolTipText("Clique para criar um novo botão e adicioná-lo à biblioteca.");
        painel_botoesBiblioteca.add(btn_criarBotao);
        btn_ouvirBiblioteca = new JButton("Ouvir som");
        btn_ouvirBiblioteca.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtOuvirBiblioteca(); }
        });
        btn_ouvirBiblioteca.setEnabled(false);
        btn_ouvirBiblioteca.setToolTipText("Clique para ouvir o som associado ao botão selecionado na biblioteca.");
        painel_botoesBiblioteca.add(btn_ouvirBiblioteca);
        btn_editarBotao = new JButton("Editar botão");
        btn_editarBotao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtEditarBotao(); }
        });
        btn_editarBotao.setEnabled(false);
        btn_editarBotao.setToolTipText("Clique para editar o botão selecionado na biblioteca.");
        painel_botoesBiblioteca.add(btn_editarBotao);
        btn_clonarBotao = new JButton("Clonar botão");
        btn_clonarBotao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtClonarBotao(); }
        });
        btn_clonarBotao.setEnabled(false);
        btn_clonarBotao.setToolTipText("Clique para criar uma cópia do botão selecionado da biblioteca.");
        painel_botoesBiblioteca.add(btn_clonarBotao);
        btn_apagarBotao = new JButton("Apagar botão");
        btn_apagarBotao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtRemoverBotao(); }
        });
        btn_apagarBotao.setEnabled(false);
        btn_apagarBotao.setToolTipText("Clique para apagar o botão selecionado da biblioteca.");
        painel_botoesBiblioteca.add(btn_apagarBotao);
        btn_adicionarAoModelo = new JButton("Adicionar à prancha");
        btn_adicionarAoModelo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtAdicionarAoModelo(); }
        });
        btn_adicionarAoModelo.setEnabled(false);
        btn_adicionarAoModelo.setToolTipText("Clique para adicionar o botão selecionado ao modelo de prancha.");
        painel_botoesBiblioteca.add(btn_adicionarAoModelo);

        JSeparator separador_meio = new JSeparator();

        scroll_prancha = new JScrollPane();
        scroll_prancha.setBorder(new TitledBorder(null, "Prancha", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        scroll_prancha.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll_prancha.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        cmp_prancha = new CmpPrancha();
        scroll_prancha.setViewportView(cmp_prancha);

        painel_botoesPrancha = new JPanel();
        painel_botoesPrancha.setLayout(new GridLayout(0, 2, 5, 5));
        btn_removerDoModelo = new JButton("Remover da prancha");
        btn_removerDoModelo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtRemoverDoModelo(); }
        });
        btn_removerDoModelo.setEnabled(false);
        btn_removerDoModelo.setToolTipText("Clique para remover o botão selecionado do modelo de prancha.");
        painel_botoesPrancha.add(btn_removerDoModelo);
        btn_ouvirPrancha = new JButton("Ouvir som");
        btn_ouvirPrancha.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtOuvirPrancha(); }
        });
        btn_ouvirPrancha.setEnabled(false);
        btn_ouvirPrancha.setToolTipText("Clique para ouvir o som associado ao botão selecionado no modelo de prancha.");
        painel_botoesPrancha.add(btn_ouvirPrancha);
        btn_copiarParaBiblioteca = new JButton("Copiar botão para biblioteca");
        btn_copiarParaBiblioteca.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtCopiarParaBiblioteca(); }
        });
        btn_copiarParaBiblioteca.setEnabled(false);
        btn_copiarParaBiblioteca.setToolTipText("Clique para copiar esse botão para a sua biblioteca de botões.");
        painel_botoesPrancha.add(btn_copiarParaBiblioteca);
        btn_gravarNaPrancha = new JButton("Gravar na prancha real");
        btn_gravarNaPrancha.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtGravarNaPrancha(); }
        });
        btn_gravarNaPrancha.setEnabled(false);
        btn_gravarNaPrancha.setToolTipText("Clique para gravar o modelo de prancha na prancha real.");
        painel_botoesPrancha.add(btn_gravarNaPrancha);
        btn_salvarModelo = new JButton("Salvar prancha");
        btn_salvarModelo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtSalvarModelo(); }
        });
        btn_salvarModelo.setEnabled(false);
        btn_salvarModelo.setToolTipText("Clique para salvar o modelo de prancha em um arquivo (." + extensao_prancha + ").");
        painel_botoesPrancha.add(btn_salvarModelo);
        btn_carregarModelo = new JButton("Carregar prancha");
        btn_carregarModelo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtCarregarModelo(); }
        });
        btn_carregarModelo.setToolTipText("Clique para carregar o modelo de prancha de um arquivo (." + extensao_prancha + ").");
        painel_botoesPrancha.add(btn_carregarModelo);

        // coloca os componentes na janela.
        GridBagLayout gbl_painel_janela = new GridBagLayout();
        gbl_painel_janela.columnWidths = new int[]{0, 0};
        gbl_painel_janela.rowHeights = new int[]{0, 0, 0, scroll_prancha.getPreferredSize().height, 0};
        gbl_painel_janela.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_painel_janela.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gbl_painel_janela);
        painel_janela.setLayout(gbl_painel_janela);

        GridBagConstraints gbc_scroll_biblioteca = new GridBagConstraints();
        gbc_scroll_biblioteca.insets = new Insets(0, 0, 5, 0);
        gbc_scroll_biblioteca.fill = GridBagConstraints.BOTH;
        gbc_scroll_biblioteca.gridx = 0;
        gbc_scroll_biblioteca.gridy = 0;
        painel_janela.add(scroll_biblioteca, gbc_scroll_biblioteca);

        GridBagConstraints gbc_painel_botoesBiblioteca = new GridBagConstraints();
        gbc_painel_botoesBiblioteca.insets = new Insets(0, 0, 5, 0);
        gbc_painel_botoesBiblioteca.fill = GridBagConstraints.BOTH;
        gbc_painel_botoesBiblioteca.gridx = 0;
        gbc_painel_botoesBiblioteca.gridy = 1;
        painel_janela.add(painel_botoesBiblioteca, gbc_painel_botoesBiblioteca);

        GridBagConstraints gbc_separador_meio = new GridBagConstraints();
        gbc_separador_meio.insets = new Insets(0, 0, 5, 0);
        gbc_separador_meio.fill = GridBagConstraints.HORIZONTAL;
        gbc_separador_meio.gridx = 0;
        gbc_separador_meio.gridy = 2;
        painel_janela.add(separador_meio, gbc_separador_meio);

        GridBagConstraints gbc_scroll_prancha = new GridBagConstraints();
        gbc_scroll_prancha.insets = new Insets(0, 0, 5, 0);
        gbc_scroll_prancha.fill = GridBagConstraints.BOTH;
        gbc_scroll_prancha.gridx = 0;
        gbc_scroll_prancha.gridy = 3;
        painel_janela.add(scroll_prancha, gbc_scroll_prancha);

        GridBagConstraints gbc_painel_prancha = new GridBagConstraints();
        gbc_painel_prancha.fill = GridBagConstraints.BOTH;
        gbc_painel_prancha.gridx = 0;
        gbc_painel_prancha.gridy = 4;
        painel_janela.add(painel_botoesPrancha, gbc_painel_prancha);
    }
}
