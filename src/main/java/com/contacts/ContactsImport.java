package com.contacts;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ezvcard.Ezvcard;
import ezvcard.property.Email;
import ezvcard.property.Telephone;

public class ContactsImport {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			Path path = Paths.get("/home/accessrun/Desktop/contacts.vcf");
			File contFile = path.toFile();
			List<String> sql = new ArrayList<>();
			AtomicInteger counter = new AtomicInteger(0);
			Instant start = Instant.now();
			
			Ezvcard.parse(contFile).all()
				.forEach(c -> {
					String fullName = c.getFormattedName().getValue();
					Telephone tel = c.getTelephoneNumbers().get(0);
					String telephone = tel.getText();
					String iCode = telephone.substring(1, 3);
					String areaCode = telephone.substring(3, 5);
					String base = telephone.substring(5, 14);
					String email = c.getEmails().get(0).getValue();
					
					sql.add(String.format("INSERT INTO TELEPHONE (AREA_CODE, INTERNATIONAL_CODE, PHONE_NUMBER, BASE_PHONE_NUMBER, STATUS) VALUES ('%s', '%s', '%s', '%s', 'ENABLE');\n", areaCode, iCode, telephone, base));
					sql.add(String.format("INSERT INTO NATURAL_PERSON (NAME, STATUS, TELEPHONE, PHONE_ID, EMAIL) VALUES ('%s', 'ENABLE', '%s', (SELECT ID FROM TELEPHONE WHERE PHONE_NUMBER LIKE '%s'), '%s');\n", fullName, telephone, telephone, email));
					sql.add(String.format("INSERT INTO USER_APP (USERNAME, PASSWORD, STATUS, NATURAL_PERSON_ID) VALUES ('%s', '***', 'ENABLE', (SELECT ID FROM NATURAL_PERSON WHERE NAME LIKE '%s'));\n", fullName, fullName));
					
					counter.incrementAndGet();
				});
			
			
			Path pSql = Paths.get("/home/accessrun/Desktop/contacts.sql");
			File sqlFile = pSql.toFile();
			
			sqlFile.createNewFile();
			
			byte[] bytes = sql.stream().collect(Collectors.joining()).getBytes();
			FileOutputStream out = new FileOutputStream(sqlFile);
			
			out.write(bytes);
			out.flush();
			out.close();
			
			Instant end = Instant.now();
			
			System.out.println(String.format("Took %s ms and %s users", Duration.between(start, end).toMillis(), counter.get()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
