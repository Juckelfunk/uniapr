// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import java.util.List;
import java.util.ArrayList;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.MXParser;
import java.io.InputStream;
import java.io.IOException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.Reader;

public class Xpp3DomBuilder
{
    private static final boolean DEFAULT_TRIM = true;
    
    public static Xpp3Dom build(final Reader reader) throws XmlPullParserException, IOException {
        return build(reader, true);
    }
    
    public static Xpp3Dom build(final InputStream is, final String encoding) throws XmlPullParserException, IOException {
        return build(is, encoding, true);
    }
    
    public static Xpp3Dom build(final InputStream is, final String encoding, final boolean trim) throws XmlPullParserException, IOException {
        final XmlPullParser parser = new MXParser();
        parser.setInput(is, encoding);
        try {
            return build(parser, trim);
        }
        finally {
            IOUtil.close(is);
        }
    }
    
    public static Xpp3Dom build(final Reader reader, final boolean trim) throws XmlPullParserException, IOException {
        final XmlPullParser parser = new MXParser();
        parser.setInput(reader);
        try {
            return build(parser, trim);
        }
        finally {
            IOUtil.close(reader);
        }
    }
    
    public static Xpp3Dom build(final XmlPullParser parser) throws XmlPullParserException, IOException {
        return build(parser, true);
    }
    
    public static Xpp3Dom build(final XmlPullParser parser, final boolean trim) throws XmlPullParserException, IOException {
        final List elements = new ArrayList();
        final List values = new ArrayList();
        for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
            if (eventType == 2) {
                final String rawName = parser.getName();
                final Xpp3Dom childConfiguration = new Xpp3Dom(rawName);
                final int depth = elements.size();
                if (depth > 0) {
                    final Xpp3Dom parent = elements.get(depth - 1);
                    parent.addChild(childConfiguration);
                }
                elements.add(childConfiguration);
                if (parser.isEmptyElementTag()) {
                    values.add(null);
                }
                else {
                    values.add(new StringBuffer());
                }
                for (int attributesSize = parser.getAttributeCount(), i = 0; i < attributesSize; ++i) {
                    final String name = parser.getAttributeName(i);
                    final String value = parser.getAttributeValue(i);
                    childConfiguration.setAttribute(name, value);
                }
            }
            else if (eventType == 4) {
                final int depth2 = values.size() - 1;
                final StringBuffer valueBuffer = values.get(depth2);
                String text = parser.getText();
                if (trim) {
                    text = text.trim();
                }
                valueBuffer.append(text);
            }
            else if (eventType == 3) {
                final int depth2 = elements.size() - 1;
                final Xpp3Dom finishedConfiguration = elements.remove(depth2);
                final Object accumulatedValue = values.remove(depth2);
                if (finishedConfiguration.getChildCount() == 0) {
                    if (accumulatedValue == null) {
                        finishedConfiguration.setValue(null);
                    }
                    else {
                        finishedConfiguration.setValue(accumulatedValue.toString());
                    }
                }
                if (depth2 == 0) {
                    return finishedConfiguration;
                }
            }
        }
        throw new IllegalStateException("End of document found before returning to 0 depth");
    }
}
