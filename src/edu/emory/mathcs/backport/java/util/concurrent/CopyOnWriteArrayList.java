// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.ConcurrentModificationException;
import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ListIterator;
import java.util.Iterator;
import edu.emory.mathcs.backport.java.util.Arrays;
import java.util.Collection;
import java.io.Serializable;
import java.util.RandomAccess;
import java.util.List;

public class CopyOnWriteArrayList implements List, RandomAccess, Cloneable, Serializable
{
    private static final long serialVersionUID = 8673264195747942595L;
    private transient volatile Object[] array;
    
    public CopyOnWriteArrayList() {
        this.setArray(new Object[0]);
    }
    
    public CopyOnWriteArrayList(final Collection c) {
        Object[] array = c.toArray();
        if (array.getClass() != Object[].class) {
            array = Arrays.copyOf(array, array.length, Object[].class);
        }
        this.setArray(array);
    }
    
    public CopyOnWriteArrayList(final Object[] array) {
        this.setArray(Arrays.copyOf(array, array.length, Object[].class));
    }
    
    final Object[] getArray() {
        return this.array;
    }
    
    final void setArray(final Object[] array) {
        this.array = array;
    }
    
    public int size() {
        return this.getArray().length;
    }
    
    public boolean isEmpty() {
        return this.getArray().length == 0;
    }
    
    private static int search(final Object[] array, final Object subject, int pos, final int end) {
        if (subject == null) {
            while (pos < end) {
                if (array[pos] == null) {
                    return pos;
                }
                ++pos;
            }
        }
        else {
            while (pos < end) {
                if (subject.equals(array[pos])) {
                    return pos;
                }
                ++pos;
            }
        }
        return -1;
    }
    
    private static int reverseSearch(final Object[] array, final Object subject, final int start, int pos) {
        if (subject == null) {
            --pos;
            while (pos >= start) {
                if (array[pos] == null) {
                    return pos;
                }
                --pos;
            }
        }
        else {
            --pos;
            while (pos >= start) {
                if (subject.equals(array[pos])) {
                    return pos;
                }
                --pos;
            }
        }
        return -1;
    }
    
    public boolean contains(final Object o) {
        final Object[] array = this.getArray();
        return search(array, o, 0, array.length) >= 0;
    }
    
    public Iterator iterator() {
        return new COWIterator(this.getArray(), 0);
    }
    
    public Object[] toArray() {
        final Object[] array = this.getArray();
        return Arrays.copyOf(array, array.length, Object[].class);
    }
    
    public Object[] toArray(final Object[] a) {
        final Object[] array = this.getArray();
        final int length = array.length;
        if (a.length < length) {
            return Arrays.copyOf(array, length, a.getClass());
        }
        System.arraycopy(array, 0, a, 0, length);
        if (a.length > length) {
            a[length] = null;
        }
        return a;
    }
    
    public boolean add(final Object o) {
        synchronized (this) {
            final Object[] oldarr = this.getArray();
            final int length = oldarr.length;
            final Object[] newarr = new Object[length + 1];
            System.arraycopy(oldarr, 0, newarr, 0, length);
            newarr[length] = o;
            this.setArray(newarr);
            return true;
        }
    }
    
    public boolean addIfAbsent(final Object o) {
        synchronized (this) {
            final Object[] oldarr = this.getArray();
            final int length = oldarr.length;
            if (search(this.array, o, 0, length) >= 0) {
                return false;
            }
            final Object[] newarr = new Object[length + 1];
            System.arraycopy(oldarr, 0, newarr, 0, length);
            newarr[length] = o;
            this.setArray(newarr);
            return true;
        }
    }
    
    public int addAllAbsent(final Collection c) {
        final Object[] arr = c.toArray();
        if (arr.length == 0) {
            return 0;
        }
        synchronized (this) {
            final Object[] oldarr = this.getArray();
            final int oldlength = oldarr.length;
            final Object[] tmp = new Object[arr.length];
            int added = 0;
            for (int i = 0; i < arr.length; ++i) {
                final Object o = arr[i];
                if (search(oldarr, o, 0, oldlength) < 0 && search(tmp, o, 0, added) < 0) {
                    tmp[added++] = o;
                }
            }
            if (added == 0) {
                return 0;
            }
            final Object[] newarr = new Object[oldlength + added];
            System.arraycopy(oldarr, 0, newarr, 0, oldlength);
            System.arraycopy(tmp, 0, newarr, oldlength, added);
            this.setArray(newarr);
            return added;
        }
    }
    
    public boolean remove(final Object o) {
        synchronized (this) {
            final Object[] array = this.getArray();
            final int length = array.length;
            final int pos = search(array, o, 0, length);
            if (pos < 0) {
                return false;
            }
            final Object[] newarr = new Object[length - 1];
            final int moved = length - pos - 1;
            if (pos > 0) {
                System.arraycopy(array, 0, newarr, 0, pos);
            }
            if (moved > 0) {
                System.arraycopy(array, pos + 1, newarr, pos, moved);
            }
            this.setArray(newarr);
            return true;
        }
    }
    
    public boolean containsAll(final Collection c) {
        final Object[] array = this.getArray();
        final Iterator itr = c.iterator();
        while (itr.hasNext()) {
            if (search(array, itr.next(), 0, array.length) < 0) {
                return false;
            }
        }
        return true;
    }
    
    public boolean addAll(final Collection c) {
        final Object[] ca = c.toArray();
        if (ca.length == 0) {
            return false;
        }
        synchronized (this) {
            final Object[] oldarr = this.getArray();
            final int length = oldarr.length;
            final Object[] newarr = new Object[length + ca.length];
            System.arraycopy(oldarr, 0, newarr, 0, length);
            final int pos = length;
            System.arraycopy(ca, 0, newarr, pos, ca.length);
            this.setArray(newarr);
            return true;
        }
    }
    
    public boolean addAll(final int index, final Collection c) {
        final Object[] ca = c.toArray();
        synchronized (this) {
            final Object[] oldarr = this.getArray();
            final int length = oldarr.length;
            if (index < 0 || index > length) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + length);
            }
            if (ca.length == 0) {
                return false;
            }
            final Object[] newarr = new Object[length + ca.length];
            final int moved = length - index;
            System.arraycopy(oldarr, 0, newarr, 0, index);
            final int pos = length;
            System.arraycopy(ca, 0, newarr, index, ca.length);
            if (moved > 0) {
                System.arraycopy(oldarr, index, newarr, index + ca.length, moved);
            }
            this.setArray(newarr);
            return true;
        }
    }
    
    public boolean removeAll(final Collection c) {
        if (c.isEmpty()) {
            return false;
        }
        synchronized (this) {
            final Object[] array = this.getArray();
            final int length = array.length;
            final Object[] tmp = new Object[length];
            int newlen = 0;
            for (final Object o : array) {
                if (!c.contains(o)) {
                    tmp[newlen++] = o;
                }
            }
            if (newlen == length) {
                return false;
            }
            final Object[] newarr = new Object[newlen];
            System.arraycopy(tmp, 0, newarr, 0, newlen);
            this.setArray(newarr);
            return true;
        }
    }
    
    public boolean retainAll(final Collection c) {
        synchronized (this) {
            final Object[] array = this.getArray();
            final int length = array.length;
            final Object[] tmp = new Object[length];
            int newlen = 0;
            for (final Object o : array) {
                if (c.contains(o)) {
                    tmp[newlen++] = o;
                }
            }
            if (newlen == length) {
                return false;
            }
            final Object[] newarr = new Object[newlen];
            System.arraycopy(tmp, 0, newarr, 0, newlen);
            this.setArray(newarr);
            return true;
        }
    }
    
    public void clear() {
        this.setArray(new Object[0]);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        final ListIterator itr = ((List)o).listIterator();
        final Object[] array = this.getArray();
        final int length = array.length;
        int idx = 0;
        while (idx < length && itr.hasNext()) {
            final Object o2 = array[idx++];
            final Object o3 = itr.next();
            if (!eq(o2, o3)) {
                return false;
            }
        }
        return idx == length && !itr.hasNext();
    }
    
    public int hashCode() {
        int hashCode = 1;
        for (final Object o : this.getArray()) {
            hashCode = 31 * hashCode + ((o == null) ? 0 : o.hashCode());
        }
        return hashCode;
    }
    
    public Object get(final int index) {
        return this.getArray()[index];
    }
    
    public Object set(final int index, final Object element) {
        synchronized (this) {
            final Object[] oldarr = this.getArray();
            final int length = oldarr.length;
            final Object oldVal = oldarr[index];
            if (oldVal == element) {
                this.setArray(oldarr);
            }
            else {
                final Object[] newarr = new Object[length];
                System.arraycopy(oldarr, 0, newarr, 0, length);
                newarr[index] = element;
                this.setArray(newarr);
            }
            return oldVal;
        }
    }
    
    public void add(final int index, final Object element) {
        synchronized (this) {
            final Object[] oldarr = this.getArray();
            final int length = oldarr.length;
            if (index < 0 || index > length) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + length);
            }
            final Object[] newarr = new Object[length + 1];
            final int moved = length - index;
            System.arraycopy(oldarr, 0, newarr, 0, index);
            newarr[index] = element;
            if (moved > 0) {
                System.arraycopy(oldarr, index, newarr, index + 1, moved);
            }
            this.setArray(newarr);
        }
    }
    
    public Object remove(final int index) {
        synchronized (this) {
            final Object[] array = this.getArray();
            final int length = array.length;
            if (index < 0 || index >= length) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + length);
            }
            final Object result = array[index];
            final Object[] newarr = new Object[length - 1];
            final int moved = length - index - 1;
            if (index > 0) {
                System.arraycopy(array, 0, newarr, 0, index);
            }
            if (moved > 0) {
                System.arraycopy(array, index + 1, newarr, index, moved);
            }
            this.setArray(newarr);
            return result;
        }
    }
    
    public int indexOf(final Object o) {
        final Object[] array = this.getArray();
        return search(array, o, 0, array.length);
    }
    
    public int indexOf(final Object o, final int index) {
        final Object[] array = this.getArray();
        return search(array, o, index, array.length);
    }
    
    public int lastIndexOf(final Object o) {
        final Object[] array = this.getArray();
        return reverseSearch(array, o, 0, array.length);
    }
    
    public int lastIndexOf(final Object o, final int index) {
        final Object[] array = this.getArray();
        return reverseSearch(array, o, 0, index);
    }
    
    public ListIterator listIterator() {
        return new COWIterator(this.getArray(), 0);
    }
    
    public ListIterator listIterator(final int index) {
        final Object[] array = this.getArray();
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + array.length);
        }
        return new COWIterator(array, index);
    }
    
    public List subList(final int fromIndex, final int toIndex) {
        final Object[] array = this.getArray();
        if (fromIndex < 0 || toIndex > array.length || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        return new COWSubList(fromIndex, toIndex - fromIndex);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        final Object[] array = this.getArray();
        final int length = array.length;
        out.writeInt(length);
        for (int i = 0; i < length; ++i) {
            out.writeObject(array[i]);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final int length = in.readInt();
        final Object[] array = new Object[length];
        for (int i = 0; i < length; ++i) {
            array[i] = in.readObject();
        }
        this.setArray(array);
    }
    
    public String toString() {
        final Object[] array = this.getArray();
        final int length = array.length;
        final StringBuffer buf = new StringBuffer();
        buf.append('[');
        for (int i = 0; i < length; ++i) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(array[i]);
        }
        buf.append(']');
        return buf.toString();
    }
    
    private static boolean eq(final Object o1, final Object o2) {
        return (o1 == null) ? (o2 == null) : o1.equals(o2);
    }
    
    static class COWIterator implements ListIterator
    {
        final Object[] array;
        int cursor;
        
        COWIterator(final Object[] array, final int cursor) {
            this.array = array;
            this.cursor = cursor;
        }
        
        public boolean hasNext() {
            return this.cursor < this.array.length;
        }
        
        public boolean hasPrevious() {
            return this.cursor > 0;
        }
        
        public int nextIndex() {
            return this.cursor;
        }
        
        public Object next() {
            try {
                return this.array[this.cursor++];
            }
            catch (IndexOutOfBoundsException e) {
                --this.cursor;
                throw new NoSuchElementException();
            }
        }
        
        public int previousIndex() {
            return this.cursor - 1;
        }
        
        public Object previous() {
            try {
                final Object[] array = this.array;
                final int cursor = this.cursor - 1;
                this.cursor = cursor;
                return array[cursor];
            }
            catch (IndexOutOfBoundsException e) {
                ++this.cursor;
                throw new NoSuchElementException();
            }
        }
        
        public void add(final Object val) {
            throw new UnsupportedOperationException();
        }
        
        public void set(final Object val) {
            throw new UnsupportedOperationException();
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    class COWSubList implements Serializable, List
    {
        private static final long serialVersionUID = -8660955369431018984L;
        final int offset;
        int length;
        transient Object[] expectedArray;
        
        COWSubList(final int offset, final int length) {
            this.offset = offset;
            this.length = length;
            this.expectedArray = CopyOnWriteArrayList.this.getArray();
        }
        
        public int size() {
            return this.length;
        }
        
        public boolean isEmpty() {
            return this.length == 0;
        }
        
        public boolean contains(final Object o) {
            return search(CopyOnWriteArrayList.this.getArray(), o, this.offset, this.offset + this.length) >= 0;
        }
        
        public Iterator iterator() {
            return this.listIterator();
        }
        
        public Object[] toArray() {
            final Object[] array = CopyOnWriteArrayList.this.getArray();
            final Object[] newarr = new Object[this.length];
            System.arraycopy(array, this.offset, newarr, 0, this.length);
            return newarr;
        }
        
        public Object[] toArray(Object[] a) {
            final Object[] array = CopyOnWriteArrayList.this.getArray();
            if (a.length < this.length) {
                a = (Object[])Array.newInstance(a.getClass().getComponentType(), this.length);
                System.arraycopy(array, this.offset, a, 0, this.length);
            }
            else {
                System.arraycopy(array, this.offset, a, 0, this.length);
                if (a.length > this.length) {
                    a[this.length] = null;
                }
            }
            return a;
        }
        
        public boolean add(final Object o) {
            this.add(this.length, o);
            return true;
        }
        
        public boolean remove(final Object o) {
            synchronized (CopyOnWriteArrayList.this) {
                final Object[] array = CopyOnWriteArrayList.this.getArray();
                if (array != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                final int fullLength = array.length;
                final int pos = search(array, o, this.offset, this.length);
                if (pos < 0) {
                    return false;
                }
                final Object[] newarr = new Object[fullLength - 1];
                final int moved = this.length - pos - 1;
                if (pos > 0) {
                    System.arraycopy(array, 0, newarr, 0, pos);
                }
                if (moved > 0) {
                    System.arraycopy(array, pos + 1, newarr, pos, moved);
                }
                CopyOnWriteArrayList.this.setArray(newarr);
                this.expectedArray = newarr;
                --this.length;
                return true;
            }
        }
        
        public boolean containsAll(final Collection c) {
            final Object[] array = CopyOnWriteArrayList.this.getArray();
            final Iterator itr = c.iterator();
            while (itr.hasNext()) {
                if (search(array, itr.next(), this.offset, this.length) < 0) {
                    return false;
                }
            }
            return true;
        }
        
        public boolean addAll(final Collection c) {
            return this.addAll(this.length, c);
        }
        
        public boolean addAll(final int index, final Collection c) {
            final int added = c.size();
            synchronized (CopyOnWriteArrayList.this) {
                if (index < 0 || index >= this.length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.length);
                }
                final Object[] oldarr = CopyOnWriteArrayList.this.getArray();
                if (oldarr != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                if (added == 0) {
                    return false;
                }
                final int fullLength = oldarr.length;
                final Object[] newarr = new Object[fullLength + added];
                int newpos;
                final int pos = newpos = this.offset + index;
                System.arraycopy(oldarr, 0, newarr, 0, pos);
                final int rem = fullLength - pos;
                final Iterator itr = c.iterator();
                while (itr.hasNext()) {
                    newarr[newpos++] = itr.next();
                }
                if (rem > 0) {
                    System.arraycopy(oldarr, pos, newarr, newpos, rem);
                }
                CopyOnWriteArrayList.this.setArray(newarr);
                this.expectedArray = newarr;
                this.length += added;
                return true;
            }
        }
        
        public boolean removeAll(final Collection c) {
            if (c.isEmpty()) {
                return false;
            }
            synchronized (CopyOnWriteArrayList.this) {
                final Object[] array = CopyOnWriteArrayList.this.getArray();
                if (array != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                final int fullLength = array.length;
                final Object[] tmp = new Object[this.length];
                int retained = 0;
                for (int i = this.offset; i < this.offset + this.length; ++i) {
                    final Object o = array[i];
                    if (!c.contains(o)) {
                        tmp[retained++] = o;
                    }
                }
                if (retained == this.length) {
                    return false;
                }
                final Object[] newarr = new Object[fullLength + retained - this.length];
                final int moved = fullLength - this.offset - this.length;
                if (this.offset > 0) {
                    System.arraycopy(array, 0, newarr, 0, this.offset);
                }
                if (retained > 0) {
                    System.arraycopy(tmp, 0, newarr, this.offset, retained);
                }
                if (moved > 0) {
                    System.arraycopy(array, this.offset + this.length, newarr, this.offset + retained, moved);
                }
                CopyOnWriteArrayList.this.setArray(newarr);
                this.expectedArray = newarr;
                this.length = retained;
                return true;
            }
        }
        
        public boolean retainAll(final Collection c) {
            synchronized (CopyOnWriteArrayList.this) {
                final Object[] array = CopyOnWriteArrayList.this.getArray();
                if (array != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                final int fullLength = array.length;
                final Object[] tmp = new Object[this.length];
                int retained = 0;
                for (int i = this.offset; i < this.offset + this.length; ++i) {
                    final Object o = array[i];
                    if (c.contains(o)) {
                        tmp[retained++] = o;
                    }
                }
                if (retained == this.length) {
                    return false;
                }
                final Object[] newarr = new Object[fullLength + retained - this.length];
                final int moved = fullLength - this.offset - this.length;
                if (this.offset > 0) {
                    System.arraycopy(array, 0, newarr, 0, this.offset);
                }
                if (retained > 0) {
                    System.arraycopy(tmp, 0, newarr, this.offset, retained);
                }
                if (moved > 0) {
                    System.arraycopy(array, this.offset + this.length, newarr, this.offset + retained, moved);
                }
                CopyOnWriteArrayList.this.setArray(newarr);
                this.expectedArray = newarr;
                this.length = retained;
                return true;
            }
        }
        
        public void clear() {
            synchronized (CopyOnWriteArrayList.this) {
                final Object[] array = CopyOnWriteArrayList.this.getArray();
                if (array != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                final int fullLength = array.length;
                final Object[] newarr = new Object[fullLength - this.length];
                final int moved = fullLength - this.offset - this.length;
                if (this.offset > 0) {
                    System.arraycopy(array, 0, newarr, 0, this.offset);
                }
                if (moved > 0) {
                    System.arraycopy(array, this.offset + this.length, newarr, this.offset, moved);
                }
                CopyOnWriteArrayList.this.setArray(newarr);
                this.expectedArray = newarr;
                this.length = 0;
            }
        }
        
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof List)) {
                return false;
            }
            final Object[] array;
            final int last;
            synchronized (CopyOnWriteArrayList.this) {
                array = CopyOnWriteArrayList.this.getArray();
                if (array != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                last = this.offset + this.length;
            }
            final ListIterator itr = ((List)o).listIterator();
            final int idx = this.offset;
            while (idx < last && itr.hasNext()) {
                final Object o2 = array[idx];
                final Object o3 = itr.next();
                if (!eq(o2, o3)) {
                    return false;
                }
            }
            return idx == last && !itr.hasNext();
        }
        
        public int hashCode() {
            int hashCode = 1;
            final Object[] array;
            final int last;
            synchronized (CopyOnWriteArrayList.this) {
                array = CopyOnWriteArrayList.this.getArray();
                if (array != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                last = this.offset + this.length;
            }
            for (int i = this.offset; i < last; ++i) {
                final Object o = array[i];
                hashCode = 31 * hashCode + ((o == null) ? 0 : o.hashCode());
            }
            return hashCode;
        }
        
        public Object get(final int index) {
            return CopyOnWriteArrayList.this.getArray()[this.offset + index];
        }
        
        public Object set(final int index, final Object element) {
            synchronized (CopyOnWriteArrayList.this) {
                if (index < 0 || index >= this.length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.length);
                }
                final Object[] oldarr = CopyOnWriteArrayList.this.getArray();
                if (oldarr != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                final int fullLength = oldarr.length;
                final Object oldVal = oldarr[this.offset + index];
                if (oldVal == element) {
                    CopyOnWriteArrayList.this.setArray(oldarr);
                }
                else {
                    final Object[] newarr = new Object[fullLength];
                    System.arraycopy(oldarr, 0, newarr, 0, fullLength);
                    newarr[this.offset + index] = element;
                    CopyOnWriteArrayList.this.setArray(newarr);
                    this.expectedArray = newarr;
                }
                return oldVal;
            }
        }
        
        public void add(final int index, final Object element) {
            synchronized (CopyOnWriteArrayList.this) {
                if (index < 0 || index > this.length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.length);
                }
                final Object[] oldarr = CopyOnWriteArrayList.this.getArray();
                if (oldarr != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                final int fullLength = oldarr.length;
                final Object[] newarr = new Object[fullLength + 1];
                final int pos = this.offset + index;
                final int moved = fullLength - pos;
                System.arraycopy(oldarr, 0, newarr, 0, pos);
                newarr[pos] = element;
                if (moved > 0) {
                    System.arraycopy(oldarr, pos, newarr, pos + 1, moved);
                }
                CopyOnWriteArrayList.this.setArray(newarr);
                this.expectedArray = newarr;
                ++this.length;
            }
        }
        
        public Object remove(final int index) {
            synchronized (CopyOnWriteArrayList.this) {
                if (index < 0 || index >= this.length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.length);
                }
                final Object[] array = CopyOnWriteArrayList.this.getArray();
                if (array != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                final int fullLength = array.length;
                final int pos = this.offset + index;
                final Object result = array[pos];
                final Object[] newarr = new Object[fullLength - 1];
                final int moved = fullLength - pos - 1;
                if (index > 0) {
                    System.arraycopy(array, 0, newarr, 0, pos);
                }
                if (moved > 0) {
                    System.arraycopy(array, pos + 1, newarr, pos, moved);
                }
                CopyOnWriteArrayList.this.setArray(newarr);
                this.expectedArray = newarr;
                --this.length;
                return result;
            }
        }
        
        public int indexOf(final Object o) {
            final int pos = search(CopyOnWriteArrayList.this.getArray(), o, this.offset, this.offset + this.length);
            return (pos >= 0) ? (pos - this.offset) : -1;
        }
        
        public int indexOf(final Object o, final int index) {
            final int pos = search(CopyOnWriteArrayList.this.getArray(), o, this.offset + index, this.offset + this.length) - this.offset;
            return (pos >= 0) ? (pos - this.offset) : -1;
        }
        
        public int lastIndexOf(final Object o) {
            final int pos = reverseSearch(CopyOnWriteArrayList.this.getArray(), o, this.offset, this.offset + this.length) - this.offset;
            return (pos >= 0) ? (pos - this.offset) : -1;
        }
        
        public int lastIndexOf(final Object o, final int index) {
            final int pos = reverseSearch(CopyOnWriteArrayList.this.getArray(), o, this.offset, this.offset + index) - this.offset;
            return (pos >= 0) ? (pos - this.offset) : -1;
        }
        
        public ListIterator listIterator() {
            synchronized (CopyOnWriteArrayList.this) {
                final Object[] array = CopyOnWriteArrayList.this.getArray();
                if (array != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                return new COWSubIterator(array, this.offset, this.offset + this.length, this.offset);
            }
        }
        
        public ListIterator listIterator(final int index) {
            synchronized (CopyOnWriteArrayList.this) {
                if (index < 0 || index >= this.length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.length);
                }
                final Object[] array = CopyOnWriteArrayList.this.getArray();
                if (array != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                return new COWSubIterator(array, this.offset, this.offset + this.length, this.offset + index);
            }
        }
        
        public List subList(final int fromIndex, final int toIndex) {
            if (fromIndex < 0 || toIndex > this.length || fromIndex > toIndex) {
                throw new IndexOutOfBoundsException();
            }
            return new COWSubList(this.offset + fromIndex, toIndex - fromIndex);
        }
        
        public String toString() {
            final Object[] array;
            final int last;
            synchronized (CopyOnWriteArrayList.this) {
                array = CopyOnWriteArrayList.this.getArray();
                if (array != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
                last = this.offset + this.length;
            }
            final StringBuffer buf = new StringBuffer();
            buf.append('[');
            for (int i = this.offset; i < last; ++i) {
                if (i > this.offset) {
                    buf.append(", ");
                }
                buf.append(array[i]);
            }
            buf.append(']');
            return buf.toString();
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            synchronized (CopyOnWriteArrayList.this) {
                if (CopyOnWriteArrayList.this.getArray() != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
            }
            out.defaultWriteObject();
            synchronized (CopyOnWriteArrayList.this) {
                if (CopyOnWriteArrayList.this.getArray() != this.expectedArray) {
                    throw new ConcurrentModificationException();
                }
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            synchronized (CopyOnWriteArrayList.this) {
                this.expectedArray = CopyOnWriteArrayList.this.getArray();
            }
        }
    }
    
    static class COWSubIterator implements ListIterator
    {
        final Object[] array;
        int cursor;
        int first;
        int last;
        
        COWSubIterator(final Object[] array, final int first, final int last, final int cursor) {
            this.array = array;
            this.first = first;
            this.last = last;
            this.cursor = cursor;
        }
        
        public boolean hasNext() {
            return this.cursor < this.last;
        }
        
        public boolean hasPrevious() {
            return this.cursor > this.first;
        }
        
        public int nextIndex() {
            return this.cursor - this.first;
        }
        
        public Object next() {
            if (this.cursor == this.last) {
                throw new NoSuchElementException();
            }
            return this.array[this.cursor++];
        }
        
        public int previousIndex() {
            return this.cursor - this.first - 1;
        }
        
        public Object previous() {
            if (this.cursor == this.first) {
                throw new NoSuchElementException();
            }
            final Object[] array = this.array;
            final int cursor = this.cursor - 1;
            this.cursor = cursor;
            return array[cursor];
        }
        
        public void add(final Object val) {
            throw new UnsupportedOperationException();
        }
        
        public void set(final Object val) {
            throw new UnsupportedOperationException();
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
