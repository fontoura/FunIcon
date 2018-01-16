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
import audio.*;
import auxiliar.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.*;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;

/**
 * Classe representante da janela de edição de botão.
 * @author Felipe Michels Fontoura
 */
@SuppressWarnings("serial")
public class JanEditorBotao extends JDialog {
    /**
     * Constante indicando o estado normal (i.e. não gravando nem reproduzindo).
     */
    private static final int ESTADO_NORMAL = 0;

    /**
     * Constante indicando o estado de reprodução ativa.
     */
    private static final int ESTADO_SOANDO = 1;

    /**
     * Constante indicando o estado de gravação ativa.
     */
    private static final int ESTADO_GRAVANDO = 2;

    /**
     * Estado atual da janela.
     */
    private int estado;

    /**
     * Se o objeto de botão foi alterado.
     */
    private boolean alterado;

    /**
     * Botão que está sendo editado por essa janela.
     */
    private Botao botao;

    /**
     * Componente do painel que contém todos os componentes da janela.
     */
    private JPanel painel_janela;

    /**
     * Componente do campo de texto que contém o nome do botão.
     */
    private JTextField campo_nomeBotao;

    /**
     * Componente do painel que contém o editor de ícone.
     */
    private JPanel painel_icone;

    /**
     * Componente do editor de ícone.
     */
    private CmpEditorIcone cmp_editorIcone;

    /**
     * Componente do painel que contém os botões relativos ao ícone.
     */
    private JPanel painel_botoesIcone;

    /**
     * Componente do botão de importar a imagem do ícone.
     */
    private JButton btn_importarIcone;

    /**
     * Componente do botão de exportar a imagem do ícone.
     */
    private JButton btn_exportarIcone;

    /**
     * Componente do campo de texto que contém o nome do som.
     */
    private JTextField campo_nomeSom;

    /**
     * Componente do painel que contém o editor de som.
     */
    private JPanel painel_som;

    /**
     * Componente do editor de som.
     */
    private CmpEditorSom cmp_editorSom;

    /**
     * Componente do painel que contém os botões relativos ao som.
     */
    private JPanel painel_botoesSom;

    /**
     * Componente do botão de gravar o som e parar gravação do som.
     */
    private JButton btn_gravar_parar;

    /**
     * Componente do botão de reproduzir o som.
     */
    private JButton btn_ouvir;

    /**
     * Componente do botão de importar um som.
     */
    private JButton btn_importarSom;

    /**
     * Componente do botão de exportar um som.
     */
    private JButton btn_exportarSom;

    /**
     * Componente do painel que contém os botões relativos ao botão como um todo.
     */
    private JPanel painel_botoesEditor;

    /**
     * Componente do botão de cancelar as alterações.
     */
    private JButton btn_cancelar;

    /**
     * Componente do botão de salvar as alterações.
     */
    private JButton btn_salvar;

    /**
     * Thread de gravação de som.
     */
    private Gravador gravador;

    /**
     * Ouvinte de eventos de gravação de som (para adicionar à thread de gravação).
     */
    private OuvinteGravacao ouvinte_gravacao;

    /**
     * Thread de reprodução de som.
     */
    private Reprodutor reprodutor;

    /**
     * Ouvinte de eventos de reprodução de som (para adicionar à thread de reprodução).
     */
    private OuvinteReproducao ouvinte_reproducao;

    /**
     * Cria uma janela de edição de botão.
     * @param botao Botão a associar a essa janela.
     * @param pai Janela-pai dessa janela de edição de botão.
     */
    public JanEditorBotao(Botao botao, JFrame pai) {
        super(pai, true);

        // registra que o botão ainda não foi alterado.
        alterado = false;

        // define o estado da janela.
        estado = ESTADO_NORMAL;

        // verifica se o botão existe.
        if (botao == null) botao = new Botao();
        this.botao = botao;

        // cria o ouvinte de eventos de reprodução de áudio.
        ouvinte_reproducao = new OuvinteReproducao() {
            @Override public void reproducaoTerminou() { evtReproducaoTerminou(); }
            @Override public void reproducaoFalhou(Exception e) { e.printStackTrace(); evtReproducaoTerminou(); }
        };

        // cria o ouvinte de eventos de gravação de áudio.
        ouvinte_gravacao = new OuvinteGravacao() {
            @Override public void gravacaoTerminou() { evtGravacaoTerminou(); }
            @Override public void gravacaoFalhou(Exception e) { e.printStackTrace(); evtGravacaoTerminou(); }
        };

        // constrói os componentes e a estrutura da janela.
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
    }

    /**
     * Método que processa o evento de o botão de fechar da janela ser pressionado.
     */
    private void evtFechar() {
        // só pode fechar a janela se não estiver emitindo som nem gravando som.
        if (estado == ESTADO_NORMAL) evtCancelar();
    }

    /**
     * Método que processa o evento de o botão de ouvir som ser pressionado.
     */
    private void evtOuvirSom() {
        // prepara o objeto para reproduzir o som.
        reprodutor = cmp_editorSom.obterReprodutor();
        reprodutor.adicionarOuvinteEvento(ouvinte_reproducao);

        // altera o estado.
        estado = ESTADO_SOANDO;

        // deixa os componentes desabilitados durante a reprodução.
        campo_nomeBotao.setEnabled(false);
        cmp_editorIcone.setEnabled(false);
        btn_importarIcone.setEnabled(false);
        btn_exportarIcone.setEnabled(false);
        campo_nomeSom.setEnabled(false);
        cmp_editorSom.setEnabled(false);
        btn_gravar_parar.setEnabled(false);
        btn_ouvir.setEnabled(false);
        btn_importarSom.setEnabled(false);
        btn_exportarSom.setEnabled(false);
        btn_cancelar.setEnabled(false);
        btn_salvar.setEnabled(false);

        // executa o som.
        reprodutor.start();
    }

    /**
     * Método que processa o evento do término da reprodução do som.
     */
    private void evtReproducaoTerminou() {
        // re-habilita os botões.
        campo_nomeBotao.setEnabled(true);
        cmp_editorIcone.setEnabled(true);
        btn_importarIcone.setEnabled(true);
        btn_exportarIcone.setEnabled(true);
        campo_nomeSom.setEnabled(true);
        cmp_editorSom.setEnabled(true);
        btn_gravar_parar.setEnabled(true);
        btn_ouvir.setEnabled(true);
        btn_importarSom.setEnabled(true);
        btn_exportarSom.setEnabled(true);
        btn_cancelar.setEnabled(true);
        btn_salvar.setEnabled(true);

        // volta para o estado normal.
        estado = ESTADO_NORMAL;

        // deleta o reprodutor.
        reprodutor = null;
    }

    /**
     * Método que processa o evento de o botão de ouvir som ser pressionado.
     */
    private void evtGravarPararSom() {
        if (estado == ESTADO_GRAVANDO) {
            // pára a gravação do som.
            if (gravador != null) gravador.parar();
        } else {
            // verifica se deve substituir o som existente.
            int resposta = JOptionPane.showConfirmDialog(
              /*   componente-pai: */ this,
              /*         mensagem: */ "Isso substituirá o som gravado. Você tem certeza?",
              /*           título: */ "Substituir?",
              /* tipo de mensagem: */ JOptionPane.YES_NO_OPTION
            );

            if (resposta == JOptionPane.YES_OPTION) {
                // troca a label do botão.
                btn_gravar_parar.setText("Parar");

                // deixa os componentes desabilitados durante a gravação.
                campo_nomeBotao.setEnabled(false);
                cmp_editorIcone.setEnabled(false);
                btn_importarIcone.setEnabled(false);
                btn_exportarIcone.setEnabled(false);
                campo_nomeSom.setEnabled(false);
                cmp_editorSom.setEnabled(false);
                btn_ouvir.setEnabled(false);
                btn_importarSom.setEnabled(false);
                btn_exportarSom.setEnabled(false);
                btn_cancelar.setEnabled(false);
                btn_salvar.setEnabled(false);

                // troca o desenho de onda.
                cmp_editorSom.definirSom(new Som());

                // cria um gravador.
                gravador = new Gravador();
                gravador.adicionarOuvinteEvento(ouvinte_gravacao);

                // altera o estado.
                estado = ESTADO_GRAVANDO;

                // inicia o gravador.
                gravador.start();
            }
        }
    }

    /**
     * Método que processa o evento do término da gravação do som.
     */
    private void evtGravacaoTerminou() {
        // altera o som do componente de som
        cmp_editorSom.definirSom(gravador.obterSom());

        // re-habilita os botões
        campo_nomeBotao.setEnabled(true);
        cmp_editorIcone.setEnabled(true);
        btn_importarIcone.setEnabled(true);
        btn_exportarIcone.setEnabled(true);
        campo_nomeSom.setEnabled(true);
        cmp_editorSom.setEnabled(true);
        btn_ouvir.setEnabled(true);
        btn_importarSom.setEnabled(true);
        btn_exportarSom.setEnabled(true);
        btn_cancelar.setEnabled(true);
        btn_salvar.setEnabled(true);
        btn_gravar_parar.setText("Gravar");

        // deleta o gravador.
        gravador = null;

        // volta para o estado normal
        estado = ESTADO_NORMAL;

        // troca a label do botão
        btn_gravar_parar.setText("Gravar");
    }

    /**
     * Método que processa o evento de o botão de importar ícone ser pressionado.
     */
    private void evtImportarIcone() {
        // cria o filtro de arquivos para as imagens
        FileFilter filtro_imagem = new FileNameExtensionFilter("Formatos de imagem suportados", ImageIO.getReaderFileSuffixes());

        // cria o seletor de arquivo.
        JFileChooser seletor_arquivo = new JFileChooser();
        seletor_arquivo.setFileFilter(filtro_imagem);
        seletor_arquivo.setMultiSelectionEnabled(false);

        // mostra o seletor de arquivo.
        int resultado = seletor_arquivo.showDialog(this, "Abrir");

        // verifica se foi selecionada a opção de "abrir".
        if (resultado == JFileChooser.APPROVE_OPTION) {
            // cria o fluxo de entrada.
            InputStream fluxo_entrada = null;

            try {
                // tenta abrir o arquivo.
                fluxo_entrada = new FileInputStream(seletor_arquivo.getSelectedFile());

                // tenta ler uma imagem do arquivo.
                BufferedImage imagem = ImageIO.read(fluxo_entrada);

                // tenta converter a imagem num ícone.
                Icone icone = Icone.importarImagem(imagem);

                if (icone == null) {
                    // não foi possível converter a imagem em ícone.
                    JOptionPane.showMessageDialog(
                      /*   componente-pai: */ this,
                      /*         mensagem: */ "Não foi possível importar a imagem.",
                      /*           título: */ "Erro!",
                      /* tipo de mensagem: */ JOptionPane.ERROR_MESSAGE
                    );
                } else{
                    // foi possível converter a imagem em ícone.
                    cmp_editorIcone.definirIcone(icone);

                    // salva que o ícone foi alterado.
                    alterado = true;
                }

                // fecha o arquivo aberto.
                fluxo_entrada.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                  /*   componente-pai: */ this,
                  /*         mensagem: */ "Não foi possível abrir o arquivo de imagem.",
                  /*           título: */ "Erro!",
                  /* tipo de mensagem: */ JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }

            // tenta fechar novamente o arquivo.
            try {
                if (fluxo_entrada != null)
                    fluxo_entrada.close();
            } catch (Exception e) {}
        }
    }

    /**
     * Método que processa o evento de o botão de exportar ícone ser pressionado.
     */
    private void evtExportarIcone() {
        // cria o filtro de arquivos para as imagens
        FileFilter filtro_imagem = new FileNameExtensionFilter("Portable Network Graphics (.png)", "png");

        // cria o seletor de arquivo.
        JFileChooser seletor_arquivo = new JFileChooser();
        seletor_arquivo.setFileFilter(filtro_imagem);
        seletor_arquivo.setMultiSelectionEnabled(false);

        // mostra o seletor de arquivo.
        int resultado = seletor_arquivo.showDialog(this, "Salvar");

        // verifica se foi selecionada a opção de "salvar".
        if (resultado == JFileChooser.APPROVE_OPTION) {
            // verifica a extensão do arquivo selecionado.
            File selecionado = seletor_arquivo.getSelectedFile();
            String extensao = Auxiliar.obterExtensao(selecionado);
            if (extensao == null) selecionado = new File(selecionado.getAbsolutePath() + ".png");
            else if (! extensao.equals("png")) selecionado = new File(selecionado.getAbsolutePath() + ".png");

            // verifica se deve salvar o arquivo.
            boolean salvar = true;

            // verifica se o arquivo existe.
            if (seletor_arquivo.getSelectedFile().exists()) {
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
                OutputStream fluxo_saida = null;

                try {
                    // tenta abrir o arquivo.
                    fluxo_saida = new FileOutputStream(selecionado);

                    // tenta escrever a imagem para o arquivo.
                    ImageIO.write(
                      /*            imagem a salvar: */ cmp_editorIcone.obterIcone().exportarImagem(),
                      /* formato da imagem a salvar: */ "png",
                      /*             fluxo de saída: */ fluxo_saida
                    );

                    // fecha o arquivo aberto.
                    fluxo_saida.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                      /*   componente-pai: */ this,
                      /*         mensagem: */ "Não foi possível salvar o arquivo de imagem.",
                      /*           título: */ "Erro!",
                      /* tipo de mensagem: */ JOptionPane.ERROR_MESSAGE
                    );
                    e.printStackTrace();
                }

                // tenta fechar novamente o arquivo.
                try {
                    if (fluxo_saida != null)
                        fluxo_saida.close();
                } catch (Exception e) {}
            }
        }
    }

    /**
     * Método que processa o evento de o botão de exportar som ser pressionado.
     */
    private void evtExportarSom() {
        // cria o filtro de arquivos para as imagens
        FileFilter filtro_imagem = new FileNameExtensionFilter("Waveform Audio File Format (.wav)", "wav");

        // cria o seletor de arquivo.
        JFileChooser seletor_arquivo = new JFileChooser();
        seletor_arquivo.setFileFilter(filtro_imagem);
        seletor_arquivo.setMultiSelectionEnabled(false);

        // mostra o seletor de arquivo.
        int resultado = seletor_arquivo.showDialog(this, "Salvar");

        // verifica se foi selecionada a opção de "salvar".
        if (resultado == JFileChooser.APPROVE_OPTION) {
            // verifica a extensão do arquivo selecionado.
            File selecionado = seletor_arquivo.getSelectedFile();
            String extensao = Auxiliar.obterExtensao(selecionado);
            if (extensao == null) selecionado = new File(selecionado.getAbsolutePath() + ".wav");
            else if (! extensao.equals("wav")) selecionado = new File(selecionado.getAbsolutePath() + ".wav");

            // verifica se deve salvar o arquivo.
            boolean salvar = true;

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
                try {
                    // tenta salvar o som num arquivo.
                    cmp_editorSom.obterSomSelecionado().exportarArquivo(selecionado);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                      /*   componente-pai: */ this,
                      /*         mensagem: */ "Não foi possível salvar o arquivo de áudio.",
                      /*           título: */ "Erro!",
                      /* tipo de mensagem: */ JOptionPane.ERROR_MESSAGE
                    );
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Método que processa o evento de o botão de importar som ser pressionado.
     */
    private void evtImportarSom() {
        // cria o filtro de arquivos para as imagens
        FileFilter filtro_imagem = new FileNameExtensionFilter("Waveform Audio File Format (.wav)", "wav");

        // cria o seletor de arquivo.
        JFileChooser seletor_arquivo = new JFileChooser();
        seletor_arquivo.setFileFilter(filtro_imagem);
        seletor_arquivo.setMultiSelectionEnabled(false);

        // mostra o seletor de arquivo.
        int resultado = seletor_arquivo.showDialog(this, "Abrir");

        // verifica se foi selecionada a opção de "abrir".
        if (resultado == JFileChooser.APPROVE_OPTION) {
            try {
                // tenta importar o som do arquivo.
                Som importado = Som.importarArquivo(seletor_arquivo.getSelectedFile());

                if (importado == null) {
                    // não foi possível converter a imagem em ícone.
                    JOptionPane.showMessageDialog(
                      /*   componente-pai: */ this,
                      /*         mensagem: */ "Não foi possível importar o som.",
                      /*           título: */ "Erro!",
                      /* tipo de mensagem: */ JOptionPane.ERROR_MESSAGE
                    );
                } else{
                    // foi possível converter a imagem em ícone.
                    cmp_editorSom.definirSom(importado);

                    // salva que o ícone foi alterado.
                    alterado = true;
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                  /*   componente-pai: */ this,
                  /*         mensagem: */ "Não foi possível abrir o arquivo de áudio.",
                  /*           título: */ "Erro!",
                  /* tipo de mensagem: */ JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                JOptionPane.showMessageDialog(
                  /*   componente-pai: */ this,
                  /*         mensagem: */ "O formato desse arquivo de áudio não é suportado.",
                  /*           título: */ "Erro!",
                  /* tipo de mensagem: */ JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        }
    }

    /**
     * Método que processa o evento de o botão de salvar ser pressionado.
     */
    private void evtSalvar() {
        // salva as alterações no botão.
        alterado = true;
        botao.definirNomeBotao(campo_nomeBotao.getText());
        botao.definirIcone(cmp_editorIcone.obterIcone());
        botao.definirNomeSom(campo_nomeSom.getText());
        botao.definirSom(cmp_editorSom.obterSomSelecionado());

        // fecha a janela.
        dispose();
    }

    /**
     * Método que processa o evento de o botão de cancelar ser pressionado.
     */
    private void evtCancelar() {
        // pergunta se o usuário quer mesmo cancelar as alterações feitas.
        int resposta = JOptionPane.showConfirmDialog(
          /*   componente-pai: */ this,
          /*         mensagem: */ "Isso cancelará todas as alterações. Você tem certeza?",
          /*           título: */ "Cancelar?",
          /* tipo de mensagem: */ JOptionPane.YES_NO_OPTION
        );

        // se o usuário quer mesmo fechar, fecha a janela e não salva as alterações.
        if (resposta == JOptionPane.YES_OPTION) {
            alterado = false;
            dispose();
        }
    }

    /**
     * Exibe uma janela de criação de botão.
     * @param pai Janela-pai do editor de botão.
     * @return Botão criado, ou nulo em caso de o botão de cancelar ser pressionado.
     */
    public static Botao criarBotao(JFrame pai) {
        // cria a janela de edição de botão.
        JanEditorBotao janela = new JanEditorBotao(null, pai);

        // mostra a janela de edição de botão.
        janela.setVisible(true);

        // retorna o botão criado (ou nulo).
        if (janela.alterado) return janela.botao;
        else return null;
    }

    /**
     * Exibe uma janela de edição de botão.
     * @param botao Botão a editar.
     * @param pai Janela-pai do editor de botão.
     * @return Se o botão foi alterado.
     */
    public static boolean editarBotao(Botao botao, JFrame pai) {
        // cria a janela de edição de botão.
        JanEditorBotao janela = new JanEditorBotao(botao, pai);

        // mostra a janela de edição de botão.
        janela.setVisible(true);

        // retorna se o botão foi alterado.
        return janela.alterado;
    }

    /**
     * Insere os componentes na janela.
     */
    private void inserirComponentes() {
        // define as características da janela.
        setTitle("FunIconGUI - Editor de botão");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 280, 640);

        // cria os componentes da janela.
        painel_janela = new JPanel();
        painel_janela.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(painel_janela);

        GridBagLayout gbl_painel_janela = new GridBagLayout();

        JLabel label_nomeBotao = new JLabel("Nome do botão:");
        campo_nomeBotao = new JTextField();
        campo_nomeBotao.setColumns(10);
        campo_nomeBotao.setText(botao.obterNomeBotao());
        campo_nomeBotao.setToolTipText("Digite aqui o nome do botão.");

        JSeparator separador_cima = new JSeparator();

        painel_icone = new JPanel();
        painel_icone.setLayout(new GridLayout(1, 0, 0, 0));
        painel_icone.setBorder(new TitledBorder(null, "Ícone", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        cmp_editorIcone = new CmpEditorIcone(new Icone(botao.obterIcone()));
        painel_icone.add(cmp_editorIcone);

        painel_botoesIcone = new JPanel();
        painel_botoesIcone.setLayout(new GridLayout(0, 2, 5, 5));
        btn_importarIcone = new JButton("Importar");
        btn_importarIcone.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent eEvt) { evtImportarIcone(); }
        });
        btn_importarIcone.setToolTipText("Clique para importar um ícone de um arquivo de imagem.");
        painel_botoesIcone.add(btn_importarIcone);
        btn_exportarIcone = new JButton("Exportar");
        btn_exportarIcone.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent eEvt) { evtExportarIcone(); }
        });
        btn_exportarIcone.setToolTipText("Clique para exportar o ícone para um arquivo de imagem.");
        painel_botoesIcone.add(btn_exportarIcone);

        JSeparator separador_meio = new JSeparator();

        JLabel label_nomeSom = new JLabel("Nome do som:");
        campo_nomeSom = new JTextField();
        campo_nomeSom.setColumns(10);
        campo_nomeSom.setText(botao.obterNomeSom());
        campo_nomeSom.setToolTipText("Digite aqui o nome do som associado a esse botão.");

        painel_som = new JPanel();
        painel_som.setLayout(new GridLayout(1, 0, 0, 0));
        painel_som.setBorder(new TitledBorder(null, "Som", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        cmp_editorSom = new CmpEditorSom(botao.obterSom());
        painel_som.add(cmp_editorSom);

        painel_botoesSom = new JPanel();
        painel_botoesSom.setLayout(new GridLayout(0, 2, 5, 5));
        btn_gravar_parar = new JButton("Gravar");
        btn_gravar_parar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtGravarPararSom(); }
        });
        btn_gravar_parar.setToolTipText("Clique para gravar um som a partir do dispositivo de gravação padrão.");
        painel_botoesSom.add(btn_gravar_parar);
        btn_ouvir = new JButton("Ouvir");
        btn_ouvir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent eEvt) { evtOuvirSom(); }
        });
        btn_ouvir.setToolTipText("Clique para ouvir o som gravado.");
        painel_botoesSom.add(btn_ouvir);
        btn_importarSom = new JButton("Importar");
        btn_importarSom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtImportarSom(); }
        });
        btn_importarSom.setToolTipText("Clique para importar o som de um arquivo de áudio (.wav).");
        painel_botoesSom.add(btn_importarSom);
        btn_exportarSom = new JButton("Exportar");
        btn_exportarSom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtExportarSom(); }
        });
        btn_exportarSom.setToolTipText("Clique para exportar o som para um arquivo de áudio (.wav).");
        painel_botoesSom.add(btn_exportarSom);

        JSeparator separador_baixo = new JSeparator();

        painel_botoesEditor = new JPanel();
        painel_botoesEditor.setLayout(new GridLayout(0, 2, 5, 5));
        btn_cancelar = new JButton("Cancelar");
        btn_cancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtCancelar(); }
        });
        btn_cancelar.setToolTipText("Clique para fechar a janela e cancelar as alterações feitas nesse botão.");
        painel_botoesEditor.add(btn_cancelar);
        btn_salvar = new JButton("Salvar");
        btn_salvar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aEvt) { evtSalvar(); }
        });
        btn_salvar.setToolTipText("Clique para fechar a janela e salvar as alterações feitas nesse botão.");
        painel_botoesEditor.add(btn_salvar);

        // coloca os componentes na janela.
        gbl_painel_janela.columnWidths = new int[]{0, 0};
        gbl_painel_janela.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_painel_janela.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_painel_janela.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        painel_janela.setLayout(gbl_painel_janela);

        GridBagConstraints gbc_label_nomeBotao = new GridBagConstraints();
        gbc_label_nomeBotao.anchor = GridBagConstraints.WEST;
        gbc_label_nomeBotao.insets = new Insets(0, 0, 5, 0);
        gbc_label_nomeBotao.gridx = 0;
        gbc_label_nomeBotao.gridy = 0;
        painel_janela.add(label_nomeBotao, gbc_label_nomeBotao);

        GridBagConstraints gbc_campo_nomeBotao = new GridBagConstraints();
        gbc_campo_nomeBotao.insets = new Insets(0, 0, 5, 0);
        gbc_campo_nomeBotao.fill = GridBagConstraints.HORIZONTAL;
        gbc_campo_nomeBotao.gridx = 0;
        gbc_campo_nomeBotao.gridy = 1;
        painel_janela.add(campo_nomeBotao, gbc_campo_nomeBotao);

        GridBagConstraints gbc_separador_cima = new GridBagConstraints();
        gbc_separador_cima.insets = new Insets(0, 0, 5, 0);
        gbc_separador_cima.fill = GridBagConstraints.HORIZONTAL;
        gbc_separador_cima.gridx = 0;
        gbc_separador_cima.gridy = 2;
        painel_janela.add(separador_cima, gbc_separador_cima);

        GridBagConstraints gbc_painel_icone = new GridBagConstraints();
        gbc_painel_icone.insets = new Insets(0, 0, 5, 0);
        gbc_painel_icone.fill = GridBagConstraints.BOTH;
        gbc_painel_icone.gridx = 0;
        gbc_painel_icone.gridy = 3;
        painel_janela.add(painel_icone, gbc_painel_icone);

        GridBagConstraints gbc_painel_botoesIcone = new GridBagConstraints();
        gbc_painel_botoesIcone.insets = new Insets(0, 0, 5, 0);
        gbc_painel_botoesIcone.fill = GridBagConstraints.BOTH;
        gbc_painel_botoesIcone.gridx = 0;
        gbc_painel_botoesIcone.gridy = 4;
        painel_janela.add(painel_botoesIcone, gbc_painel_botoesIcone);

        GridBagConstraints gbc_separador_meio = new GridBagConstraints();
        gbc_separador_meio.insets = new Insets(0, 0, 5, 0);
        gbc_separador_meio.fill = GridBagConstraints.HORIZONTAL;
        gbc_separador_meio.gridx = 0;
        gbc_separador_meio.gridy = 5;
        painel_janela.add(separador_meio, gbc_separador_meio);

        GridBagConstraints gbc_label_nomeSom = new GridBagConstraints();
        gbc_label_nomeSom.insets = new Insets(0, 0, 5, 0);
        gbc_label_nomeSom.anchor = GridBagConstraints.WEST;
        gbc_label_nomeSom.gridx = 0;
        gbc_label_nomeSom.gridy = 6;
        painel_janela.add(label_nomeSom, gbc_label_nomeSom);

        GridBagConstraints gbc_campo_nomeSom = new GridBagConstraints();
        gbc_campo_nomeSom.insets = new Insets(0, 0, 5, 0);
        gbc_campo_nomeSom.fill = GridBagConstraints.HORIZONTAL;
        gbc_campo_nomeSom.gridx = 0;
        gbc_campo_nomeSom.gridy = 7;
        painel_janela.add(campo_nomeSom, gbc_campo_nomeSom);

        GridBagConstraints gbc_painel_som = new GridBagConstraints();
        gbc_painel_som.insets = new Insets(0, 0, 5, 0);
        gbc_painel_som.fill = GridBagConstraints.BOTH;
        gbc_painel_som.gridx = 0;
        gbc_painel_som.gridy = 8;
        painel_janela.add(painel_som, gbc_painel_som);

        GridBagConstraints gbc_painel_botoesSom = new GridBagConstraints();
        gbc_painel_botoesSom.insets = new Insets(0, 0, 5, 0);
        gbc_painel_botoesSom.fill = GridBagConstraints.BOTH;
        gbc_painel_botoesSom.gridx = 0;
        gbc_painel_botoesSom.gridy = 9;
        painel_janela.add(painel_botoesSom, gbc_painel_botoesSom);

        GridBagConstraints gbc_separador_baixo = new GridBagConstraints();
        gbc_separador_baixo.insets = new Insets(0, 0, 5, 0);
        gbc_separador_baixo.fill = GridBagConstraints.HORIZONTAL;
        gbc_separador_baixo.gridx = 0;
        gbc_separador_baixo.gridy = 10;
        painel_janela.add(separador_baixo, gbc_separador_baixo);

        GridBagConstraints gbc_painel_botoesEditor = new GridBagConstraints();
        gbc_painel_botoesEditor.insets = new Insets(0, 0, 5, 0);
        gbc_painel_botoesEditor.fill = GridBagConstraints.BOTH;
        gbc_painel_botoesEditor.gridx = 0;
        gbc_painel_botoesEditor.gridy = 11;
        painel_janela.add(painel_botoesEditor, gbc_painel_botoesEditor);
    }
}
