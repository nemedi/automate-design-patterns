package demo.decorator;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public interface Decorator<T> {
	
	public class DecoratorHandler<T> implements InvocationHandler {
		
		private T instance;

		public DecoratorHandler(T instance) {
			this.instance = instance;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if ("$this".equals(method.getName())
					&& method.getParameterCount() == 0) {
				return instance;
			} else {
				Class<?> type = method.getDeclaringClass();
				MethodHandles.Lookup lookup = MethodHandles.lookup().in(type);
				Field allowedModesField = lookup.getClass()
						.getDeclaredField("allowedModes");
				makeFieldModifiable(allowedModesField);
				allowedModesField.set(lookup, -1);
				return lookup.unreflectSpecial(method, type)
						.bindTo(proxy)
						.invokeWithArguments(args);
			}
		}
		
		private static void makeFieldModifiable(Field field) throws Exception {
			field.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		}
		
	}

	default T $this() {
		return null;
	}
	
	static <D extends Decorator<T>, T> D decorate(T object, Class<D> decoration) {
		if (decoration.isInterface()) {
			DecoratorHandler<T> handler = new DecoratorHandler<T>(object);
			List<Class<?>> interfaces = new ArrayList<Class<?>>();
			interfaces.add(decoration);
			Class<?> baseType = decoration.getSuperclass();
			while (baseType != null && baseType.isInterface()) {
				interfaces.add(baseType);
				baseType = baseType.getSuperclass();
			}
			Object proxy = Proxy.newProxyInstance(Decorator.class.getClassLoader(),
					interfaces.toArray(new Class<?>[interfaces.size()]),
					handler);
			return decoration.cast(proxy);
		} else {
			return null;
		}
	}
}
