package demo.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Builder<T> {
	
	private Class<T> type;
	private Map<String, Object> values = new HashMap<String, Object>();
	
	public Builder(Class<T> type) {
		this.type = type;
	}

	public void set(Object value) {
		values.put(Thread.currentThread().getStackTrace()[2]
				.getMethodName(), value);
	}
	
	@SuppressWarnings("unchecked")
	public <V> V get() {
		return (V) values.get(Thread.currentThread()
				.getStackTrace()[2].getMethodName());
	}
	
	public T build() throws RuntimeException {
		try {
			Constructor<?> constructor = Arrays.stream(type.getDeclaredConstructors())
					.filter(c -> !Arrays.stream(c.getParameters())
							.filter(p -> !values.containsKey(p.getName()))
							.findAny()
							.isPresent())
					.findAny()
					.orElseGet(() -> {
						try {
							return type.getDeclaredConstructor();
						} catch (Exception e) {
							return null;
						}
					});
			List<String> keys = values.keySet().stream().collect(Collectors.toList());
			boolean accessible = constructor.isAccessible();
			Object[] arguments = new Object[constructor.getParameterCount()];
			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = values.get(constructor.getParameters()[i].getName());
				keys.remove(constructor.getParameters()[i].getName());
			}
			if (!accessible) {
				constructor.setAccessible(true);
			}
			Object instance = constructor.newInstance(arguments);
			if (!accessible) {
				constructor.setAccessible(false);
			}
			Map<String, Method> setters = Arrays.stream(type.getDeclaredMethods())
					.filter(m -> m.getName().startsWith("set")
							&& m.getName().length() > 3
							&& Character.isUpperCase(m.getName().charAt(3))
							&& void.class.isAssignableFrom(m.getReturnType()))
					.collect(Collectors.toMap(m -> Character.toLowerCase(m.getName().charAt(3))
							+ m.getName().substring(4), m -> m));
			for (String key : keys) {
				if (setters.containsKey(key)) {
					setters.get(key).invoke(instance, values.get(key));
				} else {
					Field field = type.getDeclaredField(key);
					accessible = field.isAccessible();
					if (!accessible) {
						field.setAccessible(true);
					}
					field.set(instance, values.get(key));
					if (!accessible) {
						field.setAccessible(false);
					}
				}
			}
			return type.cast(instance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
