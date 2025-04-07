// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import org.pitest.reloc.asm.Handle;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.Opcodes;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import org.pitest.reloc.asm.Label;
import java.util.List;
import org.pitest.reloc.asm.MethodVisitor;

public class AnalyzerAdapter extends MethodVisitor
{
    public List<Object> locals;
    public List<Object> stack;
    private List<Label> labels;
    public Map<Object, Object> uninitializedTypes;
    private int maxStack;
    private int maxLocals;
    private String owner;
    
    public AnalyzerAdapter(final String owner, final int access, final String name, final String desc, final MethodVisitor mv) {
        this(393216, owner, access, name, desc, mv);
        if (this.getClass() != AnalyzerAdapter.class) {
            throw new IllegalStateException();
        }
    }
    
    protected AnalyzerAdapter(final int api, final String owner, final int access, final String name, final String desc, final MethodVisitor mv) {
        super(api, mv);
        this.owner = owner;
        this.locals = new ArrayList<Object>();
        this.stack = new ArrayList<Object>();
        this.uninitializedTypes = new HashMap<Object, Object>();
        if ((access & 0x8) == 0x0) {
            if ("<init>".equals(name)) {
                this.locals.add(Opcodes.UNINITIALIZED_THIS);
            }
            else {
                this.locals.add(owner);
            }
        }
        final Type[] types = Type.getArgumentTypes(desc);
        for (int i = 0; i < types.length; ++i) {
            final Type type = types[i];
            switch (type.getSort()) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5: {
                    this.locals.add(Opcodes.INTEGER);
                    break;
                }
                case 6: {
                    this.locals.add(Opcodes.FLOAT);
                    break;
                }
                case 7: {
                    this.locals.add(Opcodes.LONG);
                    this.locals.add(Opcodes.TOP);
                    break;
                }
                case 8: {
                    this.locals.add(Opcodes.DOUBLE);
                    this.locals.add(Opcodes.TOP);
                    break;
                }
                case 9: {
                    this.locals.add(types[i].getDescriptor());
                    break;
                }
                default: {
                    this.locals.add(types[i].getInternalName());
                    break;
                }
            }
        }
        this.maxLocals = this.locals.size();
    }
    
    @Override
    public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack) {
        if (type != -1) {
            throw new IllegalStateException("ClassReader.accept() should be called with EXPAND_FRAMES flag");
        }
        if (this.mv != null) {
            this.mv.visitFrame(type, nLocal, local, nStack, stack);
        }
        if (this.locals != null) {
            this.locals.clear();
            this.stack.clear();
        }
        else {
            this.locals = new ArrayList<Object>();
            this.stack = new ArrayList<Object>();
        }
        visitFrameTypes(nLocal, local, this.locals);
        visitFrameTypes(nStack, stack, this.stack);
        this.maxStack = Math.max(this.maxStack, this.stack.size());
    }
    
    private static void visitFrameTypes(final int n, final Object[] types, final List<Object> result) {
        for (final Object type : types) {
            result.add(type);
            if (type == Opcodes.LONG || type == Opcodes.DOUBLE) {
                result.add(Opcodes.TOP);
            }
        }
    }
    
    @Override
    public void visitInsn(final int opcode) {
        if (this.mv != null) {
            this.mv.visitInsn(opcode);
        }
        this.execute(opcode, 0, null);
        if ((opcode >= 172 && opcode <= 177) || opcode == 191) {
            this.locals = null;
            this.stack = null;
        }
    }
    
    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        if (this.mv != null) {
            this.mv.visitIntInsn(opcode, operand);
        }
        this.execute(opcode, operand, null);
    }
    
    @Override
    public void visitVarInsn(final int opcode, final int var) {
        if (this.mv != null) {
            this.mv.visitVarInsn(opcode, var);
        }
        this.execute(opcode, var, null);
    }
    
    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        if (opcode == 187) {
            if (this.labels == null) {
                final Label l = new Label();
                (this.labels = new ArrayList<Label>(3)).add(l);
                if (this.mv != null) {
                    this.mv.visitLabel(l);
                }
            }
            for (int i = 0; i < this.labels.size(); ++i) {
                this.uninitializedTypes.put(this.labels.get(i), type);
            }
        }
        if (this.mv != null) {
            this.mv.visitTypeInsn(opcode, type);
        }
        this.execute(opcode, 0, type);
    }
    
    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        if (this.mv != null) {
            this.mv.visitFieldInsn(opcode, owner, name, desc);
        }
        this.execute(opcode, 0, desc);
    }
    
    @Deprecated
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        if (this.api >= 327680) {
            super.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        this.doVisitMethodInsn(opcode, owner, name, desc, opcode == 185);
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (this.api < 327680) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        this.doVisitMethodInsn(opcode, owner, name, desc, itf);
    }
    
    private void doVisitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (this.mv != null) {
            this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
        if (this.locals == null) {
            this.labels = null;
            return;
        }
        this.pop(desc);
        if (opcode != 184) {
            final Object t = this.pop();
            if (opcode == 183 && name.charAt(0) == '<') {
                Object u;
                if (t == Opcodes.UNINITIALIZED_THIS) {
                    u = this.owner;
                }
                else {
                    u = this.uninitializedTypes.get(t);
                }
                for (int i = 0; i < this.locals.size(); ++i) {
                    if (this.locals.get(i) == t) {
                        this.locals.set(i, u);
                    }
                }
                for (int i = 0; i < this.stack.size(); ++i) {
                    if (this.stack.get(i) == t) {
                        this.stack.set(i, u);
                    }
                }
            }
        }
        this.pushDesc(desc);
        this.labels = null;
    }
    
    @Override
    public void visitInvokeDynamicInsn(final String name, final String desc, final Handle bsm, final Object... bsmArgs) {
        if (this.mv != null) {
            this.mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        }
        if (this.locals == null) {
            this.labels = null;
            return;
        }
        this.pop(desc);
        this.pushDesc(desc);
        this.labels = null;
    }
    
    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        if (this.mv != null) {
            this.mv.visitJumpInsn(opcode, label);
        }
        this.execute(opcode, 0, null);
        if (opcode == 167) {
            this.locals = null;
            this.stack = null;
        }
    }
    
    @Override
    public void visitLabel(final Label label) {
        if (this.mv != null) {
            this.mv.visitLabel(label);
        }
        if (this.labels == null) {
            this.labels = new ArrayList<Label>(3);
        }
        this.labels.add(label);
    }
    
    @Override
    public void visitLdcInsn(final Object cst) {
        if (this.mv != null) {
            this.mv.visitLdcInsn(cst);
        }
        if (this.locals == null) {
            this.labels = null;
            return;
        }
        if (cst instanceof Integer) {
            this.push(Opcodes.INTEGER);
        }
        else if (cst instanceof Long) {
            this.push(Opcodes.LONG);
            this.push(Opcodes.TOP);
        }
        else if (cst instanceof Float) {
            this.push(Opcodes.FLOAT);
        }
        else if (cst instanceof Double) {
            this.push(Opcodes.DOUBLE);
            this.push(Opcodes.TOP);
        }
        else if (cst instanceof String) {
            this.push("java/lang/String");
        }
        else if (cst instanceof Type) {
            final int sort = ((Type)cst).getSort();
            if (sort == 10 || sort == 9) {
                this.push("java/lang/Class");
            }
            else {
                if (sort != 11) {
                    throw new IllegalArgumentException();
                }
                this.push("java/lang/invoke/MethodType");
            }
        }
        else {
            if (!(cst instanceof Handle)) {
                throw new IllegalArgumentException();
            }
            this.push("java/lang/invoke/MethodHandle");
        }
        this.labels = null;
    }
    
    @Override
    public void visitIincInsn(final int var, final int increment) {
        if (this.mv != null) {
            this.mv.visitIincInsn(var, increment);
        }
        this.execute(132, var, null);
    }
    
    @Override
    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels) {
        if (this.mv != null) {
            this.mv.visitTableSwitchInsn(min, max, dflt, labels);
        }
        this.execute(170, 0, null);
        this.locals = null;
        this.stack = null;
    }
    
    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        if (this.mv != null) {
            this.mv.visitLookupSwitchInsn(dflt, keys, labels);
        }
        this.execute(171, 0, null);
        this.locals = null;
        this.stack = null;
    }
    
    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        if (this.mv != null) {
            this.mv.visitMultiANewArrayInsn(desc, dims);
        }
        this.execute(197, dims, desc);
    }
    
    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        if (this.mv != null) {
            this.maxStack = Math.max(this.maxStack, maxStack);
            this.maxLocals = Math.max(this.maxLocals, maxLocals);
            this.mv.visitMaxs(this.maxStack, this.maxLocals);
        }
    }
    
    private Object get(final int local) {
        this.maxLocals = Math.max(this.maxLocals, local + 1);
        return (local < this.locals.size()) ? this.locals.get(local) : Opcodes.TOP;
    }
    
    private void set(final int local, final Object type) {
        this.maxLocals = Math.max(this.maxLocals, local + 1);
        while (local >= this.locals.size()) {
            this.locals.add(Opcodes.TOP);
        }
        this.locals.set(local, type);
    }
    
    private void push(final Object type) {
        this.stack.add(type);
        this.maxStack = Math.max(this.maxStack, this.stack.size());
    }
    
    private void pushDesc(final String desc) {
        final int index = (desc.charAt(0) == '(') ? (desc.indexOf(41) + 1) : 0;
        switch (desc.charAt(index)) {
            case 'V': {}
            case 'B':
            case 'C':
            case 'I':
            case 'S':
            case 'Z': {
                this.push(Opcodes.INTEGER);
            }
            case 'F': {
                this.push(Opcodes.FLOAT);
            }
            case 'J': {
                this.push(Opcodes.LONG);
                this.push(Opcodes.TOP);
            }
            case 'D': {
                this.push(Opcodes.DOUBLE);
                this.push(Opcodes.TOP);
            }
            case '[': {
                if (index == 0) {
                    this.push(desc);
                    break;
                }
                this.push(desc.substring(index, desc.length()));
                break;
            }
            default: {
                if (index == 0) {
                    this.push(desc.substring(1, desc.length() - 1));
                    break;
                }
                this.push(desc.substring(index + 1, desc.length() - 1));
                break;
            }
        }
    }
    
    private Object pop() {
        return this.stack.remove(this.stack.size() - 1);
    }
    
    private void pop(final int n) {
        final int size = this.stack.size();
        for (int end = size - n, i = size - 1; i >= end; --i) {
            this.stack.remove(i);
        }
    }
    
    private void pop(final String desc) {
        final char c = desc.charAt(0);
        if (c == '(') {
            int n = 0;
            final Type[] types = Type.getArgumentTypes(desc);
            for (int i = 0; i < types.length; ++i) {
                n += types[i].getSize();
            }
            this.pop(n);
        }
        else if (c == 'J' || c == 'D') {
            this.pop(2);
        }
        else {
            this.pop(1);
        }
    }
    
    private void execute(final int opcode, final int iarg, final String sarg) {
        if (this.locals == null) {
            this.labels = null;
            return;
        }
        Label_1956: {
            switch (opcode) {
                case 0:
                case 116:
                case 117:
                case 118:
                case 119:
                case 145:
                case 146:
                case 147:
                case 167:
                case 177: {
                    break;
                }
                case 1: {
                    this.push(Opcodes.NULL);
                    break;
                }
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 16:
                case 17: {
                    this.push(Opcodes.INTEGER);
                    break;
                }
                case 9:
                case 10: {
                    this.push(Opcodes.LONG);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 11:
                case 12:
                case 13: {
                    this.push(Opcodes.FLOAT);
                    break;
                }
                case 14:
                case 15: {
                    this.push(Opcodes.DOUBLE);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 21:
                case 23:
                case 25: {
                    this.push(this.get(iarg));
                    break;
                }
                case 22:
                case 24: {
                    this.push(this.get(iarg));
                    this.push(Opcodes.TOP);
                    break;
                }
                case 46:
                case 51:
                case 52:
                case 53: {
                    this.pop(2);
                    this.push(Opcodes.INTEGER);
                    break;
                }
                case 47:
                case 143: {
                    this.pop(2);
                    this.push(Opcodes.LONG);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 48: {
                    this.pop(2);
                    this.push(Opcodes.FLOAT);
                    break;
                }
                case 49:
                case 138: {
                    this.pop(2);
                    this.push(Opcodes.DOUBLE);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 50: {
                    this.pop(1);
                    final Object t1 = this.pop();
                    if (t1 instanceof String) {
                        this.pushDesc(((String)t1).substring(1));
                        break;
                    }
                    if (t1 == Opcodes.NULL) {
                        this.push(t1);
                        break;
                    }
                    this.push("java/lang/Object");
                    break;
                }
                case 54:
                case 56:
                case 58: {
                    final Object t1 = this.pop();
                    this.set(iarg, t1);
                    if (iarg <= 0) {
                        break;
                    }
                    final Object t2 = this.get(iarg - 1);
                    if (t2 == Opcodes.LONG || t2 == Opcodes.DOUBLE) {
                        this.set(iarg - 1, Opcodes.TOP);
                        break;
                    }
                    break;
                }
                case 55:
                case 57: {
                    this.pop(1);
                    final Object t1 = this.pop();
                    this.set(iarg, t1);
                    this.set(iarg + 1, Opcodes.TOP);
                    if (iarg <= 0) {
                        break;
                    }
                    final Object t2 = this.get(iarg - 1);
                    if (t2 == Opcodes.LONG || t2 == Opcodes.DOUBLE) {
                        this.set(iarg - 1, Opcodes.TOP);
                        break;
                    }
                    break;
                }
                case 79:
                case 81:
                case 83:
                case 84:
                case 85:
                case 86: {
                    this.pop(3);
                    break;
                }
                case 80:
                case 82: {
                    this.pop(4);
                    break;
                }
                case 87:
                case 153:
                case 154:
                case 155:
                case 156:
                case 157:
                case 158:
                case 170:
                case 171:
                case 172:
                case 174:
                case 176:
                case 191:
                case 194:
                case 195:
                case 198:
                case 199: {
                    this.pop(1);
                    break;
                }
                case 88:
                case 159:
                case 160:
                case 161:
                case 162:
                case 163:
                case 164:
                case 165:
                case 166:
                case 173:
                case 175: {
                    this.pop(2);
                    break;
                }
                case 89: {
                    final Object t1 = this.pop();
                    this.push(t1);
                    this.push(t1);
                    break;
                }
                case 90: {
                    final Object t1 = this.pop();
                    final Object t2 = this.pop();
                    this.push(t1);
                    this.push(t2);
                    this.push(t1);
                    break;
                }
                case 91: {
                    final Object t1 = this.pop();
                    final Object t2 = this.pop();
                    final Object t3 = this.pop();
                    this.push(t1);
                    this.push(t3);
                    this.push(t2);
                    this.push(t1);
                    break;
                }
                case 92: {
                    final Object t1 = this.pop();
                    final Object t2 = this.pop();
                    this.push(t2);
                    this.push(t1);
                    this.push(t2);
                    this.push(t1);
                    break;
                }
                case 93: {
                    final Object t1 = this.pop();
                    final Object t2 = this.pop();
                    final Object t3 = this.pop();
                    this.push(t2);
                    this.push(t1);
                    this.push(t3);
                    this.push(t2);
                    this.push(t1);
                    break;
                }
                case 94: {
                    final Object t1 = this.pop();
                    final Object t2 = this.pop();
                    final Object t3 = this.pop();
                    final Object t4 = this.pop();
                    this.push(t2);
                    this.push(t1);
                    this.push(t4);
                    this.push(t3);
                    this.push(t2);
                    this.push(t1);
                    break;
                }
                case 95: {
                    final Object t1 = this.pop();
                    final Object t2 = this.pop();
                    this.push(t1);
                    this.push(t2);
                    break;
                }
                case 96:
                case 100:
                case 104:
                case 108:
                case 112:
                case 120:
                case 122:
                case 124:
                case 126:
                case 128:
                case 130:
                case 136:
                case 142:
                case 149:
                case 150: {
                    this.pop(2);
                    this.push(Opcodes.INTEGER);
                    break;
                }
                case 97:
                case 101:
                case 105:
                case 109:
                case 113:
                case 127:
                case 129:
                case 131: {
                    this.pop(4);
                    this.push(Opcodes.LONG);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 98:
                case 102:
                case 106:
                case 110:
                case 114:
                case 137:
                case 144: {
                    this.pop(2);
                    this.push(Opcodes.FLOAT);
                    break;
                }
                case 99:
                case 103:
                case 107:
                case 111:
                case 115: {
                    this.pop(4);
                    this.push(Opcodes.DOUBLE);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 121:
                case 123:
                case 125: {
                    this.pop(3);
                    this.push(Opcodes.LONG);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 132: {
                    this.set(iarg, Opcodes.INTEGER);
                    break;
                }
                case 133:
                case 140: {
                    this.pop(1);
                    this.push(Opcodes.LONG);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 134: {
                    this.pop(1);
                    this.push(Opcodes.FLOAT);
                    break;
                }
                case 135:
                case 141: {
                    this.pop(1);
                    this.push(Opcodes.DOUBLE);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 139:
                case 190:
                case 193: {
                    this.pop(1);
                    this.push(Opcodes.INTEGER);
                    break;
                }
                case 148:
                case 151:
                case 152: {
                    this.pop(4);
                    this.push(Opcodes.INTEGER);
                    break;
                }
                case 168:
                case 169: {
                    throw new RuntimeException("JSR/RET are not supported");
                }
                case 178: {
                    this.pushDesc(sarg);
                    break;
                }
                case 179: {
                    this.pop(sarg);
                    break;
                }
                case 180: {
                    this.pop(1);
                    this.pushDesc(sarg);
                    break;
                }
                case 181: {
                    this.pop(sarg);
                    this.pop();
                    break;
                }
                case 187: {
                    this.push(this.labels.get(0));
                    break;
                }
                case 188: {
                    this.pop();
                    switch (iarg) {
                        case 4: {
                            this.pushDesc("[Z");
                            break Label_1956;
                        }
                        case 5: {
                            this.pushDesc("[C");
                            break Label_1956;
                        }
                        case 8: {
                            this.pushDesc("[B");
                            break Label_1956;
                        }
                        case 9: {
                            this.pushDesc("[S");
                            break Label_1956;
                        }
                        case 10: {
                            this.pushDesc("[I");
                            break Label_1956;
                        }
                        case 6: {
                            this.pushDesc("[F");
                            break Label_1956;
                        }
                        case 7: {
                            this.pushDesc("[D");
                            break Label_1956;
                        }
                        default: {
                            this.pushDesc("[J");
                            break Label_1956;
                        }
                    }
                    break;
                }
                case 189: {
                    this.pop();
                    this.pushDesc("[" + Type.getObjectType(sarg));
                    break;
                }
                case 192: {
                    this.pop();
                    this.pushDesc(Type.getObjectType(sarg).getDescriptor());
                    break;
                }
                default: {
                    this.pop(iarg);
                    this.pushDesc(sarg);
                    break;
                }
            }
        }
        this.labels = null;
    }
}
