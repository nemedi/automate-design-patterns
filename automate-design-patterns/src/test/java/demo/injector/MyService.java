package demo.injector;

public class MyService implements Service {
	
	@Inject
	private Repository repository;

	@Override
	public void doSomething() {
		System.out.print(repository.getMessage());
	}

}
