package com.lixar.apba.core.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpSession;

public class SessionUtil {
	public static synchronized void clearSession(HttpSession session) {
		List<String> attributesList = new ArrayList<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			attributesList.add(attributeNames.nextElement());
		}
		attributesList.stream().forEach(attribute -> session.removeAttribute(attribute));
	}
}
