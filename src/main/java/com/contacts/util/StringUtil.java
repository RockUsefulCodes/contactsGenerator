package com.contacts.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

public final class StringUtil {

	/** Constante STRING_VAZIA. */
	public static final String STRING_VAZIA = "";

	/**
	 * Construtor.
	 */
	private StringUtil() {
		super();
	}
	
	public static void isNotEmpty(final String value, Consumer<String> consumer) {
		if (!isEmpty(value)) {
			consumer.accept(value);
		}
	}

	public static boolean isEmpty(final String string) {
		return (string == null || string.trim().equals(""));
	}

	public static boolean isEmpty(final String... strings) {
		return Arrays.asList(strings).stream().anyMatch(StringUtil::isEmpty);
	}

	public static String remove(final String srcString, final String localizar) {
		String result = srcString;
		if (!StringUtil.isEmpty(srcString) && !StringUtil.isEmpty(localizar)) {
			result = srcString.replaceAll(localizar, "");
		}
		return result;
	}

	public static String removeSpecialCharacter(final String string) {
		String result = string;
		if (!StringUtil.isEmpty(string)) {
			result = StringUtil.remove(string, "[^0-9]");
		}
		return result;
	}

	public static String toMatchAnyPartOfString(Optional<String> search) {
		if (search.isPresent() && !search.get().isEmpty()) {
			return "%".concat(search.get()).concat("%");
		}
		
		return "";
	}
	
	public static final String formatUUID(final String unformated) {
		return unformated.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
				"$1-$2-$3-$4-$5");
	}

	public static final String lpad(String uid, int size) {
		return size > uid.length() ? String.format("%0" + (size - uid.length()) + "d%s", 0, uid) : uid;
	}

	public static boolean nonEmpty(String str) {
		return !isEmpty(str);
	}

	public static String toString(Object obj) {
		if (obj != null) {
			return obj.toString();
		}
		
		return null;
	}
	
	
	public static Boolean notBlank(String value ) {
		if(value != null && !value.isEmpty())
			return true;

		return false;
	}

	public static String clean(String value) {
		return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public static String justNumbers(String value) {
		return value
				.replaceAll("\\s+", "")
				.replaceAll("\\D+", "");
	}

	public static String cleanPhone(String value) {
		String phone = justNumbers(value);
		
		if (value.contains("+")) {
			phone = "+".concat(phone);
		}
		
		return phone;
	}
}
