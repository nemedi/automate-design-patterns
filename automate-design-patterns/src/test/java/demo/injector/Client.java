package demo.injector;

import demo.injector.Inject;

public class Client {

	@Inject
	private Service service;
	
	public void doSomething() {
		this.service.doSomething();
	}
}
