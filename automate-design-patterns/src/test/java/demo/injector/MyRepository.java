package demo.injector;

public class MyRepository implements Repository {

	@Override
	public String getMessage() {
		return "Just doing nothing.";
	}

}
