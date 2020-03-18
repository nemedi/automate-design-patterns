package demo.adapter;

import java.text.MessageFormat;

public class Service {

	public String sayHello(String name) {
		return MessageFormat.format("Hello {0}!", name);
	}
}
