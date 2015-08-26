package MaQiao.Constants;

import MaQiao.Constants.Constants;

public final class UNSAFEcommon {
	/**
	 * 判断两个字符串是否相同，如有一方为null，则为false
	 * @param c String
	 * @param d String
	 * @return boolean
	 */
	public static final boolean equals(final String c, final String d) {
		if (c == null || d == null) return false;
		if (System.identityHashCode(c) == System.identityHashCode(d)) return true;
		if (c.length() != d.length()) return false;
		return c.equals(d);
		//Constants.UNSAFE.getObject(c, Constants.StringArrayOffset);
	}

	/**
	 * 字符串与数组的比较
	 * @param c String
	 * @param d char[]
	 * @return boolean
	 */
	public static final boolean equals(final String c, final char... d) {
		if (c == null || d == null) return false;
		if (System.identityHashCode(c) == System.identityHashCode(d)) return true;
		if (c.length() != d.length) return false;
		int len;
		if ((len = d.length) == 0) return false;
		Object obj = Constants.UNSAFE.getObject(c, Constants.StringArrayOffset);
		while (--len >= 0)
			if (d[len] != Constants.UNSAFE.getChar(obj, Constants.ArrayAddress + ((len) << 1))) return false;
		return true;
	}
}
