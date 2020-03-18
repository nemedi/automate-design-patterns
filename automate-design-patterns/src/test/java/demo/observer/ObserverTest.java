package demo.observer;

import static demo.observer.ObservableFactory.observable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;

import org.junit.Test;

public class ObserverTest {

	@Test
	public void test() throws Throwable {
		Contact contact = observable(Contact.class);
		((Observable) contact).addPropertyChangeListener((event) -> {
			System.out.print(MessageFormat.format("property = {0}, oldValue = {1}, newValue = {2}",
					event.getPropertyName(),
					event.getOldValue(),
					event.getNewValue()));
		});
		ByteArrayOutputStream stream = null;
		stream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(stream));
		contact.setFirstName("Iulian");
		assert "property = firstName, oldValue = null, newValue = Iulian".equals(new String(stream.toByteArray()));
		stream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(stream));
		contact.setLastName("Ilie-Nemedi");
		assert "property = lastName, oldValue = null, newValue = Ilie-Nemedi".equals(new String(stream.toByteArray()));
		stream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(stream));
		contact.setEmail("iilie@axway.com");
		assert "property = email, oldValue = null, newValue = iilie@axway.com".equals(new String(stream.toByteArray()));
	}
}
