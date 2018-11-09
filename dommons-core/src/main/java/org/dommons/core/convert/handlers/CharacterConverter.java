/*
 * @(#)CharacterConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers;

import org.dommons.core.convert.ConvertHandler;
import org.dommons.core.convert.Converter;
import org.dommons.core.string.Charician;
import org.dommons.core.string.Stringure;

/**
 * 字符转换器
 * @author Demon 2011-10-21
 */
public class CharacterConverter implements ConvertHandler<Object, Character> {

	public Character convert(Object value, Class<? extends Object> source, Class<Character> target) {
		if (CharSequence.class.isAssignableFrom(source)) {
			String str = String.valueOf(value);
			if (str.length() == 0) return null;
			Character ch = Charician.ascII(Stringure.trim(str));
			if (ch != null) return ch;
			Integer integer = Converter.F.convert(str, Integer.class);
			return Character.valueOf(integer == null ? str.charAt(0) : (char) integer.intValue());
		} else if (Boolean.class.isAssignableFrom(source)) {
			return Character.valueOf(Boolean.TRUE.equals(value) ? 'Y' : 'N');
		} else if (Number.class.isAssignableFrom(source)) {
			return Character.valueOf((char) ((Number) value).intValue());
		}
		return null;
	}
}
