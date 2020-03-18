package demo.proxy;

import java.lang.reflect.Method;

@FunctionalInterface
public interface BeforeInterceptor {

	void execute(Object target, Method method, Object[] arguments) throws Throwable;
}
