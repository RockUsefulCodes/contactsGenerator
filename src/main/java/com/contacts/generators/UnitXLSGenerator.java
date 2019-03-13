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

public class UnitXLSGenerator {
	
	private static final String OUTPUT_FILE = "/home/accessrun/Downloads/generated-units.xls";

	static final Integer QNT_DATA = 2;
	
	private static HSSFWorkbook workBook;
	
	private List<String> units = new ArrayList<>();

	private List<String> people = new ArrayList<>();

	private List<String> phones = new ArrayList<>();
	
	public static void main(String[] args) {
		new UnitXLSGenerator();
		
		try {
			OutputStream out = new FileOutputStream(OUTPUT_FILE);
			workBook.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public UnitXLSGenerator() {
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
				Unit unit = fakeUnit(faker);
				
				addValue(0, unit.getId(), row);
				addValue(1, unit.getName(), row);
				addValue(2, unit.getSuperUnit().toString(), row);
				addValue(3, unit.getType(), row);
				addValue(4, unit.getArea().toString(), row);
				addValue(5, unit.getCommom().toString(), row);
				addValue(6, unit.getStreetName(), row);
				addValue(7, unit.getPerson().getId(), row);
				addValue(8, unit.getPerson().getName(), row);
				addValue(9, unit.getPerson().getRegister(), row);
				addValue(10, unit.getPerson().getPhone(), row);
				addValue(11, unit.getPerson().getEmail(), row);
				addValue(12, "", row);
				addValue(13, "MASCULINE", row);
				addValue(14, unit.getPerson().getMotherName(), row);
				addValue(15, unit.getPerson().getFatherName(), row);
				addValue(16, unit.getPerson().getMatrialStatus(), row);
				addValue(17, unit.getPerson().getContact(), row);
				
				try {
					row.createCell(18, CellType.NUMERIC).setCellValue(new SimpleDateFormat("dd/MM/yyyy").parse(unit.getPerson().getBirthDate()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Finished");
	}

	private void createHeader(HSSFRow row) {
		row.createCell(0).setCellValue("Id*");
		row.createCell(1).setCellValue("Nome*");
		row.createCell(2).setCellValue("Unidade Superior*");
		row.createCell(3).setCellValue("Tipo*");
		row.createCell(4).setCellValue("Área");
		row.createCell(5).setCellValue("Comum");
		row.createCell(6).setCellValue("Endereço*");
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

	private Unit fakeUnit(Faker faker) {
		String id = UUID.randomUUID().toString();
		String unitName = this.generateUnitName(faker);
		
		return Unit.builder()
			.id(id)
			.name(unitName)
			.superUnit(1L)
			.type("HOME")
			.area(80)
			.commom(Boolean.TRUE)
			.streetName("Segunda Avenida")
			.person(this.fakePerson(faker))
			.build();
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

	private String generateUnitName(Faker faker) {
		String cName = faker.company().name();
		String name = faker.university().name();
		
		if (units.stream().noneMatch(name::equals)) {
			String fullName = cName + " " + name;
			this.units.add(fullName);
			return fullName;
		}
		
		return this.generateUnitName(faker);
	}
	
	void addValue(int idx, String value, HSSFRow row) {
		HSSFCell cell = row.createCell(idx);
		cell.setCellValue(value);
	}
	
	@Data
	@Builder
	static class Unit {
		private String id;
		private String name;
		private Long superUnit;
		private String type;
		private Integer area;
		private Boolean commom;
		private String streetName;
		private Person person;
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
