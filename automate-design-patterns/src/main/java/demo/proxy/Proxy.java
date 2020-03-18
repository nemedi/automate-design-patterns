package demo.proxy;

import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class Proxy<T> {

	private T target;
	
	private List<BeforeInterceptor> befores = new ArrayList<BeforeInterceptor>();
	private List<AfterInterceptor> afters = new ArrayList<AfterInterceptor>();
	private List<ThrowingInterceptor> throwings = new ArrayList<ThrowingInterceptor>();
	
	@SuppressWarnings("unchecked")
	private Proxy(T original) {
		MethodInterceptor interceptor = (object, method, arguments, proxy) -> {
			Object result = null;
			try {
				for (BeforeInterceptor before: befores) {
					try {
						before.execute(original, method, arguments);
					} catch (Throwable throwable) {
					}
				}
				result = proxy.invokeSuper(object, arguments);
			} catch (Throwable throwable) {
				for (ThrowingInterceptor throwing : throwings) {
					try {
						throwing.execute(original, method, arguments, throwable);
					} catch (Throwable innerThrowable) {
					}
				}
				throw throwable;
			} finally {
				for (AfterInterceptor after : afters) {
					try {
						after.execute(original, method, arguments, result);
					} catch (Throwable throwable) {
					}
				}
			}
			return result;
		};
		target = (T) original.getClass().cast(
				Enhancer.create(original.getClass(), interceptor));
		
	}
	
	public Proxy<T> before(BeforeInterceptor before) {
		befores.add(before);
		return this;
	}
	
	public Proxy<T> after(AfterInterceptor after) {
		afters.add(after);
		return this;
	}

	public Proxy<T> throwing(ThrowingInterceptor throwing) {
		throwings.add(throwing);
		return this;
	}
	
	public static <T> Proxy<T> proxy(T original) {
		return new Proxy<T>(original);
	}
	
	public T getTarget() {
		return target;
	}
	
}
