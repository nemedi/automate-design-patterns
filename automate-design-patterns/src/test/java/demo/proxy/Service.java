package demo.proxy;

import java.util.Random;

public class Service {

	public int countStars() throws Throwable {
		int number = new Random().nextInt();
		if (number % 10 == 0) {
			throw new RuntimeException("Something went wrong.");
		}
		return number;
	}
}
