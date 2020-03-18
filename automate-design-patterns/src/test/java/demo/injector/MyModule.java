package demo.injector;

import demo.injector.Binder;
import demo.injector.Module;

public class MyModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(Service.class).to(MyService.class);
		binder.bind(Repository.class).to(MyRepository.class);
	}

}
