package demo.adapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Adapter {

	public static interface AdapterHandler {
		Object invoke(Object service, Method method, Object[] arguments) throws Throwable;
	}
	
	public static <T> T adapt(Object adaptee, Class<T> contract, AdapterHandler handler) {
		return contract.cast(Proxy.newProxyInstance(contract.getClassLoader(),
				new Class[] {contract},
				new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] arguments)
					throws Throwable {
				return handler.invoke(adaptee, method, arguments);
			}
		}));
	}
	
	public static <T> T adapt(Object service, Class<T> type) {
		return adapt(service, type, (adaptee, method, arguments) ->
			adaptee.getClass().getMethod(method.getName(),
				method.getParameterTypes()).invoke(adaptee, arguments));
	}
}

