// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Set;
import com.google.common.primitives.Primitives;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Map;
import java.util.HashMap;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;

@GwtIncompatible
public final class MutableClassToInstanceMap<B> extends MapConstraints.ConstrainedMap<Class<? extends B>, B> implements ClassToInstanceMap<B>, Serializable
{
    private static final MapConstraint<Class<?>, Object> VALUE_CAN_BE_CAST_TO_KEY;
    
    public static <B> MutableClassToInstanceMap<B> create() {
        return new MutableClassToInstanceMap<B>(new HashMap<Class<? extends B>, B>());
    }
    
    public static <B> MutableClassToInstanceMap<B> create(final Map<Class<? extends B>, B> backingMap) {
        return new MutableClassToInstanceMap<B>(backingMap);
    }
    
    private MutableClassToInstanceMap(final Map<Class<? extends B>, B> delegate) {
        super(delegate, MutableClassToInstanceMap.VALUE_CAN_BE_CAST_TO_KEY);
    }
    
    @CanIgnoreReturnValue
    @Override
    public <T extends B> T putInstance(final Class<T> type, final T value) {
        return cast(type, this.put((Class<? extends B>)type, value));
    }
    
    @Override
    public <T extends B> T getInstance(final Class<T> type) {
        return cast(type, this.get(type));
    }
    
    @CanIgnoreReturnValue
    private static <B, T extends B> T cast(final Class<T> type, final B value) {
        return Primitives.wrap(type).cast(value);
    }
    
    private Object writeReplace() {
        return new SerializedForm(this.delegate());
    }
    
    static {
        VALUE_CAN_BE_CAST_TO_KEY = new MapConstraint<Class<?>, Object>() {
            @Override
            public void checkKeyValue(final Class<?> key, final Object value) {
                cast(key, value);
            }
        };
    }
    
    private static final class SerializedForm<B> implements Serializable
    {
        private final Map<Class<? extends B>, B> backingMap;
        private static final long serialVersionUID = 0L;
        
        SerializedForm(final Map<Class<? extends B>, B> backingMap) {
            this.backingMap = backingMap;
        }
        
        Object readResolve() {
            return MutableClassToInstanceMap.create(this.backingMap);
        }
    }
}
