// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;
import java.util.Map;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible
public abstract class ImmutableTable<R, C, V> extends AbstractTable<R, C, V> implements Serializable
{
    public static <R, C, V> ImmutableTable<R, C, V> of() {
        return (ImmutableTable<R, C, V>)SparseImmutableTable.EMPTY;
    }
    
    public static <R, C, V> ImmutableTable<R, C, V> of(final R rowKey, final C columnKey, final V value) {
        return new SingletonImmutableTable<R, C, V>(rowKey, columnKey, value);
    }
    
    public static <R, C, V> ImmutableTable<R, C, V> copyOf(final Table<? extends R, ? extends C, ? extends V> table) {
        if (table instanceof ImmutableTable) {
            final ImmutableTable<R, C, V> parameterizedTable = (ImmutableTable<R, C, V>)(ImmutableTable)table;
            return parameterizedTable;
        }
        final int size = table.size();
        switch (size) {
            case 0: {
                return of();
            }
            case 1: {
                final Table.Cell<? extends R, ? extends C, ? extends V> onlyCell = Iterables.getOnlyElement(table.cellSet());
                return of((R)onlyCell.getRowKey(), (C)onlyCell.getColumnKey(), (V)onlyCell.getValue());
            }
            default: {
                final ImmutableSet.Builder<Table.Cell<R, C, V>> cellSetBuilder = new ImmutableSet.Builder<Table.Cell<R, C, V>>(size);
                for (final Table.Cell<? extends R, ? extends C, ? extends V> cell : table.cellSet()) {
                    cellSetBuilder.add(cellOf((R)cell.getRowKey(), (C)cell.getColumnKey(), (V)cell.getValue()));
                }
                return RegularImmutableTable.forCells(cellSetBuilder.build());
            }
        }
    }
    
    public static <R, C, V> Builder<R, C, V> builder() {
        return new Builder<R, C, V>();
    }
    
    static <R, C, V> Table.Cell<R, C, V> cellOf(final R rowKey, final C columnKey, final V value) {
        return Tables.immutableCell((R)Preconditions.checkNotNull((R)rowKey), (C)Preconditions.checkNotNull((C)columnKey), (V)Preconditions.checkNotNull((V)value));
    }
    
    ImmutableTable() {
    }
    
    @Override
    public ImmutableSet<Table.Cell<R, C, V>> cellSet() {
        return (ImmutableSet<Table.Cell<R, C, V>>)(ImmutableSet)super.cellSet();
    }
    
    @Override
    abstract ImmutableSet<Table.Cell<R, C, V>> createCellSet();
    
    @Override
    final UnmodifiableIterator<Table.Cell<R, C, V>> cellIterator() {
        throw new AssertionError((Object)"should never be called");
    }
    
    @Override
    public ImmutableCollection<V> values() {
        return (ImmutableCollection<V>)(ImmutableCollection)super.values();
    }
    
    @Override
    abstract ImmutableCollection<V> createValues();
    
    @Override
    final Iterator<V> valuesIterator() {
        throw new AssertionError((Object)"should never be called");
    }
    
    @Override
    public ImmutableMap<R, V> column(final C columnKey) {
        Preconditions.checkNotNull(columnKey);
        return MoreObjects.firstNonNull(this.columnMap().get(columnKey), ImmutableMap.of());
    }
    
    @Override
    public ImmutableSet<C> columnKeySet() {
        return this.columnMap().keySet();
    }
    
    @Override
    public abstract ImmutableMap<C, Map<R, V>> columnMap();
    
    @Override
    public ImmutableMap<C, V> row(final R rowKey) {
        Preconditions.checkNotNull(rowKey);
        return MoreObjects.firstNonNull(this.rowMap().get(rowKey), ImmutableMap.of());
    }
    
    @Override
    public ImmutableSet<R> rowKeySet() {
        return this.rowMap().keySet();
    }
    
    @Override
    public abstract ImmutableMap<R, Map<C, V>> rowMap();
    
    @Override
    public boolean contains(@Nullable final Object rowKey, @Nullable final Object columnKey) {
        return this.get(rowKey, columnKey) != null;
    }
    
    @Override
    public boolean containsValue(@Nullable final Object value) {
        return this.values().contains(value);
    }
    
    @Deprecated
    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final V put(final R rowKey, final C columnKey, final V value) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final void putAll(final Table<? extends R, ? extends C, ? extends V> table) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final V remove(final Object rowKey, final Object columnKey) {
        throw new UnsupportedOperationException();
    }
    
    abstract SerializedForm createSerializedForm();
    
    final Object writeReplace() {
        return this.createSerializedForm();
    }
    
    public static final class Builder<R, C, V>
    {
        private final List<Table.Cell<R, C, V>> cells;
        private Comparator<? super R> rowComparator;
        private Comparator<? super C> columnComparator;
        
        public Builder() {
            this.cells = (List<Table.Cell<R, C, V>>)Lists.newArrayList();
        }
        
        @CanIgnoreReturnValue
        public Builder<R, C, V> orderRowsBy(final Comparator<? super R> rowComparator) {
            this.rowComparator = Preconditions.checkNotNull(rowComparator);
            return this;
        }
        
        @CanIgnoreReturnValue
        public Builder<R, C, V> orderColumnsBy(final Comparator<? super C> columnComparator) {
            this.columnComparator = Preconditions.checkNotNull(columnComparator);
            return this;
        }
        
        @CanIgnoreReturnValue
        public Builder<R, C, V> put(final R rowKey, final C columnKey, final V value) {
            this.cells.add(ImmutableTable.cellOf(rowKey, columnKey, value));
            return this;
        }
        
        @CanIgnoreReturnValue
        public Builder<R, C, V> put(final Table.Cell<? extends R, ? extends C, ? extends V> cell) {
            if (cell instanceof Tables.ImmutableCell) {
                Preconditions.checkNotNull(cell.getRowKey());
                Preconditions.checkNotNull(cell.getColumnKey());
                Preconditions.checkNotNull(cell.getValue());
                final Table.Cell<R, C, V> immutableCell = (Table.Cell<R, C, V>)cell;
                this.cells.add(immutableCell);
            }
            else {
                this.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
            }
            return this;
        }
        
        @CanIgnoreReturnValue
        public Builder<R, C, V> putAll(final Table<? extends R, ? extends C, ? extends V> table) {
            for (final Table.Cell<? extends R, ? extends C, ? extends V> cell : table.cellSet()) {
                this.put(cell);
            }
            return this;
        }
        
        public ImmutableTable<R, C, V> build() {
            final int size = this.cells.size();
            switch (size) {
                case 0: {
                    return ImmutableTable.of();
                }
                case 1: {
                    return new SingletonImmutableTable<R, C, V>(Iterables.getOnlyElement(this.cells));
                }
                default: {
                    return RegularImmutableTable.forCells(this.cells, this.rowComparator, this.columnComparator);
                }
            }
        }
    }
    
    static final class SerializedForm implements Serializable
    {
        private final Object[] rowKeys;
        private final Object[] columnKeys;
        private final Object[] cellValues;
        private final int[] cellRowIndices;
        private final int[] cellColumnIndices;
        private static final long serialVersionUID = 0L;
        
        private SerializedForm(final Object[] rowKeys, final Object[] columnKeys, final Object[] cellValues, final int[] cellRowIndices, final int[] cellColumnIndices) {
            this.rowKeys = rowKeys;
            this.columnKeys = columnKeys;
            this.cellValues = cellValues;
            this.cellRowIndices = cellRowIndices;
            this.cellColumnIndices = cellColumnIndices;
        }
        
        static SerializedForm create(final ImmutableTable<?, ?, ?> table, final int[] cellRowIndices, final int[] cellColumnIndices) {
            return new SerializedForm(table.rowKeySet().toArray(), table.columnKeySet().toArray(), table.values().toArray(), cellRowIndices, cellColumnIndices);
        }
        
        Object readResolve() {
            if (this.cellValues.length == 0) {
                return ImmutableTable.of();
            }
            if (this.cellValues.length == 1) {
                return ImmutableTable.of(this.rowKeys[0], this.columnKeys[0], this.cellValues[0]);
            }
            final ImmutableList.Builder<Table.Cell<Object, Object, Object>> cellListBuilder = new ImmutableList.Builder<Table.Cell<Object, Object, Object>>(this.cellValues.length);
            for (int i = 0; i < this.cellValues.length; ++i) {
                cellListBuilder.add(ImmutableTable.cellOf(this.rowKeys[this.cellRowIndices[i]], this.columnKeys[this.cellColumnIndices[i]], this.cellValues[i]));
            }
            return RegularImmutableTable.forOrderedComponents(cellListBuilder.build(), (ImmutableSet<Object>)ImmutableSet.copyOf(this.rowKeys), (ImmutableSet<Object>)ImmutableSet.copyOf(this.columnKeys));
        }
    }
}
