package com.lixar.apba.core.util;

import java.util.Objects;

import com.lixar.apba.domain.IdAble;

public class ObjectUtil {

	public static boolean areEqualIdsAfterCast(IdAble idAble, Object id) {
		return areEqualIds(idAble, (Integer) id);
	}

	public static boolean areEqualIds(IdAble idAble, Integer id) {
		return Objects.equals(getIdOrDefault(idAble), id);
	}

	public static boolean areEqualIds(IdAble idAble, IdAble idAble2) {
		return Objects.equals(getIdOrDefault(idAble), getIdOrDefault(idAble2));
	}


	private static Integer getIdOrDefault(IdAble idAble) {
		return idAble != null ? idAble.getId() : null;
	}
}
