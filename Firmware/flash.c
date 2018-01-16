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

#include <flash.h>		
				 
#define FLASH_POS_DESTRAVAR1 ((volatile unsigned char xdata*)0x5555)
#define FLASH_POS_DESTRAVAR2 ((volatile unsigned char xdata*)0x2AAA)
#define FLASH_POS_COMANDOS ((volatile unsigned char xdata*)0x5555)

#define FLASH_CMD_DESTRAVAR1 0xAA
#define FLASH_CMD_DESTRAVAR2 0x55
#define FLASH_CMD_PROGRAMAR_LIMPEZA 0x80
#define FLASH_CMD_PROGRAMAR_BYTE 0xA0
#define FLASH_LIMPAR_SETOR 0x30

#define DQ7 0x80
#define DQ5 0x20
	
/*
 * Copia dados da RAM externa para a FLASH.  Assume que:
 * - a FLASH já foi selecionada.
 * - em "endereco_memoria" está o primeiro byte do setor.
 * - as interrupções estão desabilitadas.
 */
void flash_copiar(unsigned short tamanho_dados) {
    unsigned short i, buffer;
	for (i = 0; i < tamanho_dados; i ++) {
		// 1. seleciona a RAM.
		B0 = 1;

		// 2. copia o dado da RAM para o buffer.
		buffer = *((unsigned char xdata*)i);

		// 3. seleciona a FLASH.
		B0 = 0;

		// 4. copia o dado do buffer para a FLASH.
		*FLASH_POS_DESTRAVAR1 = FLASH_CMD_DESTRAVAR1;
		*FLASH_POS_DESTRAVAR2 = FLASH_CMD_DESTRAVAR2;
		*FLASH_POS_COMANDOS = FLASH_CMD_PROGRAMAR_BYTE;
		*endereco_memoria = buffer;
		while (((*endereco_memoria & DQ7) != (buffer & DQ7)) && !(*endereco_memoria & DQ5));

		// 5. avança a posição na FLASH.
		endereco_memoria ++;
	}
}
	 
/*
 * Apaga os setores da flash relativos ao botão selecionado. Assume que:
 * - a FLASH já foi selecionada.
 * - em "endereco_memoria" está o primeiro byte do setor.
 * - as interrupções estão desabilitadas.
 */
void flash_apagarSetor() {
	// 1. limpa o primeiro setor
	// 1.a envia os comandos para limpar o primeiro setor.
	*FLASH_POS_DESTRAVAR1 = FLASH_CMD_DESTRAVAR1;
	*FLASH_POS_DESTRAVAR2 = FLASH_CMD_DESTRAVAR2;
	*FLASH_POS_COMANDOS = FLASH_CMD_PROGRAMAR_LIMPEZA;
	*FLASH_POS_DESTRAVAR1 = FLASH_CMD_DESTRAVAR1;
	*FLASH_POS_DESTRAVAR2 = FLASH_CMD_DESTRAVAR2;
	*endereco_memoria = FLASH_LIMPAR_SETOR;

	// 1.b espera até ter limpado o primeiro setor.
	while (!(*endereco_memoria & DQ7) && !(*endereco_memoria & DQ5));

	// 2. limpa o segundo setor. 
	// 2.a seleciona o segundo setor.
	SHORT_MSB(endereco_memoria) = SHORT_MSB(endereco_memoria) ^ 0x40;

	// 2.b envia os comandos para limpar o segundo setor.
	*(FLASH_POS_DESTRAVAR1) = FLASH_CMD_DESTRAVAR1;
	*(FLASH_POS_DESTRAVAR2) = FLASH_CMD_DESTRAVAR2;
	*(FLASH_POS_COMANDOS) = FLASH_CMD_PROGRAMAR_LIMPEZA;
	*(FLASH_POS_DESTRAVAR1) = FLASH_CMD_DESTRAVAR1;
	*(FLASH_POS_DESTRAVAR2) = FLASH_CMD_DESTRAVAR2;
	*endereco_memoria = FLASH_LIMPAR_SETOR;

	// 2.c espera até ter limpado o segundo setor.
	while (!(*endereco_memoria & DQ7) && !(*endereco_memoria & DQ5));
			  
	// 2.d deseleciona o segundo setor.
	SHORT_MSB(endereco_memoria) = SHORT_MSB(endereco_memoria) ^ 0x40;
}