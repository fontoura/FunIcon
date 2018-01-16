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

#ifndef __VARIAVEIS_H__
#define __VARIAVEIS_H__
			
#include <reg52.h>
											  
#define MEMORIA_EXTERNA ((volatile unsigned char xdata*)0x0000)
#define SELECIONAR(a,b,c) {B2=a;B1=b;B0=c;}
#define SELECIONAR_FLASH() {B0=0;B1=0;B2=0;A16=botao_MSB;SHORT_MSB(endereco_memoria)=(botao_LSB)?0x80:0x00;SHORT_LSB(endereco_memoria)=0;}
#define SELECIONAR_BOTAO(a,b) {botao_MSB=a;botao_LSB=b;}
#define SHORT_MSB(a) *((volatile unsigned char*)&a)	  
#define SHORT_LSB(a) *((volatile unsigned char*)&a + 1)

// Variável que contém um endereço de memória.
extern data volatile unsigned char xdata* endereco_memoria;

// Seletor de botão.
extern bdata volatile bit botao_MSB;
extern bdata volatile bit botao_LSB;

// Indicador da ocorrência de um evento do timer 0.
extern bdata volatile bit INTERRUPCAO_TIMER0;

// Indicador da ocorrência de um evento da serial.
extern bdata volatile bit RECEBEU_BOTAO;   

extern bdata volatile bit COMUNICACAO_FALHOU;	

// Indica que a reprodução do som chegou ao fim.
extern bdata volatile bit REPRODUCAO_TERMINOU;
			 	   
sbit ENTRADA_BOTAO0 = P1 ^ 0;  
sbit ENTRADA_BOTAO1 = P1 ^ 1;
sbit ENTRADA_BOTAO2 = P1 ^ 2;
sbit ENTRADA_BOTAO3 = P1 ^ 3;
sbit B0 = P1 ^ 4;
sbit B1 = P1 ^ 5;
sbit B2 = P1 ^ 6;
sbit A16 = P1 ^ 7;

#endif