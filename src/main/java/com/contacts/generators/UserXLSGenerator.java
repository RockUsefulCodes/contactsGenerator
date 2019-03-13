package com.contacts.generators;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;

import com.contacts.util.StringUtil;
import com.github.javafaker.Faker;

import lombok.Builder;
import lombok.Data;

public class UserXLSGenerator {
	
	private static final String OUTPUT_FILE = "/home/accessrun/Downloads/generated-users.xls";

	static final Integer QNT_DATA = 20000;
	
	private static HSSFWorkbook workBook;
	
	private List<String> people = new ArrayList<>();

	private List<String> phones = new ArrayList<>();
	
	public static void main(String[] args) {
		new UserXLSGenerator();
		
		try {
			OutputStream out = new FileOutputStream(OUTPUT_FILE);
			workBook.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public UserXLSGenerator() {
		System.out.println("Generating " + (QNT_DATA) + " lines");
		
		workBook = new HSSFWorkbook();
		
		final Faker faker = new Faker(new Locale("pt", "Br"));
		final HSSFSheet sheet = workBook.createSheet();
		
		for (int i = 0; i < (QNT_DATA + 2); i++) {
			HSSFRow row = sheet.createRow(i);
			
			if (i == 0) {
				HSSFCell head = row.createCell(0);
				head.setCellValue("Importação de Unidades");
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 19));
			} else if (i == 1) {
				this.createHeader(row);
			} else {
				
				Person person = this.fakePerson(faker);
				
				addValue(0, person.getId(), row);
				addValue(1, person.getName(), row);
				addValue(2, person.getRegister(), row);
				addValue(3, person.getPhone(), row);
				addValue(4, person.getEmail(), row);
				addValue(5, "", row);
				addValue(6, "MASCULINE", row);
				addValue(7, person.getMotherName(), row);
				addValue(8, person.getFatherName(), row);
				addValue(9, person.getMatrialStatus(), row);
				addValue(10, person.getContact(), row);
				
				try {
					row.createCell(11, CellType.NUMERIC).setCellValue(new SimpleDateFormat("dd/MM/yyyy").parse(person.getBirthDate()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Finished");
	}

	private void createHeader(HSSFRow row) {
		row.createCell(7).setCellValue("Id*");
		row.createCell(8).setCellValue("Nome*");
		row.createCell(9).setCellValue("CPF*");
		row.createCell(10).setCellValue("Telefone*");
		row.createCell(11).setCellValue("Email*");
		row.createCell(12).setCellValue("Senha");
		row.createCell(13).setCellValue("Sexo*");
		row.createCell(14).setCellValue("Nome da Mãe*");
		row.createCell(15).setCellValue("Nome do Pai*");
		row.createCell(16).setCellValue("Estado Civil*");
		row.createCell(17).setCellValue("Contato*");
		row.createCell(18).setCellValue("Data de Nascimento*");
	}

	private Person fakePerson(Faker faker) {
		String id = UUID.randomUUID().toString();
		String birthDate = new SimpleDateFormat("dd/MM/yyyy").format(faker.date().past(3, TimeUnit.DAYS));
		String fullName = this.generatePersonName(faker);
		String email = StringUtil.clean(fullName.replace(" ", "").replace(".", "")).concat("@gmail.com").toLowerCase();
		String phone = generatePhoneNumber();
		
		return Person.builder()
			.id(id)
			.name(fullName)
			.register(CPFGenerator.generate())
			.phone(phone)
			.email(email)
			.motherName(fullName)
			.fatherName(fullName)
			.matrialStatus("SINGLE")
			.contact(phone)
			.birthDate(birthDate)
			.build();
	}

	private String generatePhoneNumber() {
		String phone = "+55 " + new PhoneNumberGenerator().toString();
		
		if (phones.stream().noneMatch(phone::equals)) {
			phones.add(phone);
			return phone;
		}
		
		return this.generatePhoneNumber();
	}

	private String generatePersonName(Faker faker) {
		String name = faker.name().fullName() + " " + new Random(1).nextInt(999999999);

		if (people.stream().noneMatch(name::equals)) {
			people.add(name);
			return name;
		}
		
		return this.generatePersonName(faker);
	}

	void addValue(int idx, String value, HSSFRow row) {
		HSSFCell cell = row.createCell(idx);
		cell.setCellValue(value);
	}

	@Data
	@Builder
	static class Person {
		private String id;
		private String name;
		private String register;
		private String phone;
		private String email;
		private String gender;
		private String motherName;
		private String fatherName;
		private String matrialStatus;
		private String contact;
		private String birthDate;
	}
}
