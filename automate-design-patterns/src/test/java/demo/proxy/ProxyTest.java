package demo.proxy;

import static demo.proxy.Proxy.proxy;

import org.junit.Test;

public class ProxyTest {

	@Test
	public void test() throws Throwable {
		Service service = proxy(new Service())
				.before((target, method, arguments) -> {
					System.out.println("before");
				})
				.after((target, method, arguments, result) -> {
					System.out.println("after: " + result);
				})
				.throwing((target, method, arguments, throwable) -> {
					System.out.println("throwing");
				})
				.getTarget();
		for (int i = 0; i < 10; i++) {
			try {
				service.countStars();
			} catch (Exception e) {
			}
		}
	}
}
