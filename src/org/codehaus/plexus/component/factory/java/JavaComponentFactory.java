// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.factory.java;

import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import java.lang.reflect.Modifier;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;

public class JavaComponentFactory extends AbstractComponentFactory
{
    public Object newInstance(final ComponentDescriptor componentDescriptor, final ClassRealm classRealm, final PlexusContainer container) throws ComponentInstantiationException {
        Class implementationClass = null;
        try {
            final String implementation = componentDescriptor.getImplementation();
            implementationClass = classRealm.loadClass(implementation);
            final int modifiers = implementationClass.getModifiers();
            if (Modifier.isInterface(modifiers)) {
                throw new ComponentInstantiationException("Cannot instanciate implementation '" + implementation + "' because the class is a interface.");
            }
            if (Modifier.isAbstract(modifiers)) {
                throw new ComponentInstantiationException("Cannot instanciate implementation '" + implementation + "' because the class is abstract.");
            }
            final Object instance = implementationClass.newInstance();
            return instance;
        }
        catch (InstantiationException e) {
            throw this.makeException(classRealm, componentDescriptor, implementationClass, e);
        }
        catch (ClassNotFoundException e2) {
            throw this.makeException(classRealm, componentDescriptor, implementationClass, e2);
        }
        catch (IllegalAccessException e3) {
            throw this.makeException(classRealm, componentDescriptor, implementationClass, e3);
        }
        catch (LinkageError e4) {
            throw this.makeException(classRealm, componentDescriptor, implementationClass, e4);
        }
    }
    
    private ComponentInstantiationException makeException(final ClassRealm componentClassRealm, final ComponentDescriptor componentDescriptor, final Class implementationClass, final Throwable e) {
        componentClassRealm.display();
        final String msg = "Could not instanciate component: " + componentDescriptor.getHumanReadableKey();
        return new ComponentInstantiationException(msg, e);
    }
}
