package com.contacts.generators;

import java.util.Random;

public class PhoneNumberGenerator {

	private String retorno;
	  private String digitos = "32";
	  private Random r = new Random();

	  public PhoneNumberGenerator() {
	    retorno = nDDD() + "9" + nPrefixo() + nFinal();
	  }

	  public String toString() {
	    return retorno;
	  }

	  //Gera o prefixo do telefone.
	  private String nDDD() {  
	    int codArea = 19 - r.nextInt(9);
	    return "("+codArea+") ";
	  }

	  //Gera os primeiros Numeros
	  private String nPrefixo() {    
	    String compl[] = {"51", "59", "05"};
	    digitos += compl[r.nextInt(3)];
	    return digitos + '-';
	  }

	  //Gera a Numeracao final baseando-se em numeros aleatorios
	  private String nFinal() {
	    digitos = "";
	    for (int i=0; i<4;i++)
	      digitos += r.nextInt(10);
	    return digitos;
	  }
}
