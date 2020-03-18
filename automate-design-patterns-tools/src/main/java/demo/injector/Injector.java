package demo.injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Injector {
	
	private static Binder binder;
	
	public static void setModule(Module module) {
		module.configure(binder = new Binder());
	}

	public static void resolveDependencies(Object object) {
		try {
			for (Field field : object.getClass().getDeclaredFields()) {
				if (field.getAnnotation(Inject.class) != null) {
					boolean accessible = field.isAccessible();
					if (!accessible) {
						field.setAccessible(true);
					}
					field.set(object, getDependency(field.getType()));
					if (!accessible) {
						field.setAccessible(false);
					}
				}
			}
		} catch (Exception e) {
			throw new InstantiationError(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T getDependency(Class<T> type) throws Exception {
		Binding<T> binding = binder.getBinding(type);
		if (binding.getInstance() != null) {
			return binding.getInstance();
		} else {
			Constructor<T> constructor = (Constructor<T>) binding.getType()
					.getDeclaredConstructor();
			boolean accessible = constructor.isAccessible();
			if (!accessible) {
				constructor.setAccessible(true);
			}
			T object = constructor.newInstance();
			if (!accessible) {
				constructor.setAccessible(false);
			}
			return object;
		}
	}
}
