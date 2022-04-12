package com.lixar.apba.core.util;

import com.lixar.apba.domain.IdAble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class SQLUtil {
	private static final Logger log = LoggerFactory.getLogger(SQLUtil.class);

	public static void setStringOrNull(PreparedStatement ps, int index, String s) throws SQLException {
		// some drivers may automatically handle this - but the generic approach is (though you could further change on the type) :
		if (s == null) {
			ps.setNull(index, Types.VARCHAR);
		} else {
			ps.setString(index, s);
		}
	}

	public static void setIntOrNull(PreparedStatement ps, int index, Integer i) throws SQLException {
		if (i == null) {
			ps.setNull(index, Types.INTEGER);
		} else {
			ps.setInt(index, i);
		}
	}

	public static void setBooleanOrNull(PreparedStatement ps, int index, Boolean b) throws SQLException {
		if (b == null) {
			ps.setNull(index, Types.TINYINT);
		} else {
			ps.setBoolean(index, b);
		}
	}

	public static void setIntOrNull(PreparedStatement ps, int index, IdAble obj) throws SQLException {
		if (obj == null || obj.getId() == null) {
			ps.setNull(index, Types.INTEGER);
		} else {
			ps.setInt(index, obj.getId());
		}
	}

	public static void safeClose(Statement s) {
		if (s != null) {
			try {
				s.close();
			} catch (Exception e) {
				log.error("Failed to close statement", e);
			}
		}
	}
}
