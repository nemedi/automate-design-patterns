package demo.observer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class PropertyChangeInterceptor implements MethodInterceptor {

	private PropertyChangeSupport propertyChangeSupport;

	public void setSubject(Object target) {
		this.propertyChangeSupport = new PropertyChangeSupport(target);
	}

	private void firePropertyChange(String property, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
	}

	private Object tryForGetter(String setterName, Object target) {
		String getterName = "get" + setterName.substring(3);
		try {
			return target.getClass().getMethod(getterName, new Class<?>[] {}).invoke(target, (Object[]) null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			return null;
		}
	}
	
	@Override
    public Object intercept(Object target, Method method, Object[] arguments, MethodProxy proxy) throws Throwable {
        Object targetReturn = null;
        if ("addPropertyChangeListener".equals(method.getName())) {
        	propertyChangeSupport.addPropertyChangeListener((PropertyChangeListener) arguments[0]);
            return null;
        } else if ("removePropertyChangeListener".equals(method.getName())) {
        	propertyChangeSupport.removePropertyChangeListener((PropertyChangeListener) arguments[0]);
        	return null;
        }
        Object oldValue = null;
        String name = method.getName();
        boolean isSetter = (name.startsWith("set") && arguments.length == 1 && method.getReturnType() == Void.TYPE);
        if (isSetter) {
            oldValue = tryForGetter(name, target);
        }
        if (!Modifier.isAbstract(method.getModifiers())) {
            targetReturn = proxy.invokeSuper(target, arguments);
        }
        if (isSetter) {
            String propName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
            firePropertyChange(propName, oldValue, arguments[0]);
        }
        return targetReturn;
    }

}