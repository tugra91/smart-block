package com.turkcell.blockmail.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ExceptionUtil {
	
	public static String convertExceptionToString(Exception e) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		PrintStream stream = new PrintStream(out);
		e.printStackTrace(stream);
		try {
			out.writeTo(stream);
		} catch (IOException e1) {
		}
		return out.toString();
	}

}
