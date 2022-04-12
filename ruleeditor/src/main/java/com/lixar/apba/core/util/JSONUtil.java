package com.lixar.apba.core.util;

import java.io.IOException;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONUtil {

	public static <K> K toObject(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, new TypeReference<K>() {
		});
	}

	public static <K> K toObjectSafe(String json, Logger log) {
		if (json != null) {
			try {
				return toObject(json);
			} catch (IOException e) {
				log.error("Error converting JSON \"" + json + "\"", e);
			}
		}
		return null;
	}

	public static String toMinifiedJSON(Object o) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.INDENT_OUTPUT);
		return mapper.writeValueAsString(o);
	}
}
