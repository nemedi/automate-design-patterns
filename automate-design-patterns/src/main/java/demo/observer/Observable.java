package demo.observer;

import java.beans.PropertyChangeListener;

public interface Observable {

	void addPropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(PropertyChangeListener listener);
}
