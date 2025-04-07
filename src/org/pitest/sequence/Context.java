// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.sequence;

import org.pitest.functional.Option;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context<T>
{
    private final boolean debug;
    private final Map<Slot<?>, Object> slots;
    private final List<T> sequence;
    private int position;
    
    Context(final Map<Slot<?>, Object> slots, final List<T> sequence, final int position, final boolean debug) {
        this.slots = slots;
        this.sequence = sequence;
        this.position = position;
        this.debug = debug;
    }
    
    public static <T> Context<T> start(final List<T> sequence) {
        return start(sequence, false);
    }
    
    public static <T> Context<T> start(final List<T> sequence, final boolean debug) {
        return new Context<T>(new HashMap<Slot<?>, Object>(), sequence, -1, debug);
    }
    
    public <S> boolean store(final SlotWrite<S> slot, final S value) {
        this.slots.put(slot.slot(), value);
        return true;
    }
    
    public <S> Option<S> retrieve(final SlotRead<S> slot) {
        return Option.some(this.slots.get(slot.slot()));
    }
    
    void moveForward() {
        ++this.position;
    }
    
    public int position() {
        return this.position;
    }
    
    public void debug(final String msg) {
        if (this.debug) {
            System.out.println(msg + " at " + this.position + " for " + this.sequence.get(this.position));
        }
    }
}
