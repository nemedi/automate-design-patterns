package demo.prototype;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class Cloner {
 
	public static <T> T clone(T object) throws Exception {
		return clone(object, new HashMap<Object, Object>());
	}

	@SuppressWarnings("unchecked")
	private static <T> T clone(T object, HashMap<Object, Object> cloneTable) throws Exception {
		if(object == null) {
			return object;
		}
		if (cloneTable.containsKey(object)) {
			return (T) cloneTable.get(object);
		}
		Class<?> type = object.getClass();
		if (type.isPrimitive()
				|| Arrays.asList(Byte.class, Short.class,
						Integer.class, Long.class, Float.class, Double.class,
						Character.class, Boolean.class).contains(type)) {
			return object;
		}
		Object clone = null;
		if (String.class.equals(type)) {
			char[] data = ((String) object).toCharArray();
			clone = String.copyValueOf(data);
		} else if (type.isArray()) {
			int length = Array.getLength(object);
			clone = Array.newInstance(type.getComponentType(), length);
			if (clone != null) {
				cloneTable.put(object, clone);
			}
			for (int i = 0; i < length; i++) {
				Object item = clone(Array.get(object, i), cloneTable);
				Array.set(clone, i, item);
			}
		} else if (object instanceof Cloneable) {
			Method method = type.getDeclaredMethod("clone");
			boolean accessible = method.isAccessible();
			if (!accessible) {
				method.setAccessible(true);
			}
			clone = method.invoke(object);
			if (!accessible) {
				method.setAccessible(false);
			}
			if (clone != null) {
				cloneTable.put(object, clone);
			}
		} else if (object instanceof Collection<?>) {
			Collection<Object> collection = (Collection<Object>) object;
			Constructor<?> constructor = type.getDeclaredConstructor();
			boolean accessible = constructor.isAccessible();
			if (!accessible) {
				constructor.setAccessible(true);
			}
			clone = constructor.newInstance();
			if (!accessible) {
				constructor.setAccessible(false);
			}
			Collection<Object> cloneCollection = (Collection<Object>) clone;
			cloneTable.put(object, clone);
			for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();) {
				Object item = iterator.next();
				Object cloneItem = clone(item, cloneTable);
				cloneCollection.add(cloneItem);
			}
		} else {
			Constructor<?> constructor = type.getDeclaredConstructor();
			boolean accessible = constructor.isAccessible();
			if (!accessible) {
				constructor.setAccessible(true);
			}
			clone = constructor.newInstance();
			if (!accessible) {
				constructor.setAccessible(false);
			}
			if (clone != null) {
				cloneTable.put(object, clone);
			}
			Field[] fields = type.getDeclaredFields();
			for(Field field : fields) {
				accessible = field.isAccessible();
				if(!accessible) {
					field.setAccessible(true);
				}
				Object value = field.get(object);
				field.set(clone, clone(value, cloneTable));
				if(!accessible) {
					field.setAccessible(false);
				}
			}
		}
		return (T) clone;
	}
}
