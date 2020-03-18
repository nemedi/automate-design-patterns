package demo.prototype;

import java.util.function.Consumer;

public class TreeItem<T extends Comparable<T>> {

	private T data;
	private TreeItem<T> parent;
	private TreeItem<T> left;
	private TreeItem<T> right;
	
	private TreeItem() {
	}
	
	private TreeItem(T data, TreeItem<T> parent) {
		this();
		this.data = data;
		this.parent = parent;
	}

	public TreeItem(T data) {
		this(data, null);
	}
	
	public TreeItem<T> add(T data) {
		TreeItem<T> item = null;
		if (data.compareTo(this.data) < 0) {
			if (this.left == null) {
				this.left = item = new TreeItem<T>(data, this);
			} else {
				item = this.left.add(data);
			}
		} else if (data.compareTo(this.data) >= 0) {
			if (this.right == null) {
				this.right = item = new TreeItem<T>(data, this);
			} else {
				item = this.right.add(data);
			}
		}
		return item;
	}
	
	public void visit(Consumer<TreeItem<T>> consumer) {
		if (this.left != null) {
			this.left.visit(consumer);
		}
		consumer.accept(this);
		if (this.right != null) {
			this.right.visit(consumer);
		}
	}
	
	public T getData() {
		return data;
	}
	
	public TreeItem<T> getParent() {
		return parent;
	}
	
	public TreeItem<T> getLeft() {
		return left;
	}
	
	public TreeItem<T> getRight() {
		return right;
	}
	
	@Override
	public String toString() {
		return data.toString();
	}
}
