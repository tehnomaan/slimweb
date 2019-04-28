package eu.miltema.slimweb.controller;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import eu.miltema.slimweb.annot.Validate;
import eu.miltema.slimweb.annot.Validate.V;

public class ValidatorAdapter implements Validator {

	@FunctionalInterface
	private interface FieldValidator extends BiFunction<Object, Map<String, String>, MessagePair> {
	}

	private class MessagePair {
		String fieldName;
		String message;

		public MessagePair(String fieldName, String message) {
			this.fieldName = fieldName;
			this.message = message;
		}
	}

	private static final Pattern MAILMATCH = Pattern.compile("[a-zA-Z_0-9\\.]+@\\w+(\\.\\w+)+");
	private Collection<FieldValidator> fieldValidators;

	public ValidatorAdapter(Class<?> classToValidate) {
		fieldValidators = new ArrayList<ValidatorAdapter.FieldValidator>();
		Class<?> clazz = classToValidate;
		while(clazz != Object.class) {
			for(Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);
				Validate v = field.getAnnotation(Validate.class);
				if (v == null)
					continue;
				int limiterIdx = 0;
				for(V vv : v.value()) {
					String errorKey = "error." + vv.toString().toLowerCase();
					boolean allowNull = true;
					Predicate<Object> logic;
					double limitVal = 0;
					try {
						switch(vv) {
						case MANDATORY:
							allowNull = false;
							logic = val -> val != null;
							break;
						case EMAIL:
							logic = val -> MAILMATCH.matcher(val.toString()).matches();
							break;
						case MINVAL:
							double min = limitVal = v.limiter()[limiterIdx++];
							logic = val -> Double.parseDouble(val.toString()) >= min;
							break;
						case MAXVAL:
							double max = limitVal = v.limiter()[limiterIdx++];
							logic = val -> Double.parseDouble(val.toString()) <= max;
							break;
						case MINLEN:
							int minlen = (int) (limitVal = v.limiter()[limiterIdx++]);
							logic = val -> val.toString().length() >= minlen;
							break;
						case MAXLEN:
							int maxlen = (int) (limitVal = v.limiter()[limiterIdx++]);
							logic = val -> val.toString().length() <= maxlen;
							break;
						default:
							continue;
						}
						if (limiterIdx >= v.limiter().length)
							++limiterIdx;
					}
					catch(ArrayIndexOutOfBoundsException e) {
						throw new RuntimeException(clazz.getSimpleName() + "." + field.getName() + " declares validator " + vv.toString() + ", but no limiters");
					}

					String msgParam = (limitVal == (int) limitVal ? ((int) limitVal) + "" : limitVal + "");
					boolean finalAllowNull = allowNull;
					fieldValidators.add((o, l) -> {
						try {
							return validateField(o, field, l, finalAllowNull, logic, errorKey, msgParam);
						}
						catch(Exception x) {
							throw new RuntimeException(x);
						}
					});
				}
			}
			clazz = clazz.getSuperclass();
		}
		if (fieldValidators.isEmpty())
			fieldValidators = null;
	}

	@Override
	public Map<String, String> validate(Object object, Map<String, String> labels) throws Exception {
		if (fieldValidators == null)
			return null;
		Map<String, String> messageMap = fieldValidators.stream().
				map(v -> v.apply(object, labels)).
				filter(msg -> msg != null).
				collect(Collectors.toMap(msg -> msg.fieldName, msg -> msg.message));
		return (messageMap.isEmpty() ? null : messageMap);
	}

	private MessagePair validateField(Object object, Field field, Map<String, String> labels, boolean allowNull, Predicate<Object> logic, String errorKey, String messageParam) {
		try {
			Object fieldVal = field.get(object);
			if (allowNull && fieldVal == null)
				return null;
			if (!logic.test(fieldVal))
				return new MessagePair(field.getName(), labels.getOrDefault(errorKey, "!!!" + errorKey + "!!!").replace("$", messageParam));
			else return null;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> addError(Map<String, String> errors, String fieldName, String message) {
		if (errors == null)
			errors = new HashMap<>();
		errors.put(fieldName, message);
		return errors;
	}
}
