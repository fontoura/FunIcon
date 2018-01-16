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

#ifndef __COMUNICACAO_H__
#define __COMUNICACAO_H__

#include <reg52.h>
#include <variaveis.h> 
#include <flash.h>

#define MENSAGEM_PC_INICIO 0x03	
#define MENSAGEM_PC_ENVIO 0x0C
#define MENSAGEM_PC_ABORTAR 0x0F	

#define MENSAGEM_PRANCHA_PERMISSAO 0xB3	
#define MENSAGEM_PRANCHA_NEGACAO 0xBC
#define MENSAGEM_PRANCHA_CONFIMACAO 0xB6 
#define MENSAGEM_PRANCHA_ABORTAR 0xBF	 
#define MENSAGEM_PRANCHA_GRAVADO 0xB0

// estrutura de uma mensagem pequena.
struct mensagem {
	unsigned char tipo;
	unsigned short tamanho;
	unsigned char dados[2];
	unsigned short checksum;
};

extern data volatile unsigned char xdata* comunicacao_posicao_recebida; 	 
extern data volatile unsigned short comunicacao_tamanho_recebido; 

/*
 *  Executa a comunicação.
 */
void comunicacao_executar();

#endif