package demo.proxy;

import java.lang.reflect.Method;

@FunctionalInterface
public interface ThrowingInterceptor {

	void execute(Object target, Method method, Object[] arguments, Throwable throwable) throws Throwable;
}
