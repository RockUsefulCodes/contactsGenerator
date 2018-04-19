package com.contacts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.github.javafaker.Faker;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.TelephoneType;

public class ContactsGenerator {

	static final int QNT_CONTACTS = 5000;
	
	public static void main(String[] args) {
		
		VCard[] cards = new VCardGenerator().generate(QNT_CONTACTS);
		
		OutputStream out = null;
		
		try {
			Path path = Paths.get("/home/accessrun/Desktop/contacts.vcf");
			File contFile = path.toFile();
			
			if (!contFile.exists())
				contFile.createNewFile();
			
			out = new FileOutputStream(contFile);
			
			Ezvcard.write(cards).version(VCardVersion.V2_1).go(out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Done!");
	}
	
	static class VCardGenerator {
		VCard[] cards;
		VCard vCard = null;
		
		VCard[] generate(int qntContacts) {
			cards = new VCard[qntContacts];

			List<String> phoneNumbers = getPhoneNumbers(qntContacts);
			Faker faker = new Faker(new Locale("pt-BR"));
			
			for (int i = 0; i < cards.length; i++) {
				
				do {
					vCard = generateVCard(faker, i, phoneNumbers);
				} while (Arrays.asList(cards).stream().anyMatch(this::isEqual));
				
				cards[i] = vCard;
			}
			
			return cards;
		}
		
		private boolean isEqual(VCard v2) {
			if (v2 == null) return false;
			return this.vCard.equals(v2) || this.vCard.getFormattedName().getValue().equals(v2.getFormattedName().getValue());
		}
		
		private VCard generateVCard(Faker faker, int index, List<String> phoneNumbers) {
			String name = faker.name().fullName();
			String telephone = phoneNumbers.get(index);
			
			VCard vCard = new VCard();
			vCard.setFormattedName(name);
			vCard.addTelephoneNumber(telephone, TelephoneType.CELL);
			vCard.addEmail(name.replace(" ", "").toLowerCase().concat("@mail.com"));
			
			return vCard;
		}
		
		private List<String> getPhoneNumbers(int qnt) {
			Faker faker = new Faker();
			List<String> numbers = new ArrayList<String>();
			
			for (int i = 0; i < qnt; i++) {
				int st1 = faker.number().numberBetween(1000, 9999);
				int st2 = faker.number().numberBetween(1000, 9999);
				String phoneNumber = String.format("+55629%s%s", st1, st2);
				
				if (14 != phoneNumber.length())
					throw new RuntimeException("Quantidade de caracteres inválida");
				
				numbers.add(phoneNumber);
			}
			
			return numbers;
		}
	}

}
