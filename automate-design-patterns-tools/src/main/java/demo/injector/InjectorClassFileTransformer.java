package demo.injector;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;

public class InjectorClassFileTransformer implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain,
			byte[] bytes) throws IllegalClassFormatException {
		ClassPool pool = ClassPool.getDefault();
		CtClass type = null;
		try {
			type = pool.makeClass(new ByteArrayInputStream(bytes));
			type.defrost();
			if (Arrays.stream(type.getDeclaredFields())
				.filter(field -> {
					try {
						return field.getAnnotation(Inject.class) != null;
					} catch (ClassNotFoundException e) {
						return false;
					}
				})
				.findAny()
				.isPresent()) {
				Arrays.stream(type.getDeclaredConstructors())
					.forEach(constructor -> {
					try {
						constructor.insertAfter(Injector.class.getName() + ".resolveDependencies($0);");
					} catch (CannotCompileException e) {
						e.printStackTrace();
					}
				});
			}
			bytes = type.toBytecode();
		} catch (Exception e) {
			throw new IllegalClassFormatException(e.getMessage());
		} finally {
			if (type != null) {
				type.detach();
			}
		}
		return bytes;
	}

}
