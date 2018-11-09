/*
 * @(#)NLSItem.java     2017-01-06
 */
package org.dommons.io.nls;

import java.util.Locale;

import org.dommons.core.format.text.MessageFormat;
import org.dommons.core.util.Arrayard;
import org.dommons.io.message.AbsMessageTemplate;

/**
 * 多语言信息项
 * @author demon 2017-01-06
 */
public final class NLSItem extends AbsMessageTemplate {

	protected final Locale def;
	protected final NLSBundle bundle;
	protected final String key;
	protected final Object[] ps;

	protected NLSItem(Locale locale, NLSBundle bundle, String key, Object... ps) {
		this.def = locale;
		this.bundle = bundle;
		this.key = key;
		this.ps = ps;
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof NLSItem)) return false;
		NLSItem ni = (NLSItem) obj;
		if (!Arrayard.equals(def, ni.def)) return false;
		if (!Arrayard.equals(bundle, ni.bundle)) return false;
		if (!Arrayard.equals(key, ni.key)) return false;
		return Arrayard.equals(ps, ni.ps);
	}

	public int hashCode() {
		int hc = 0;
		if (def != null) hc ^= def.hashCode();
		hc ^= bundle.hashCode();
		hc ^= key.hashCode();
		if (ps != null) {
			for (Object o : ps)
				hc ^= o == null ? 0 : o.hashCode();
		}
		return hc;
	}

	public String toString() {
		return toString(def);
	}

	/**
	 * @param locale
	 * @return
	 */
	public String toString(Locale locale) {
		if (ps == null || ps.length < 1) return get(locale);
		else return format(locale).format(ps);
	}

	protected MessageFormat format() {
		return format(def);
	}

	/**
	 * @param locale
	 * @return
	 */
	protected MessageFormat format(Locale locale) {
		if (locale == null) locale = def;
		return bundle.format(locale, key);
	}

	/**
	 * @param locale
	 * @return
	 */
	protected String get(Locale locale) {
		if (locale == null) locale = def;
		return bundle.get(locale, key);
	}
}
