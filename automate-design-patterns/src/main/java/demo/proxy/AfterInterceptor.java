package demo.proxy;

import java.lang.reflect.Method;

@FunctionalInterface
public interface AfterInterceptor {

	void execute(Object target, Method method, Object[] arguments, Object result)
			throws Throwable;
}
