// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import java.io.IOException;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class Xpp3DomUtils
{
    public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";
    public static final String CHILDREN_COMBINATION_MERGE = "merge";
    public static final String CHILDREN_COMBINATION_APPEND = "append";
    public static final String DEFAULT_CHILDREN_COMBINATION_MODE = "merge";
    public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";
    public static final String SELF_COMBINATION_OVERRIDE = "override";
    public static final String SELF_COMBINATION_MERGE = "merge";
    public static final String DEFAULT_SELF_COMBINATION_MODE = "merge";
    
    public void writeToSerializer(final String namespace, final XmlSerializer serializer, final Xpp3Dom dom) throws IOException {
        final SerializerXMLWriter xmlWriter = new SerializerXMLWriter(namespace, serializer);
        Xpp3DomWriter.write(xmlWriter, dom);
        if (xmlWriter.getExceptions().size() > 0) {
            throw (IOException)xmlWriter.getExceptions().get(0);
        }
    }
    
    private static void mergeIntoXpp3Dom(final Xpp3Dom dominant, final Xpp3Dom recessive, final Boolean childMergeOverride) {
        if (recessive == null) {
            return;
        }
        boolean mergeSelf = true;
        final String selfMergeMode = dominant.getAttribute("combine.self");
        if (isNotEmpty(selfMergeMode) && "override".equals(selfMergeMode)) {
            mergeSelf = false;
        }
        if (mergeSelf) {
            if (isEmpty(dominant.getValue())) {
                dominant.setValue(recessive.getValue());
            }
            final String[] recessiveAttrs = recessive.getAttributeNames();
            for (int i = 0; i < recessiveAttrs.length; ++i) {
                final String attr = recessiveAttrs[i];
                if (isEmpty(dominant.getAttribute(attr))) {
                    dominant.setAttribute(attr, recessive.getAttribute(attr));
                }
            }
            boolean mergeChildren = true;
            if (childMergeOverride != null) {
                mergeChildren = childMergeOverride;
            }
            else {
                final String childMergeMode = dominant.getAttribute("combine.children");
                if (isNotEmpty(childMergeMode) && "append".equals(childMergeMode)) {
                    mergeChildren = false;
                }
            }
            final Xpp3Dom[] children = recessive.getChildren();
            for (int j = 0; j < children.length; ++j) {
                final Xpp3Dom child = children[j];
                final Xpp3Dom childDom = dominant.getChild(child.getName());
                if (mergeChildren && childDom != null) {
                    mergeIntoXpp3Dom(childDom, child, childMergeOverride);
                }
                else {
                    dominant.addChild(new Xpp3Dom(child));
                }
            }
        }
    }
    
    public static Xpp3Dom mergeXpp3Dom(final Xpp3Dom dominant, final Xpp3Dom recessive, final Boolean childMergeOverride) {
        if (dominant != null) {
            mergeIntoXpp3Dom(dominant, recessive, childMergeOverride);
            return dominant;
        }
        return recessive;
    }
    
    public static Xpp3Dom mergeXpp3Dom(final Xpp3Dom dominant, final Xpp3Dom recessive) {
        if (dominant != null) {
            mergeIntoXpp3Dom(dominant, recessive, null);
            return dominant;
        }
        return recessive;
    }
    
    public static boolean isNotEmpty(final String str) {
        return str != null && str.length() > 0;
    }
    
    public static boolean isEmpty(final String str) {
        return str == null || str.trim().length() == 0;
    }
}
