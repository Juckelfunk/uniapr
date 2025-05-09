// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm;

class Frame
{
    static final int SAME_FRAME = 0;
    static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
    static final int RESERVED = 128;
    static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
    static final int CHOP_FRAME = 248;
    static final int SAME_FRAME_EXTENDED = 251;
    static final int APPEND_FRAME = 252;
    static final int FULL_FRAME = 255;
    static final int ITEM_TOP = 0;
    static final int ITEM_INTEGER = 1;
    static final int ITEM_FLOAT = 2;
    static final int ITEM_DOUBLE = 3;
    static final int ITEM_LONG = 4;
    static final int ITEM_NULL = 5;
    static final int ITEM_UNINITIALIZED_THIS = 6;
    static final int ITEM_OBJECT = 7;
    static final int ITEM_UNINITIALIZED = 8;
    private static final int ITEM_ASM_BOOLEAN = 9;
    private static final int ITEM_ASM_BYTE = 10;
    private static final int ITEM_ASM_CHAR = 11;
    private static final int ITEM_ASM_SHORT = 12;
    private static final int DIM_MASK = -268435456;
    private static final int KIND_MASK = 251658240;
    private static final int FLAGS_MASK = 15728640;
    private static final int VALUE_MASK = 1048575;
    private static final int DIM_SHIFT = 28;
    private static final int ARRAY_OF = 268435456;
    private static final int ELEMENT_OF = -268435456;
    private static final int CONSTANT_KIND = 16777216;
    private static final int REFERENCE_KIND = 33554432;
    private static final int UNINITIALIZED_KIND = 50331648;
    private static final int LOCAL_KIND = 67108864;
    private static final int STACK_KIND = 83886080;
    private static final int TOP_IF_LONG_OR_DOUBLE_FLAG = 1048576;
    private static final int TOP = 16777216;
    private static final int BOOLEAN = 16777225;
    private static final int BYTE = 16777226;
    private static final int CHAR = 16777227;
    private static final int SHORT = 16777228;
    private static final int INTEGER = 16777217;
    private static final int FLOAT = 16777218;
    private static final int LONG = 16777220;
    private static final int DOUBLE = 16777219;
    private static final int NULL = 16777221;
    private static final int UNINITIALIZED_THIS = 16777222;
    Label owner;
    private int[] inputLocals;
    private int[] inputStack;
    private int[] outputLocals;
    private int[] outputStack;
    private short outputStackStart;
    private short outputStackTop;
    private int initializationCount;
    private int[] initializations;
    
    Frame(final Label owner) {
        this.owner = owner;
    }
    
    final void copyFrom(final Frame frame) {
        this.inputLocals = frame.inputLocals;
        this.inputStack = frame.inputStack;
        this.outputStackStart = 0;
        this.outputLocals = frame.outputLocals;
        this.outputStack = frame.outputStack;
        this.outputStackTop = frame.outputStackTop;
        this.initializationCount = frame.initializationCount;
        this.initializations = frame.initializations;
    }
    
    static int getAbstractTypeFromApiFormat(final SymbolTable symbolTable, final Object type) {
        if (type instanceof Integer) {
            return 0x1000000 | (int)type;
        }
        if (type instanceof String) {
            final String descriptor = Type.getObjectType((String)type).getDescriptor();
            return getAbstractTypeFromDescriptor(symbolTable, descriptor, 0);
        }
        return 0x3000000 | symbolTable.addUninitializedType("", ((Label)type).bytecodeOffset);
    }
    
    static int getAbstractTypeFromInternalName(final SymbolTable symbolTable, final String internalName) {
        return 0x2000000 | symbolTable.addType(internalName);
    }
    
    private static int getAbstractTypeFromDescriptor(final SymbolTable symbolTable, final String buffer, final int offset) {
        switch (buffer.charAt(offset)) {
            case 'V': {
                return 0;
            }
            case 'B':
            case 'C':
            case 'I':
            case 'S':
            case 'Z': {
                return 16777217;
            }
            case 'F': {
                return 16777218;
            }
            case 'J': {
                return 16777220;
            }
            case 'D': {
                return 16777219;
            }
            case 'L': {
                final String internalName = buffer.substring(offset + 1, buffer.length() - 1);
                return 0x2000000 | symbolTable.addType(internalName);
            }
            case '[': {
                int elementDescriptorOffset;
                for (elementDescriptorOffset = offset + 1; buffer.charAt(elementDescriptorOffset) == '['; ++elementDescriptorOffset) {}
                int typeValue = 0;
                switch (buffer.charAt(elementDescriptorOffset)) {
                    case 'Z': {
                        typeValue = 16777225;
                        break;
                    }
                    case 'C': {
                        typeValue = 16777227;
                        break;
                    }
                    case 'B': {
                        typeValue = 16777226;
                        break;
                    }
                    case 'S': {
                        typeValue = 16777228;
                        break;
                    }
                    case 'I': {
                        typeValue = 16777217;
                        break;
                    }
                    case 'F': {
                        typeValue = 16777218;
                        break;
                    }
                    case 'J': {
                        typeValue = 16777220;
                        break;
                    }
                    case 'D': {
                        typeValue = 16777219;
                        break;
                    }
                    case 'L': {
                        final String internalName = buffer.substring(elementDescriptorOffset + 1, buffer.length() - 1);
                        typeValue = (0x2000000 | symbolTable.addType(internalName));
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException();
                    }
                }
                return elementDescriptorOffset - offset << 28 | typeValue;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    final void setInputFrameFromDescriptor(final SymbolTable symbolTable, final int access, final String descriptor, final int maxLocals) {
        this.inputLocals = new int[maxLocals];
        this.inputStack = new int[0];
        int inputLocalIndex = 0;
        if ((access & 0x8) == 0x0) {
            if ((access & 0x40000) == 0x0) {
                this.inputLocals[inputLocalIndex++] = (0x2000000 | symbolTable.addType(symbolTable.getClassName()));
            }
            else {
                this.inputLocals[inputLocalIndex++] = 16777222;
            }
        }
        for (final Type argumentType : Type.getArgumentTypes(descriptor)) {
            final int abstractType = getAbstractTypeFromDescriptor(symbolTable, argumentType.getDescriptor(), 0);
            this.inputLocals[inputLocalIndex++] = abstractType;
            if (abstractType == 16777220 || abstractType == 16777219) {
                this.inputLocals[inputLocalIndex++] = 16777216;
            }
        }
        while (inputLocalIndex < maxLocals) {
            this.inputLocals[inputLocalIndex++] = 16777216;
        }
    }
    
    final void setInputFrameFromApiFormat(final SymbolTable symbolTable, final int numLocal, final Object[] local, final int numStack, final Object[] stack) {
        int inputLocalIndex = 0;
        for (int i = 0; i < numLocal; ++i) {
            this.inputLocals[inputLocalIndex++] = getAbstractTypeFromApiFormat(symbolTable, local[i]);
            if (local[i] == Opcodes.LONG || local[i] == Opcodes.DOUBLE) {
                this.inputLocals[inputLocalIndex++] = 16777216;
            }
        }
        while (inputLocalIndex < this.inputLocals.length) {
            this.inputLocals[inputLocalIndex++] = 16777216;
        }
        int numStackTop = 0;
        for (int j = 0; j < numStack; ++j) {
            if (stack[j] == Opcodes.LONG || stack[j] == Opcodes.DOUBLE) {
                ++numStackTop;
            }
        }
        this.inputStack = new int[numStack + numStackTop];
        int inputStackIndex = 0;
        for (int k = 0; k < numStack; ++k) {
            this.inputStack[inputStackIndex++] = getAbstractTypeFromApiFormat(symbolTable, stack[k]);
            if (stack[k] == Opcodes.LONG || stack[k] == Opcodes.DOUBLE) {
                this.inputStack[inputStackIndex++] = 16777216;
            }
        }
        this.outputStackTop = 0;
        this.initializationCount = 0;
    }
    
    final int getInputStackSize() {
        return this.inputStack.length;
    }
    
    private int getLocal(final int localIndex) {
        if (this.outputLocals == null || localIndex >= this.outputLocals.length) {
            return 0x4000000 | localIndex;
        }
        int abstractType = this.outputLocals[localIndex];
        if (abstractType == 0) {
            final int[] outputLocals = this.outputLocals;
            final int n = 0x4000000 | localIndex;
            outputLocals[localIndex] = n;
            abstractType = n;
        }
        return abstractType;
    }
    
    private void setLocal(final int localIndex, final int abstractType) {
        if (this.outputLocals == null) {
            this.outputLocals = new int[10];
        }
        final int outputLocalsLength = this.outputLocals.length;
        if (localIndex >= outputLocalsLength) {
            final int[] newOutputLocals = new int[Math.max(localIndex + 1, 2 * outputLocalsLength)];
            System.arraycopy(this.outputLocals, 0, newOutputLocals, 0, outputLocalsLength);
            this.outputLocals = newOutputLocals;
        }
        this.outputLocals[localIndex] = abstractType;
    }
    
    private void push(final int abstractType) {
        if (this.outputStack == null) {
            this.outputStack = new int[10];
        }
        final int outputStackLength = this.outputStack.length;
        if (this.outputStackTop >= outputStackLength) {
            final int[] newOutputStack = new int[Math.max(this.outputStackTop + 1, 2 * outputStackLength)];
            System.arraycopy(this.outputStack, 0, newOutputStack, 0, outputStackLength);
            this.outputStack = newOutputStack;
        }
        final int[] outputStack = this.outputStack;
        final short outputStackTop = this.outputStackTop;
        this.outputStackTop = (short)(outputStackTop + 1);
        outputStack[outputStackTop] = abstractType;
        final short outputStackSize = (short)(this.outputStackStart + this.outputStackTop);
        if (outputStackSize > this.owner.outputStackMax) {
            this.owner.outputStackMax = outputStackSize;
        }
    }
    
    private void push(final SymbolTable symbolTable, final String descriptor) {
        final int typeDescriptorOffset = (descriptor.charAt(0) == '(') ? (descriptor.indexOf(41) + 1) : 0;
        final int abstractType = getAbstractTypeFromDescriptor(symbolTable, descriptor, typeDescriptorOffset);
        if (abstractType != 0) {
            this.push(abstractType);
            if (abstractType == 16777220 || abstractType == 16777219) {
                this.push(16777216);
            }
        }
    }
    
    private int pop() {
        if (this.outputStackTop > 0) {
            final int[] outputStack = this.outputStack;
            final short outputStackTop = (short)(this.outputStackTop - 1);
            this.outputStackTop = outputStackTop;
            return outputStack[outputStackTop];
        }
        return 0x5000000 | -(--this.outputStackStart);
    }
    
    private void pop(final int elements) {
        if (this.outputStackTop >= elements) {
            this.outputStackTop -= (short)elements;
        }
        else {
            this.outputStackStart -= (short)(elements - this.outputStackTop);
            this.outputStackTop = 0;
        }
    }
    
    private void pop(final String descriptor) {
        final char firstDescriptorChar = descriptor.charAt(0);
        if (firstDescriptorChar == '(') {
            this.pop((Type.getArgumentsAndReturnSizes(descriptor) >> 2) - 1);
        }
        else if (firstDescriptorChar == 'J' || firstDescriptorChar == 'D') {
            this.pop(2);
        }
        else {
            this.pop(1);
        }
    }
    
    private void addInitializedType(final int abstractType) {
        if (this.initializations == null) {
            this.initializations = new int[2];
        }
        final int initializationsLength = this.initializations.length;
        if (this.initializationCount >= initializationsLength) {
            final int[] newInitializations = new int[Math.max(this.initializationCount + 1, 2 * initializationsLength)];
            System.arraycopy(this.initializations, 0, newInitializations, 0, initializationsLength);
            this.initializations = newInitializations;
        }
        this.initializations[this.initializationCount++] = abstractType;
    }
    
    private int getInitializedType(final SymbolTable symbolTable, final int abstractType) {
        if (abstractType == 16777222 || (abstractType & 0xFF000000) == 0x3000000) {
            int i = 0;
            while (i < this.initializationCount) {
                int initializedType = this.initializations[i];
                final int dim = initializedType & 0xF0000000;
                final int kind = initializedType & 0xF000000;
                final int value = initializedType & 0xFFFFF;
                if (kind == 67108864) {
                    initializedType = dim + this.inputLocals[value];
                }
                else if (kind == 83886080) {
                    initializedType = dim + this.inputStack[this.inputStack.length - value];
                }
                if (abstractType == initializedType) {
                    if (abstractType == 16777222) {
                        return 0x2000000 | symbolTable.addType(symbolTable.getClassName());
                    }
                    return 0x2000000 | symbolTable.addType(symbolTable.getType(abstractType & 0xFFFFF).value);
                }
                else {
                    ++i;
                }
            }
        }
        return abstractType;
    }
    
    void execute(final int opcode, final int arg, final Symbol argSymbol, final SymbolTable symbolTable) {
        Label_2317: {
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
                    this.push(16777221);
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
                case 17:
                case 21: {
                    this.push(16777217);
                    break;
                }
                case 9:
                case 10:
                case 22: {
                    this.push(16777220);
                    this.push(16777216);
                    break;
                }
                case 11:
                case 12:
                case 13:
                case 23: {
                    this.push(16777218);
                    break;
                }
                case 14:
                case 15:
                case 24: {
                    this.push(16777219);
                    this.push(16777216);
                    break;
                }
                case 18: {
                    switch (argSymbol.tag) {
                        case 3: {
                            this.push(16777217);
                            break Label_2317;
                        }
                        case 5: {
                            this.push(16777220);
                            this.push(16777216);
                            break Label_2317;
                        }
                        case 4: {
                            this.push(16777218);
                            break Label_2317;
                        }
                        case 6: {
                            this.push(16777219);
                            this.push(16777216);
                            break Label_2317;
                        }
                        case 7: {
                            this.push(0x2000000 | symbolTable.addType("java/lang/Class"));
                            break Label_2317;
                        }
                        case 8: {
                            this.push(0x2000000 | symbolTable.addType("java/lang/String"));
                            break Label_2317;
                        }
                        case 16: {
                            this.push(0x2000000 | symbolTable.addType("java/lang/invoke/MethodType"));
                            break Label_2317;
                        }
                        case 15: {
                            this.push(0x2000000 | symbolTable.addType("java/lang/invoke/MethodHandle"));
                            break Label_2317;
                        }
                        case 17: {
                            this.push(symbolTable, argSymbol.value);
                            break Label_2317;
                        }
                        default: {
                            throw new AssertionError();
                        }
                    }
                    break;
                }
                case 25: {
                    this.push(this.getLocal(arg));
                    break;
                }
                case 47:
                case 143: {
                    this.pop(2);
                    this.push(16777220);
                    this.push(16777216);
                    break;
                }
                case 49:
                case 138: {
                    this.pop(2);
                    this.push(16777219);
                    this.push(16777216);
                    break;
                }
                case 50: {
                    this.pop(1);
                    final int abstractType1 = this.pop();
                    this.push((abstractType1 == 16777221) ? abstractType1 : (-268435456 + abstractType1));
                    break;
                }
                case 54:
                case 56:
                case 58: {
                    final int abstractType1 = this.pop();
                    this.setLocal(arg, abstractType1);
                    if (arg > 0) {
                        final int previousLocalType = this.getLocal(arg - 1);
                        if (previousLocalType == 16777220 || previousLocalType == 16777219) {
                            this.setLocal(arg - 1, 16777216);
                        }
                        else if ((previousLocalType & 0xF000000) == 0x4000000 || (previousLocalType & 0xF000000) == 0x5000000) {
                            this.setLocal(arg - 1, previousLocalType | 0x100000);
                        }
                        break;
                    }
                    break;
                }
                case 55:
                case 57: {
                    this.pop(1);
                    final int abstractType1 = this.pop();
                    this.setLocal(arg, abstractType1);
                    this.setLocal(arg + 1, 16777216);
                    if (arg > 0) {
                        final int previousLocalType = this.getLocal(arg - 1);
                        if (previousLocalType == 16777220 || previousLocalType == 16777219) {
                            this.setLocal(arg - 1, 16777216);
                        }
                        else if ((previousLocalType & 0xF000000) == 0x4000000 || (previousLocalType & 0xF000000) == 0x5000000) {
                            this.setLocal(arg - 1, previousLocalType | 0x100000);
                        }
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
                    final int abstractType1 = this.pop();
                    this.push(abstractType1);
                    this.push(abstractType1);
                    break;
                }
                case 90: {
                    final int abstractType1 = this.pop();
                    final int abstractType2 = this.pop();
                    this.push(abstractType1);
                    this.push(abstractType2);
                    this.push(abstractType1);
                    break;
                }
                case 91: {
                    final int abstractType1 = this.pop();
                    final int abstractType2 = this.pop();
                    final int abstractType3 = this.pop();
                    this.push(abstractType1);
                    this.push(abstractType3);
                    this.push(abstractType2);
                    this.push(abstractType1);
                    break;
                }
                case 92: {
                    final int abstractType1 = this.pop();
                    final int abstractType2 = this.pop();
                    this.push(abstractType2);
                    this.push(abstractType1);
                    this.push(abstractType2);
                    this.push(abstractType1);
                    break;
                }
                case 93: {
                    final int abstractType1 = this.pop();
                    final int abstractType2 = this.pop();
                    final int abstractType3 = this.pop();
                    this.push(abstractType2);
                    this.push(abstractType1);
                    this.push(abstractType3);
                    this.push(abstractType2);
                    this.push(abstractType1);
                    break;
                }
                case 94: {
                    final int abstractType1 = this.pop();
                    final int abstractType2 = this.pop();
                    final int abstractType3 = this.pop();
                    final int abstractType4 = this.pop();
                    this.push(abstractType2);
                    this.push(abstractType1);
                    this.push(abstractType4);
                    this.push(abstractType3);
                    this.push(abstractType2);
                    this.push(abstractType1);
                    break;
                }
                case 95: {
                    final int abstractType1 = this.pop();
                    final int abstractType2 = this.pop();
                    this.push(abstractType1);
                    this.push(abstractType2);
                    break;
                }
                case 46:
                case 51:
                case 52:
                case 53:
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
                    this.push(16777217);
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
                    this.push(16777220);
                    this.push(16777216);
                    break;
                }
                case 48:
                case 98:
                case 102:
                case 106:
                case 110:
                case 114:
                case 137:
                case 144: {
                    this.pop(2);
                    this.push(16777218);
                    break;
                }
                case 99:
                case 103:
                case 107:
                case 111:
                case 115: {
                    this.pop(4);
                    this.push(16777219);
                    this.push(16777216);
                    break;
                }
                case 121:
                case 123:
                case 125: {
                    this.pop(3);
                    this.push(16777220);
                    this.push(16777216);
                    break;
                }
                case 132: {
                    this.setLocal(arg, 16777217);
                    break;
                }
                case 133:
                case 140: {
                    this.pop(1);
                    this.push(16777220);
                    this.push(16777216);
                    break;
                }
                case 134: {
                    this.pop(1);
                    this.push(16777218);
                    break;
                }
                case 135:
                case 141: {
                    this.pop(1);
                    this.push(16777219);
                    this.push(16777216);
                    break;
                }
                case 139:
                case 190:
                case 193: {
                    this.pop(1);
                    this.push(16777217);
                    break;
                }
                case 148:
                case 151:
                case 152: {
                    this.pop(4);
                    this.push(16777217);
                    break;
                }
                case 168:
                case 169: {
                    throw new IllegalArgumentException("JSR/RET are not supported with computeFrames option");
                }
                case 178: {
                    this.push(symbolTable, argSymbol.value);
                    break;
                }
                case 179: {
                    this.pop(argSymbol.value);
                    break;
                }
                case 180: {
                    this.pop(1);
                    this.push(symbolTable, argSymbol.value);
                    break;
                }
                case 181: {
                    this.pop(argSymbol.value);
                    this.pop();
                    break;
                }
                case 182:
                case 183:
                case 184:
                case 185: {
                    this.pop(argSymbol.value);
                    if (opcode != 184) {
                        final int abstractType1 = this.pop();
                        if (opcode == 183 && argSymbol.name.charAt(0) == '<') {
                            this.addInitializedType(abstractType1);
                        }
                    }
                    this.push(symbolTable, argSymbol.value);
                    break;
                }
                case 186: {
                    this.pop(argSymbol.value);
                    this.push(symbolTable, argSymbol.value);
                    break;
                }
                case 187: {
                    this.push(0x3000000 | symbolTable.addUninitializedType(argSymbol.value, arg));
                    break;
                }
                case 188: {
                    this.pop();
                    switch (arg) {
                        case 4: {
                            this.push(285212681);
                            break Label_2317;
                        }
                        case 5: {
                            this.push(285212683);
                            break Label_2317;
                        }
                        case 8: {
                            this.push(285212682);
                            break Label_2317;
                        }
                        case 9: {
                            this.push(285212684);
                            break Label_2317;
                        }
                        case 10: {
                            this.push(285212673);
                            break Label_2317;
                        }
                        case 6: {
                            this.push(285212674);
                            break Label_2317;
                        }
                        case 7: {
                            this.push(285212675);
                            break Label_2317;
                        }
                        case 11: {
                            this.push(285212676);
                            break Label_2317;
                        }
                        default: {
                            throw new IllegalArgumentException();
                        }
                    }
                    break;
                }
                case 189: {
                    final String arrayElementType = argSymbol.value;
                    this.pop();
                    if (arrayElementType.charAt(0) == '[') {
                        this.push(symbolTable, '[' + arrayElementType);
                        break;
                    }
                    this.push(0x12000000 | symbolTable.addType(arrayElementType));
                    break;
                }
                case 192: {
                    final String castType = argSymbol.value;
                    this.pop();
                    if (castType.charAt(0) == '[') {
                        this.push(symbolTable, castType);
                        break;
                    }
                    this.push(0x2000000 | symbolTable.addType(castType));
                    break;
                }
                case 197: {
                    this.pop(arg);
                    this.push(symbolTable, argSymbol.value);
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
    }
    
    final boolean merge(final SymbolTable symbolTable, final Frame dstFrame, final int catchTypeIndex) {
        boolean frameChanged = false;
        final int numLocal = this.inputLocals.length;
        final int numStack = this.inputStack.length;
        if (dstFrame.inputLocals == null) {
            dstFrame.inputLocals = new int[numLocal];
            frameChanged = true;
        }
        for (int i = 0; i < numLocal; ++i) {
            int concreteOutputType;
            if (this.outputLocals != null && i < this.outputLocals.length) {
                final int abstractOutputType = this.outputLocals[i];
                if (abstractOutputType == 0) {
                    concreteOutputType = this.inputLocals[i];
                }
                else {
                    final int dim = abstractOutputType & 0xF0000000;
                    final int kind = abstractOutputType & 0xF000000;
                    if (kind == 67108864) {
                        concreteOutputType = dim + this.inputLocals[abstractOutputType & 0xFFFFF];
                        if ((abstractOutputType & 0x100000) != 0x0 && (concreteOutputType == 16777220 || concreteOutputType == 16777219)) {
                            concreteOutputType = 16777216;
                        }
                    }
                    else if (kind == 83886080) {
                        concreteOutputType = dim + this.inputStack[numStack - (abstractOutputType & 0xFFFFF)];
                        if ((abstractOutputType & 0x100000) != 0x0 && (concreteOutputType == 16777220 || concreteOutputType == 16777219)) {
                            concreteOutputType = 16777216;
                        }
                    }
                    else {
                        concreteOutputType = abstractOutputType;
                    }
                }
            }
            else {
                concreteOutputType = this.inputLocals[i];
            }
            if (this.initializations != null) {
                concreteOutputType = this.getInitializedType(symbolTable, concreteOutputType);
            }
            frameChanged |= merge(symbolTable, concreteOutputType, dstFrame.inputLocals, i);
        }
        if (catchTypeIndex > 0) {
            for (int i = 0; i < numLocal; ++i) {
                frameChanged |= merge(symbolTable, this.inputLocals[i], dstFrame.inputLocals, i);
            }
            if (dstFrame.inputStack == null) {
                dstFrame.inputStack = new int[1];
                frameChanged = true;
            }
            frameChanged |= merge(symbolTable, catchTypeIndex, dstFrame.inputStack, 0);
            return frameChanged;
        }
        final int numInputStack = this.inputStack.length + this.outputStackStart;
        if (dstFrame.inputStack == null) {
            dstFrame.inputStack = new int[numInputStack + this.outputStackTop];
            frameChanged = true;
        }
        for (int j = 0; j < numInputStack; ++j) {
            int concreteOutputType2 = this.inputStack[j];
            if (this.initializations != null) {
                concreteOutputType2 = this.getInitializedType(symbolTable, concreteOutputType2);
            }
            frameChanged |= merge(symbolTable, concreteOutputType2, dstFrame.inputStack, j);
        }
        for (int j = 0; j < this.outputStackTop; ++j) {
            final int abstractOutputType2 = this.outputStack[j];
            final int dim2 = abstractOutputType2 & 0xF0000000;
            final int kind2 = abstractOutputType2 & 0xF000000;
            int concreteOutputType2;
            if (kind2 == 67108864) {
                concreteOutputType2 = dim2 + this.inputLocals[abstractOutputType2 & 0xFFFFF];
                if ((abstractOutputType2 & 0x100000) != 0x0 && (concreteOutputType2 == 16777220 || concreteOutputType2 == 16777219)) {
                    concreteOutputType2 = 16777216;
                }
            }
            else if (kind2 == 83886080) {
                concreteOutputType2 = dim2 + this.inputStack[numStack - (abstractOutputType2 & 0xFFFFF)];
                if ((abstractOutputType2 & 0x100000) != 0x0 && (concreteOutputType2 == 16777220 || concreteOutputType2 == 16777219)) {
                    concreteOutputType2 = 16777216;
                }
            }
            else {
                concreteOutputType2 = abstractOutputType2;
            }
            if (this.initializations != null) {
                concreteOutputType2 = this.getInitializedType(symbolTable, concreteOutputType2);
            }
            frameChanged |= merge(symbolTable, concreteOutputType2, dstFrame.inputStack, numInputStack + j);
        }
        return frameChanged;
    }
    
    private static boolean merge(final SymbolTable symbolTable, final int sourceType, final int[] dstTypes, final int dstIndex) {
        final int dstType = dstTypes[dstIndex];
        if (dstType == sourceType) {
            return false;
        }
        int srcType = sourceType;
        if ((sourceType & 0xFFFFFFF) == 0x1000005) {
            if (dstType == 16777221) {
                return false;
            }
            srcType = 16777221;
        }
        if (dstType == 0) {
            dstTypes[dstIndex] = srcType;
            return true;
        }
        int mergedType;
        if ((dstType & 0xF0000000) != 0x0 || (dstType & 0xF000000) == 0x2000000) {
            if (srcType == 16777221) {
                return false;
            }
            if ((srcType & 0xFF000000) == (dstType & 0xFF000000)) {
                if ((dstType & 0xF000000) == 0x2000000) {
                    mergedType = ((srcType & 0xF0000000) | 0x2000000 | symbolTable.addMergedType(srcType & 0xFFFFF, dstType & 0xFFFFF));
                }
                else {
                    final int mergedDim = -268435456 + (srcType & 0xF0000000);
                    mergedType = (mergedDim | 0x2000000 | symbolTable.addType("java/lang/Object"));
                }
            }
            else if ((srcType & 0xF0000000) != 0x0 || (srcType & 0xF000000) == 0x2000000) {
                int srcDim = srcType & 0xF0000000;
                if (srcDim != 0 && (srcType & 0xF000000) != 0x2000000) {
                    srcDim -= 268435456;
                }
                int dstDim = dstType & 0xF0000000;
                if (dstDim != 0 && (dstType & 0xF000000) != 0x2000000) {
                    dstDim -= 268435456;
                }
                mergedType = (Math.min(srcDim, dstDim) | 0x2000000 | symbolTable.addType("java/lang/Object"));
            }
            else {
                mergedType = 16777216;
            }
        }
        else if (dstType == 16777221) {
            mergedType = (((srcType & 0xF0000000) != 0x0 || (srcType & 0xF000000) == 0x2000000) ? srcType : 16777216);
        }
        else {
            mergedType = 16777216;
        }
        if (mergedType != dstType) {
            dstTypes[dstIndex] = mergedType;
            return true;
        }
        return false;
    }
    
    final void accept(final MethodWriter methodWriter) {
        final int[] localTypes = this.inputLocals;
        int numLocal = 0;
        int numTrailingTop = 0;
        int i = 0;
        while (i < localTypes.length) {
            final int localType = localTypes[i];
            i += ((localType == 16777220 || localType == 16777219) ? 2 : 1);
            if (localType == 16777216) {
                ++numTrailingTop;
            }
            else {
                numLocal += numTrailingTop + 1;
                numTrailingTop = 0;
            }
        }
        int[] stackTypes;
        int numStack;
        int stackType;
        for (stackTypes = this.inputStack, numStack = 0, i = 0; i < stackTypes.length; i += ((stackType == 16777220 || stackType == 16777219) ? 2 : 1), ++numStack) {
            stackType = stackTypes[i];
        }
        int frameIndex = methodWriter.visitFrameStart(this.owner.bytecodeOffset, numLocal, numStack);
        i = 0;
        while (numLocal-- > 0) {
            final int localType2 = localTypes[i];
            i += ((localType2 == 16777220 || localType2 == 16777219) ? 2 : 1);
            methodWriter.visitAbstractType(frameIndex++, localType2);
        }
        i = 0;
        while (numStack-- > 0) {
            final int stackType2 = stackTypes[i];
            i += ((stackType2 == 16777220 || stackType2 == 16777219) ? 2 : 1);
            methodWriter.visitAbstractType(frameIndex++, stackType2);
        }
        methodWriter.visitFrameEnd();
    }
    
    static void putAbstractType(final SymbolTable symbolTable, final int abstractType, final ByteVector output) {
        int arrayDimensions = (abstractType & 0xF0000000) >> 28;
        if (arrayDimensions == 0) {
            final int typeValue = abstractType & 0xFFFFF;
            switch (abstractType & 0xF000000) {
                case 16777216: {
                    output.putByte(typeValue);
                    break;
                }
                case 33554432: {
                    output.putByte(7).putShort(symbolTable.addConstantClass(symbolTable.getType(typeValue).value).index);
                    break;
                }
                case 50331648: {
                    output.putByte(8).putShort((int)symbolTable.getType(typeValue).data);
                    break;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        else {
            final StringBuilder typeDescriptor = new StringBuilder();
            while (arrayDimensions-- > 0) {
                typeDescriptor.append('[');
            }
            if ((abstractType & 0xF000000) == 0x2000000) {
                typeDescriptor.append('L').append(symbolTable.getType(abstractType & 0xFFFFF).value).append(';');
            }
            else {
                switch (abstractType & 0xFFFFF) {
                    case 9: {
                        typeDescriptor.append('Z');
                        break;
                    }
                    case 10: {
                        typeDescriptor.append('B');
                        break;
                    }
                    case 11: {
                        typeDescriptor.append('C');
                        break;
                    }
                    case 12: {
                        typeDescriptor.append('S');
                        break;
                    }
                    case 1: {
                        typeDescriptor.append('I');
                        break;
                    }
                    case 2: {
                        typeDescriptor.append('F');
                        break;
                    }
                    case 4: {
                        typeDescriptor.append('J');
                        break;
                    }
                    case 3: {
                        typeDescriptor.append('D');
                        break;
                    }
                    default: {
                        throw new AssertionError();
                    }
                }
            }
            output.putByte(7).putShort(symbolTable.addConstantClass(typeDescriptor.toString()).index);
        }
    }
}
