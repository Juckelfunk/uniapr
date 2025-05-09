// 
// Decompiled by Procyon v0.5.36
// 

package edu.utdallas.prf.reloc.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import edu.utdallas.prf.reloc.apache.commons.lang3.ArrayUtils;

@Deprecated
public class AggregateTranslator extends CharSequenceTranslator
{
    private final CharSequenceTranslator[] translators;
    
    public AggregateTranslator(final CharSequenceTranslator... translators) {
        this.translators = ArrayUtils.clone(translators);
    }
    
    @Override
    public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
        for (final CharSequenceTranslator translator : this.translators) {
            final int consumed = translator.translate(input, index, out);
            if (consumed != 0) {
                return consumed;
            }
        }
        return 0;
    }
}
