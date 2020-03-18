package demo.adapter;

import static demo.adapter.Adapter.adapt;

import org.junit.Test;

public class AdapterTest {

	@Test
	public void test() throws Throwable {
		Service service = new Service();
		Contract contract = adapt(service, Contract.class);
		String message = contract.sayHello("Iulian");
		assert "Hello Iulian!".equals(message);
	}

}
