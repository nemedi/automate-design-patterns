package demo.observer;

import net.sf.cglib.proxy.Enhancer;

public final class ObservableFactory {
	
	@SuppressWarnings("unchecked")
	public static <T> T observable(Class<T> type) {
		PropertyChangeInterceptor interceptor = new PropertyChangeInterceptor();
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(type);
		enhancer.setCallback(interceptor);
		enhancer.setInterfaces(new Class[] { Observable.class });
		T bean = (T) enhancer.create();
		interceptor.setSubject(bean);
		return bean;
	}
}
