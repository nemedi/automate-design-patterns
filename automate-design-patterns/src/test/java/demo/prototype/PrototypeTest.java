package demo.prototype;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class PrototypeTest {

	@Test
	public void test() throws Throwable {
		final int maximum = 100;
		Random random = new Random();
		TreeItem<Integer> treeItem = new TreeItem<Integer>(random.nextInt(maximum));
		for (int i = 0; i < 10; i++) {
			treeItem.add(random.nextInt(maximum));
		}
		final List<TreeItem<Integer>> originalItems = new ArrayList<TreeItem<Integer>>();
		treeItem.visit(item -> originalItems.add(item));
		TreeItem<Integer> clone = Cloner.clone(treeItem);
		final List<TreeItem<Integer>> cloneItems = new ArrayList<TreeItem<Integer>>();
		clone.visit(item -> cloneItems.add(item));
		assert cloneItems.size() == originalItems.size();
		for (int i = 0; i < originalItems.size(); i++) {
			assert cloneItems.get(i) != originalItems.get(i)
					&& cloneItems.get(i).getData().equals(originalItems.get(i).getData());
		}
	}
}
