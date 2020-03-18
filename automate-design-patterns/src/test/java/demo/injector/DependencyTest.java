package demo.injector;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class DependencyTest {

	@Test
	public void test() throws Throwable {
		Injector.setModule(new MyModule());
		Client client = new Client();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(stream));
		client.doSomething();
		assert "Just doing nothing.".equals(new String(stream.toByteArray()));
	}
}
