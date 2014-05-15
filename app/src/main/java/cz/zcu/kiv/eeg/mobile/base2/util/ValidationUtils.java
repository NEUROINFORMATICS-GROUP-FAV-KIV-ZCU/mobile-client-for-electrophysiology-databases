package cz.zcu.kiv.eeg.mobile.base2.util;

import android.content.Context;
import android.webkit.URLUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;

/**
 * Support class gathering validation util methods. Based on previous implementation.
 * 
 * @author Petr Miko and Jaroslav Hošek
 */
public class ValidationUtils {

	public static String isUserValid(Context cx, User user) {
		StringBuilder error = new StringBuilder();
		if (isUsernameFormatInvalid(user.getUsername()))
			error.append(cx.getString(R.string.error_invalid_username)).append('\n');
		if (isPasswordFormatInvalid(user.getPassword()))
			error.append(cx.getString(R.string.error_invalid_password)).append('\n');
		if (isUrlFormatInvalid(user.getUrl()))
			error.append(cx.getString(R.string.error_invalid_url)).append('\n');

		return error.toString();
	}

	/**
	 * Checks, whether is provided string a valid mail address.
	 * 
	 * @param email email address
	 * @return true if is string a valid email address
	 */
	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * Checks, if is string not empty or not initialized.
	 * 
	 * @param s string reference
	 * @return true if is string empty or not initialized
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}

	/**
	 * Checks, whether is username empty or has invalid format (ie. no email).
	 * 
	 * @param username credentials username
	 * @return true if username is invalid
	 */
	public static boolean isUsernameFormatInvalid(String username) {
		return isEmpty(username) || !isEmailValid(username);
	}

	/**
	 * Checks, whether is password was provided.
	 * 
	 * @param password credentials password
	 * @return true if no password was provided
	 */
	public static boolean isPasswordFormatInvalid(String password) {
		return isEmpty(password);
	}

	/**
	 * Method for checking, whether provided endpoint is a valid URL.
	 * 
	 * @param url endpoint url
	 * @return true if endpoint string is not a URL.
	 */
	public static boolean isUrlFormatInvalid(String url) {
		return isEmpty(url) || !URLUtil.isValidUrl(url) || "http://".equals(url) || "https://".equals(url);
	}

	/**
	 * Tests whether provided string represents date.
	 * 
	 * @param date date string
	 * @param datePattern date format pattern
	 * @return true if string represents date
	 */
	public static boolean isDateValid(String date, String datePattern) {
		SimpleDateFormat sf = new SimpleDateFormat(datePattern);
		sf.setLenient(false);
		try {
			sf.parse(date);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
}
