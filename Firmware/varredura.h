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
    * Neither the name of Universidade Federal do Paran� nor the names of its contributors
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

#ifndef __VARREDURA_H__
#define __VARREDURA_H__
			  
#include <reg52.h>
#include <variaveis.h>	

/*
 * Indica que a reprodu��o do som chegou ao fim.
 */
extern bit REPRODUCAO_TERMINOU;
			
/*
 * Inicializa as vari�veis relativas � varredura das matrizes de LEDs.
 */
void varredura_inicializar();

/*
 * Prepara as vari�veis para a reprodu��o do som de um �cone.
 */
void varredura_iniciar_soando();

/*
 * Continua a reprodu��o do som de um �cone, e continua a varredura de sua matriz de LEDs.
 */
void varredura_continuar_soando();		

/*
 * Prepara as vari�veis para a varredura de todas as matrizes de LEDs.
 */
void varredura_iniciar_mostrando();
	
/*
 * Continua a varredura de todas as matrizes de LEDs.
 */
void varredura_continuar_mostrando();

void varredura_limpar();
#endif