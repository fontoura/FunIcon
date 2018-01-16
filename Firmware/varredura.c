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

#include <varredura.h>

// Linha atual da varredura das matrizes de LEDs (vai de 0 a 7).
data volatile char varredura_linha_numero;

// Seletor de linha atual, das matrizes de LEDs.
data volatile char varredura_linha_seletor;

// Amostra atual da emissão de som.
data volatile unsigned char xdata* varredura_amostra;

// Número de amostras de som restantes.
data volatile unsigned short varredura_amostras_restantes;

// Bits dos ícones separados por linha.
data volatile unsigned char varredura_icone0[8]; 
data volatile unsigned char varredura_icone1[8]; 
data volatile unsigned char varredura_icone2[8];
data volatile unsigned char varredura_icone3[8];

// Ponteiros para as coluna dos ícones relativa à linha atual.
data volatile unsigned char data* varredura_coluna_icone0; 
data volatile unsigned char data* varredura_coluna_icone1; 
data volatile unsigned char data* varredura_coluna_icone2;
data volatile unsigned char data* varredura_coluna_icone3; 

// Ponteiro para a primeira linha do ícone selecionado.
data volatile unsigned char data* varredura_coluna_icone_selecionado;

/*
 * Inicializa as variáveis relativas à varredura das matrizes de LEDs.
 */
void varredura_inicializar() {
	// 1. Carrega todos os ícones da FLASH para os buffers respectivos.
	// 1.a. Seleciona a FLASH. 
    SELECIONAR(0, 0, 0);	
	A16 = 0;

	// 1.b. Carrega o primeiro e o segundo ícones.
	varredura_icone0[0] = MEMORIA_EXTERNA[0x0000];	
	varredura_icone0[1] = MEMORIA_EXTERNA[0x0001]; 	 
	varredura_icone0[2] = MEMORIA_EXTERNA[0x0002];	 
	varredura_icone0[3] = MEMORIA_EXTERNA[0x0003]; 
	varredura_icone0[4] = MEMORIA_EXTERNA[0x0004];	 
	varredura_icone0[5] = MEMORIA_EXTERNA[0x0005]; 	 
	varredura_icone0[6] = MEMORIA_EXTERNA[0x0006];	 
	varredura_icone0[7] = MEMORIA_EXTERNA[0x0007];
	varredura_icone1[0] = MEMORIA_EXTERNA[0x8000];	 
	varredura_icone1[1] = MEMORIA_EXTERNA[0x8001]; 	 
	varredura_icone1[2] = MEMORIA_EXTERNA[0x8002];	 
	varredura_icone1[3] = MEMORIA_EXTERNA[0x8003]; 
	varredura_icone1[4] = MEMORIA_EXTERNA[0x8004];	 
	varredura_icone1[5] = MEMORIA_EXTERNA[0x8005]; 	 
	varredura_icone1[6] = MEMORIA_EXTERNA[0x8006];	 
	varredura_icone1[7] = MEMORIA_EXTERNA[0x8007];	 

	// 1.c. Carrega o terceiro e o quarto ícones.
	A16 = 1;
	varredura_icone2[0] = MEMORIA_EXTERNA[0x0000];
	varredura_icone2[1] = MEMORIA_EXTERNA[0x0001]; 	 
	varredura_icone2[2] = MEMORIA_EXTERNA[0x0002];	 
	varredura_icone2[3] = MEMORIA_EXTERNA[0x0003]; 
	varredura_icone2[4] = MEMORIA_EXTERNA[0x0004];	 
	varredura_icone2[5] = MEMORIA_EXTERNA[0x0005]; 	 
	varredura_icone2[6] = MEMORIA_EXTERNA[0x0006];	 
	varredura_icone2[7] = MEMORIA_EXTERNA[0x0007];
	varredura_icone3[0] = MEMORIA_EXTERNA[0x8000]; 
	varredura_icone3[1] = MEMORIA_EXTERNA[0x8001]; 	 
	varredura_icone3[2] = MEMORIA_EXTERNA[0x8002];	 
	varredura_icone3[3] = MEMORIA_EXTERNA[0x8003]; 
	varredura_icone3[4] = MEMORIA_EXTERNA[0x8004];	 
	varredura_icone3[5] = MEMORIA_EXTERNA[0x8005]; 	 
	varredura_icone3[6] = MEMORIA_EXTERNA[0x8006];	 
	varredura_icone3[7] = MEMORIA_EXTERNA[0x8007];

	// 2. Define os seletores.
	varredura_coluna_icone0 = varredura_icone0;	
	varredura_coluna_icone1 = varredura_icone1;
	varredura_coluna_icone2 = varredura_icone2;
	varredura_coluna_icone3 = varredura_icone3;
}
		  	
/*
 * Prepara as variáveis para a reprodução do som de um ícone.
 */
void varredura_iniciar_soando()  {
	// 1. Define o indicador de coluna.
	if (botao_MSB) {
		if (botao_LSB) {
			varredura_coluna_icone_selecionado = varredura_icone3;
		} else {   
			varredura_coluna_icone_selecionado = varredura_icone2;
		}
	} else {
		if (botao_LSB) {
			varredura_coluna_icone_selecionado = varredura_icone1;
		} else {
			varredura_coluna_icone_selecionado = varredura_icone0;
		}
	}
	varredura_coluna_icone0 = varredura_coluna_icone_selecionado;
	   
	// 2. Define os indicadores de linha.
	varredura_linha_numero = 0;
	varredura_linha_seletor = 1;

	// 3. Carrega da FLASH o número de amostras.
	SELECIONAR_FLASH();
	varredura_amostras_restantes = 0;		   
	SHORT_MSB(varredura_amostras_restantes) = endereco_memoria[8];
	SHORT_LSB(varredura_amostras_restantes) = endereco_memoria[9];    
	varredura_amostra = endereco_memoria + 10;

    // 4. Limpa as colunas.	
	// 4.a. Colunas da matriz 0.
	/* SELECIONAR(1, 0, 0); */ B2 = 1;
	*MEMORIA_EXTERNA = 0x00;
	// 4.b. Colunas da matriz 1.
	/* SELECIONAR(1, 0, 1); */ B0 = 1;
	*MEMORIA_EXTERNA = 0x00;  
	// 4.c. Colunas da matriz 3.
	/* SELECIONAR(1, 1, 1); */ B1 = 1;
	*MEMORIA_EXTERNA = 0x00;  
	// 4.d. Colunas da matriz 2.
	/* SELECIONAR(1, 1, 0); */ B0 = 0;
	*MEMORIA_EXTERNA = 0x00;

	// 5. Define que a reprodução não terminou.
	REPRODUCAO_TERMINOU = 0;
}
	   
/*
 * Continua a reprodução do som de um ícone, e continua a varredura de sua matriz de LEDs.
 */
void varredura_continuar_soando()  {
	data unsigned char valor_amostra;
	if (varredura_amostras_restantes) {
		varredura_amostras_restantes --;

		// 1. Puxa a amostra de áudio da FLASH.
    	SELECIONAR(0, 0, 0);
		A16 = botao_MSB;
		valor_amostra = *varredura_amostra;
		varredura_amostra ++;

		// 2. Escreve a amostra de áudio para o latch de áudio.
		/* SELECIONAR(0, 1, 0); */ B1 = 1;
		*MEMORIA_EXTERNA = valor_amostra;
		
		// 3. Limpa a coluna.
		SELECIONAR(1, botao_MSB, botao_LSB);
		*MEMORIA_EXTERNA = 0x00; 
			
		// 4. Define a linha.
		SELECIONAR(0, 1, 1);
		*MEMORIA_EXTERNA = ~varredura_linha_seletor; 
		
		// 5. Define a coluna. 
		SELECIONAR(1, botao_MSB, botao_LSB);
		*MEMORIA_EXTERNA = *varredura_coluna_icone0;
		
		// 6. Avança a linha.		   
		varredura_linha_numero = (varredura_linha_numero + 1) & 0x07;
		if (varredura_linha_numero) {
			varredura_coluna_icone0 ++;
			varredura_linha_seletor = varredura_linha_seletor << 1;
		} else {
			varredura_coluna_icone0 = varredura_coluna_icone_selecionado;
			varredura_linha_seletor = 1;
		} 
	} else {
		// 1. Marca que a reprodução terminou.
		REPRODUCAO_TERMINOU = 1;

		// 2. Escreve uma amostra de áudio vazia para o latch de áudio.
		SELECIONAR(0, 1, 0);
		*MEMORIA_EXTERNA = 0;	
	}
}

void varredura_limpar() {	
    // 1. Limpa as colunas.	
	// 1.a. Colunas da matriz 2.
	SELECIONAR(1, 1, 0);
	*MEMORIA_EXTERNA = 0x00;
	// 1.b. Colunas da matriz 0.
	/* SELECIONAR(1, 0, 0); */ B1 = 0;
	*MEMORIA_EXTERNA = 0x00;
	// 1.c. Colunas da matriz 1.
	/* SELECIONAR(1, 0, 1); */ B0 = 1;
	*MEMORIA_EXTERNA = 0x00;  
	// 1.d. Colunas da matriz 3.
	/* SELECIONAR(1, 1, 1); */ B1 = 1;
	*MEMORIA_EXTERNA = 0x00;
}

/*
 * Prepara as variáveis para a varredura de todas as matrizes de LEDs.
 */	 		
void varredura_iniciar_mostrando() {
	// 1. Define os indicadores de coluna.
	varredura_coluna_icone0 = varredura_icone0;	
	varredura_coluna_icone1 = varredura_icone1;
	varredura_coluna_icone2 = varredura_icone2;
	varredura_coluna_icone3 = varredura_icone3;

	// 2. Define os indicadores de linha.
	varredura_linha_numero = 0;
	varredura_linha_seletor = 1;
}
   	
/*
 * Continua a varredura de todas as matrizes de LEDs.
 */
void varredura_continuar_mostrando() {	
    // 1. Limpa as colunas.	
	// 1.a. Colunas da matriz 2.
	SELECIONAR(1, 1, 0);
	*MEMORIA_EXTERNA = 0x00;
	// 1.b. Colunas da matriz 0.
	/* SELECIONAR(1, 0, 0); */ B1 = 0;
	*MEMORIA_EXTERNA = 0x00;
	// 1.c. Colunas da matriz 1.
	/* SELECIONAR(1, 0, 1); */ B0 = 1;
	*MEMORIA_EXTERNA = 0x00;  
	// 1.d. Colunas da matriz 3.
	/* SELECIONAR(1, 1, 1); */ B1 = 1;
	*MEMORIA_EXTERNA = 0x00;
	 
	// 3. define a linha atual.
	/* SELECIONAR(0, 1, 1); */ B2 = 0;
	*MEMORIA_EXTERNA = ~varredura_linha_seletor;

    // 4. atualiza as colunas.
	// 4.a. colunas da matriz 3.
	/* SELECIONAR(1, 1, 1); */ B2 = 1;
	*MEMORIA_EXTERNA = *varredura_coluna_icone3;
	// 4.b. colunas da matriz 2.
	/* SELECIONAR(1, 1, 0); */ B0 = 0;
	*MEMORIA_EXTERNA = *varredura_coluna_icone2;
	// 4.c. colunas da matriz 0.
	/* SELECIONAR(1, 0, 0); */ B1 = 0;
	*MEMORIA_EXTERNA = *varredura_coluna_icone0;
	// 4.d. colunas da matriz 1.
	/* SELECIONAR(1, 0, 1); */ B0 = 1;
	*MEMORIA_EXTERNA = *varredura_coluna_icone1;

	// 2. avança a linha.
	varredura_linha_numero = (varredura_linha_numero + 1) & 0x07;
    if (varredura_linha_numero) {
		varredura_linha_seletor = varredura_linha_seletor << 1;
		varredura_coluna_icone0 ++;	  
		varredura_coluna_icone1 ++;
		varredura_coluna_icone2 ++;
		varredura_coluna_icone3 ++;
    } else {
		varredura_linha_seletor = 1;
		varredura_coluna_icone0 = varredura_icone0;	
		varredura_coluna_icone1 = varredura_icone1;
		varredura_coluna_icone2 = varredura_icone2;
		varredura_coluna_icone3 = varredura_icone3;
	}	  
}