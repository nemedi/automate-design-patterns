package demo.decorator;

import static demo.decorator.Decorator.decorate;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class DecoratorTest {

	@SuppressWarnings("unchecked")
	@Test
	public void test() throws Throwable {
		List<String> list = Arrays.asList("a", "b", "c");
		VisitableList<String> visitableList = decorate(list, VisitableList.class);
		assert visitableList instanceof VisitableList;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(stream));
		visitableList.foreach(System.out::print);
		assert "abc".equals(new String(stream.toByteArray()));
	}

}
