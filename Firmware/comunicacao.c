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

#include <comunicacao.h>   
	 
#define ESPERA_GRANDE 320 
#define ESPERA_PEQUENA 16

// Estrutura de mensagem para recebimento e envio.
data struct mensagem comunicacao_mensagem;

data volatile unsigned short mensagem_checksum;

data volatile unsigned char xdata* comunicacao_posicao_recebida;
data volatile unsigned short comunicacao_tamanho_recebido;		  
data volatile unsigned char comunicacao_buffer_byte;
		
			 /*
void TEMP_DEBUG(unsigned char TEMP) {
	SELECIONAR(0, 1, 1);
	*MEMORIA_EXTERNA = 0x00;
	SELECIONAR(1, 0, 0);
	*MEMORIA_EXTERNA = TEMP;
}
			*/
void comunicacao_enviar_byte(unsigned char origem) {
	TI = 0;
	SBUF = origem;
	while (!TI);  
	TI = 0;
}

void enviar_mensagem_abortar() {
   comunicacao_enviar_byte(MENSAGEM_PRANCHA_ABORTAR);
   comunicacao_enviar_byte(0x00);
   comunicacao_enviar_byte(0x00);	   
   comunicacao_enviar_byte(0x00);	
   comunicacao_enviar_byte(MENSAGEM_PRANCHA_ABORTAR);
}  

void enviar_mensagem_gravado() {
   comunicacao_enviar_byte(MENSAGEM_PRANCHA_GRAVADO);
   comunicacao_enviar_byte(0x00);
   comunicacao_enviar_byte(0x00);	   
   comunicacao_enviar_byte(0x00);	
   comunicacao_enviar_byte(MENSAGEM_PRANCHA_GRAVADO);
}
	  
void enviar_mensagem_confirmar() {
   comunicacao_enviar_byte(MENSAGEM_PRANCHA_CONFIMACAO);
   comunicacao_enviar_byte(0x00);
   comunicacao_enviar_byte(0x00);	   
   comunicacao_enviar_byte(0x00);	
   comunicacao_enviar_byte(MENSAGEM_PRANCHA_CONFIMACAO);
}

void enviar_mensagem_permissao() {
   comunicacao_enviar_byte(MENSAGEM_PRANCHA_PERMISSAO);
   comunicacao_enviar_byte(0x00);
   comunicacao_enviar_byte(0x00);	   
   comunicacao_enviar_byte(0x00);	
   comunicacao_enviar_byte(MENSAGEM_PRANCHA_PERMISSAO);
}

// Recebe um byte pelo cabo serial.
void comunicacao_receber_byte(unsigned short espera_maxima, unsigned char data* destino) {
	data volatile unsigned short espera_countdown;

	// 1. Verifica se a comunicação falhou.
	if (COMUNICACAO_FALHOU) return;
				  
    // 2. Verifica se já recebeu o byte.
    if (RI) goto recebeu_byte;
	
    // 3. Espera pelo evento serial ou pelo evento de timer.
    espera_countdown = espera_maxima;
	while (-1)	{			
		if (RI) goto recebeu_byte; 
		if (INTERRUPCAO_TIMER0) {
			INTERRUPCAO_TIMER0 = 0;
			if (espera_countdown) espera_countdown --;
			else goto nao_recebeu_byte;
		}	 
	}

	// 4. Tratamento da recepção de um byte.
	recebeu_byte:
		RI = 0;
		comunicacao_buffer_byte = SBUF;
		*destino = comunicacao_buffer_byte;
		mensagem_checksum += comunicacao_buffer_byte;
		return;

	// 5. Tratamento da falha no recebimento de um byte.
	nao_recebeu_byte:
        COMUNICACAO_FALHOU = 1;
		return;		  
}

void comunicacao_receber_cabecalho() {
	// 1. Recebe os bytes do cabeçalho.	
	mensagem_checksum = 0;
	comunicacao_receber_byte(ESPERA_GRANDE,  &comunicacao_mensagem.tipo);
	comunicacao_receber_byte(ESPERA_PEQUENA, &SHORT_MSB(comunicacao_mensagem.tamanho));  
	comunicacao_receber_byte(ESPERA_PEQUENA, &SHORT_LSB(comunicacao_mensagem.tamanho));
	
}
	
// Recebe o resto de uma mensagem grande pela porta serial.
void comunicacao_receber_resto_grande() {
	data volatile unsigned short recepcao_countdown;	
	data volatile unsigned short i;
	data volatile unsigned char buffer;

	// 1. Recebe os bytes do corpo.	
	SELECIONAR(0, 0, 1);
	recepcao_countdown = comunicacao_mensagem.tamanho;
	i = 0;
	while (recepcao_countdown) { 	
		recepcao_countdown --;
		comunicacao_receber_byte(ESPERA_PEQUENA, &buffer); 
		if (COMUNICACAO_FALHOU) return;	 
		comunicacao_posicao_recebida[i] = buffer;
		comunicacao_tamanho_recebido ++;
		i ++;
	}	   	
	SELECIONAR(0, 1, 1);

	// 2. Recebe o checksum.
	comunicacao_receber_byte(ESPERA_PEQUENA, &SHORT_MSB(comunicacao_mensagem.checksum));
	comunicacao_receber_byte(ESPERA_PEQUENA, &SHORT_LSB(comunicacao_mensagem.checksum));
	if (COMUNICACAO_FALHOU) return;

	/*if (mensagem_checksum != comunicacao_mensagem.checksum) {
		COMUNICACAO_FALHOU = 1;
		return;
	}*/

	comunicacao_posicao_recebida += comunicacao_mensagem.tamanho;
}  

// Recebe o resto de uma mensagem pequena pela porta serial.
void comunicacao_receber_resto_pequena() {
	data volatile unsigned short recepcao_countdown;	
	data volatile unsigned char buffer;
	// 1. Recebe os (até 2) bytes do corpo.
	recepcao_countdown = comunicacao_mensagem.tamanho;
	// 1.a. Recebe o primeiro byte.
	if (recepcao_countdown) {
		comunicacao_receber_byte(ESPERA_PEQUENA, &comunicacao_mensagem.dados[0]);  
		if (COMUNICACAO_FALHOU) return;
		recepcao_countdown --;
	}		  
	// 1.b. Recebe o segundo byte.
	if (recepcao_countdown) {
		comunicacao_receber_byte(ESPERA_PEQUENA, &comunicacao_mensagem.dados[1]); 
		if (COMUNICACAO_FALHOU) return;
		recepcao_countdown --;
	}
	// 1.c. Recebe e descarta os demais bytes.
	while (recepcao_countdown) {
		comunicacao_receber_byte(ESPERA_PEQUENA, &buffer); 
		if (COMUNICACAO_FALHOU) return;
		recepcao_countdown --;
	}
	// 2. Recebe o checksum.
	comunicacao_receber_byte(ESPERA_PEQUENA, &SHORT_MSB(comunicacao_mensagem.checksum));  
	comunicacao_receber_byte(ESPERA_PEQUENA, &SHORT_LSB(comunicacao_mensagem.checksum));

	/*if (mensagem_checksum != comunicacao_mensagem.checksum) {
		COMUNICACAO_FALHOU = 1;
		return;
	}*/
}  
void comunicacao_executar() {  
	// 1. Define as variáveis relativas à espera.
	comunicacao_posicao_recebida = 0x0000;
	comunicacao_tamanho_recebido = 0;
	RECEBEU_BOTAO = 0;
	COMUNICACAO_FALHOU = 0;
					
	// 2. Espera pelo início da comunicação.
	comunicacao_receber_cabecalho();
	if (COMUNICACAO_FALHOU) {
		enviar_mensagem_abortar();
		return;		
	}	   

	comunicacao_receber_resto_pequena();
	if (COMUNICACAO_FALHOU) {
		enviar_mensagem_abortar();
		return;			
	}	 		

	// 2.a. A mensagem não é de início. Aborta.
	if (comunicacao_mensagem.tipo != MENSAGEM_PC_INICIO) {
		if (comunicacao_mensagem.tipo != MENSAGEM_PC_ABORTAR)
			enviar_mensagem_abortar();
		return;
	}

	// 2.b. Valida a mensagem de início.
	if (comunicacao_mensagem.dados[0] == 0) {
		SELECIONAR_BOTAO(0, 0);
	} else if (comunicacao_mensagem.dados[0] == 1) {
		SELECIONAR_BOTAO(0, 1);	   
	} else if (comunicacao_mensagem.dados[0] == 2) {
		SELECIONAR_BOTAO(1, 0);
	} else if (comunicacao_mensagem.dados[0] == 3) {	
		SELECIONAR_BOTAO(1, 1);
	} else {
		enviar_mensagem_abortar();
		return;
	}
	if (comunicacao_mensagem.dados[1] != 0) { 
		enviar_mensagem_abortar();
		return;
	}

	// 3. Responde (positivamente) ao pedido de transferência.
	enviar_mensagem_permissao();

	// 4. Recebe segmentos dos dados.
	while (-1) {
		comunicacao_receber_cabecalho();
		if (COMUNICACAO_FALHOU) {
			enviar_mensagem_abortar();
			return;
		}
		if (comunicacao_mensagem.tipo == MENSAGEM_PC_ENVIO) {
			if (comunicacao_mensagem.tamanho == 0) {
				// 5. Encerra a comunicação.
				RECEBEU_BOTAO = 1;	  
				SELECIONAR_FLASH();
				flash_apagarSetor();
				flash_copiar(comunicacao_tamanho_recebido);
				enviar_mensagem_gravado();
				return;
			} else {   
				comunicacao_receber_resto_grande();	 
				if (COMUNICACAO_FALHOU) {
					enviar_mensagem_abortar();
					return;
				} else {
					enviar_mensagem_confirmar();
				}
			}
		} else {
			comunicacao_receber_resto_pequena();
			if (comunicacao_mensagem.tipo != MENSAGEM_PC_ABORTAR)
				enviar_mensagem_abortar();
			return;
		}
	}

}