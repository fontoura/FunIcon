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
    * Neither the name of Universidade Federal do Paraná nor the names of its contributors
      may be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

#include <interrupts.h>	   

/*
 * Função de startup. O código começa aqui.
 */
void main()	{
	// manda uma amostra vazia de áudio para o latch.
	SELECIONAR(0, 1, 0);
	*MEMORIA_EXTERNA = 0;

	// marca que não há nenhum botão em gravação.
	RECEBEU_BOTAO = 0;

	// inicializa as variáveis utilizadas para a varredura das matrizes de LEDs.
	varredura_inicializar();

	// ativa o timer 0.
	TL0 = 0x8D;	// 256 - 115 = 141 = 0x8D
	TH0 = 0x8D;
	TMOD = 0x22;

	TL1 = 0xFF; // 1111 1111
	TH1 = 0xFF; // 1111 1111
	PCON = PCON |  0x80; // 1000 0000
	SCON = 0x40; // 0100 0000
	TR1 = 1;
	REN = 1;

	// daqui para baixo é a máquina de estados do firmware.

	// estado da espera
	// - espera por eventos seriais.
	// - espera por eventos de botão pressionado.
	iniciar_estado_espera: 
		IE = 0x00;
		TR0 = 0;  	   
		varredura_iniciar_mostrando();
		IE = 0x82;	   
		TR0 = 1;
	estado_espera:	
		// varre a matriz de LEDs.
		if (INTERRUPCAO_TIMER0) {	
			INTERRUPCAO_TIMER0 = 0;
			varredura_continuar_mostrando();
		}
		// verifica condições de transição de estado.
		if (RI) {
			goto iniciar_estado_comunicacao;
		} else if (! ENTRADA_BOTAO0) {
			SELECIONAR_BOTAO(0, 0);
			goto iniciar_estado_soando;
		} else if (! ENTRADA_BOTAO1) {  
			SELECIONAR_BOTAO(0, 1);
			goto iniciar_estado_soando;
		} else if (! ENTRADA_BOTAO2) { 
			SELECIONAR_BOTAO(1, 0);
			goto iniciar_estado_soando;
		} else if (! ENTRADA_BOTAO3) {
			SELECIONAR_BOTAO(1, 1);
			goto iniciar_estado_soando;
		}
	goto estado_espera;

	// estado soando
	// - espera pelo término da reprodução.
	iniciar_estado_soando:	
		IE = 0x00;
		TR0 = 0;  
		varredura_iniciar_soando();
		IE = 0x82;
		TR0 = 1;
	estado_soando:
		if (INTERRUPCAO_TIMER0) {	 
			INTERRUPCAO_TIMER0 = 0;
			varredura_continuar_soando();
		} else if (REPRODUCAO_TERMINOU) goto iniciar_estado_espera;
		goto estado_soando;

	// estado de comunicação.
	// - comunica-se com a estação-base.
	// - verifica a flag de recebimento para atualizar a RAM.
	iniciar_estado_comunicacao:	
		IE = 0x82;
		TR0 = 1; 
		varredura_limpar();
		SELECIONAR(0, 1, 1);
		comunicacao_executar();
		if (! COMUNICACAO_FALHOU && RECEBEU_BOTAO) {
			RECEBEU_BOTAO = 0;
			varredura_inicializar();
		}
		goto iniciar_estado_espera;

	return;
}