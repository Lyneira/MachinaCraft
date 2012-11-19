package me.lyneira.MachinaCore.event;

import gnu.trove.map.hash.THashMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.event.EventException;

import me.lyneira.MachinaCore.MachinaCore;
import me.lyneira.MachinaCore.machina.MachinaController;

/**
 * Dispatches events to the controller of the machina they were intended for.
 * 
 * Inspired by and loosely based on Bukkit's event system (notably
 * org.bukkit.plugin.java.JavaPluginLoader for the reflection magic), which is
 * based on lahwran's fevents.
 * 
 * @author Lyneira
 */
public final class EventDispatcher {
    private final Map<Class<? extends MachinaController>, EventExecutor> executors = new THashMap<Class<? extends MachinaController>, EventExecutor>();

    public void dispatch(MachinaController controller, Event event) throws EventException {
        Class<? extends MachinaController> controllerClass = controller.getClass();
        EventExecutor executor = executors.get(controllerClass);
        if (executor == null) {
            executor = getExecutor(controllerClass, event.getClass());
            executors.put(controllerClass, executor);
        }
        executor.execute(controller, event);
    }

    private EventExecutor getExecutor(Class<? extends MachinaController> controllerClass, Class<? extends Event> eventClass) {
        Set<Method> methods;
        try {
            Method[] publicMethods = controllerClass.getMethods();
            methods = new HashSet<Method>(publicMethods.length, Float.MAX_VALUE);
            for (Method method : publicMethods) {
                methods.add(method);
            }
            for (Method method : controllerClass.getDeclaredMethods()) {
                methods.add(method);
            }
        } catch (NoClassDefFoundError e) {
            MachinaCore.severe("Failed to set up event registration for " + controllerClass + " because " + e.getMessage() + " does not exist!");
            return NONE;
        }

        for (final Method method : methods) {
            final EventHandler eventHandler = method.getAnnotation(EventHandler.class);
            if (eventHandler == null)
                continue;
            final Class<?> checkClass = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(checkClass) || method.getParameterTypes().length != 1) {
                MachinaCore.severe(controllerClass + " attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\"");
                continue;
            }
            final Class<? extends Event> methodEventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            if (!methodEventClass.isAssignableFrom(eventClass)) {
                continue;
            }

            return new EventExecutor() {
                @Override
                public void execute(MachinaController controller, Event event) throws EventException {
                    try {
                        if (!methodEventClass.isAssignableFrom(event.getClass())) {
                            return;
                        }
                        method.invoke(controller, event);
                    } catch (InvocationTargetException ex) {
                        throw new EventException(ex.getCause());
                    } catch (Throwable t) {
                        throw new EventException(t);
                    }
                }
            };
        }
        return NONE;
    }

    private static final EventExecutor NONE = new EventExecutor() {
        @Override
        public void execute(MachinaController controller, Event event) {
        }
    };
}
