// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Xpp3Dom
{
    protected String name;
    protected String value;
    protected Map attributes;
    protected final List childList;
    protected final Map childMap;
    protected Xpp3Dom parent;
    private static final Xpp3Dom[] EMPTY_DOM_ARRAY;
    public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";
    public static final String CHILDREN_COMBINATION_MERGE = "merge";
    public static final String CHILDREN_COMBINATION_APPEND = "append";
    public static final String DEFAULT_CHILDREN_COMBINATION_MODE = "merge";
    public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";
    public static final String SELF_COMBINATION_OVERRIDE = "override";
    public static final String SELF_COMBINATION_MERGE = "merge";
    public static final String DEFAULT_SELF_COMBINATION_MODE = "merge";
    
    public Xpp3Dom(final String name) {
        this.name = name;
        this.childList = new ArrayList();
        this.childMap = new HashMap();
    }
    
    public Xpp3Dom(final Xpp3Dom src) {
        this(src, src.getName());
    }
    
    public Xpp3Dom(final Xpp3Dom src, final String name) {
        this(name);
        this.setValue(src.getValue());
        final String[] attributeNames = src.getAttributeNames();
        for (int i = 0; i < attributeNames.length; ++i) {
            final String attributeName = attributeNames[i];
            this.setAttribute(attributeName, src.getAttribute(attributeName));
        }
        final Xpp3Dom[] children = src.getChildren();
        for (int j = 0; j < children.length; ++j) {
            this.addChild(new Xpp3Dom(children[j]));
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public String[] getAttributeNames() {
        if (null == this.attributes) {
            return new String[0];
        }
        return (String[])this.attributes.keySet().toArray(new String[0]);
    }
    
    public String getAttribute(final String name) {
        return (null != this.attributes) ? this.attributes.get(name) : null;
    }
    
    public void setAttribute(final String name, final String value) {
        if (null == value) {
            throw new NullPointerException("Attribute value can not be null");
        }
        if (null == name) {
            throw new NullPointerException("Attribute name can not be null");
        }
        if (null == this.attributes) {
            this.attributes = new HashMap();
        }
        this.attributes.put(name, value);
    }
    
    public Xpp3Dom getChild(final int i) {
        return this.childList.get(i);
    }
    
    public Xpp3Dom getChild(final String name) {
        return this.childMap.get(name);
    }
    
    public void addChild(final Xpp3Dom xpp3Dom) {
        xpp3Dom.setParent(this);
        this.childList.add(xpp3Dom);
        this.childMap.put(xpp3Dom.getName(), xpp3Dom);
    }
    
    public Xpp3Dom[] getChildren() {
        if (null == this.childList) {
            return Xpp3Dom.EMPTY_DOM_ARRAY;
        }
        return this.childList.toArray(Xpp3Dom.EMPTY_DOM_ARRAY);
    }
    
    public Xpp3Dom[] getChildren(final String name) {
        if (null == this.childList) {
            return Xpp3Dom.EMPTY_DOM_ARRAY;
        }
        final ArrayList children = new ArrayList();
        for (int size = this.childList.size(), i = 0; i < size; ++i) {
            final Xpp3Dom configuration = this.childList.get(i);
            if (name.equals(configuration.getName())) {
                children.add(configuration);
            }
        }
        return children.toArray(Xpp3Dom.EMPTY_DOM_ARRAY);
    }
    
    public int getChildCount() {
        if (null == this.childList) {
            return 0;
        }
        return this.childList.size();
    }
    
    public void removeChild(final int i) {
        final Xpp3Dom child = this.getChild(i);
        this.childMap.values().remove(child);
        this.childList.remove(i);
        child.setParent(null);
    }
    
    public Xpp3Dom getParent() {
        return this.parent;
    }
    
    public void setParent(final Xpp3Dom parent) {
        this.parent = parent;
    }
    
    public void writeToSerializer(final String namespace, final XmlSerializer serializer) throws IOException {
        final SerializerXMLWriter xmlWriter = new SerializerXMLWriter(namespace, serializer);
        Xpp3DomWriter.write(xmlWriter, this);
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
            final Xpp3Dom[] dominantChildren = dominant.getChildren();
            if (!mergeChildren) {
                dominant.childList.clear();
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
            if (!mergeChildren) {
                for (int j = 0; j < dominantChildren.length; ++j) {
                    dominant.addChild(dominantChildren[j]);
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
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Xpp3Dom)) {
            return false;
        }
        final Xpp3Dom dom = (Xpp3Dom)obj;
        Label_0054: {
            if (this.name == null) {
                if (dom.name == null) {
                    break Label_0054;
                }
            }
            else if (this.name.equals(dom.name)) {
                break Label_0054;
            }
            return false;
        }
        Label_0087: {
            if (this.value == null) {
                if (dom.value == null) {
                    break Label_0087;
                }
            }
            else if (this.value.equals(dom.value)) {
                break Label_0087;
            }
            return false;
        }
        Label_0120: {
            if (this.attributes == null) {
                if (dom.attributes == null) {
                    break Label_0120;
                }
            }
            else if (this.attributes.equals(dom.attributes)) {
                break Label_0120;
            }
            return false;
        }
        if (this.childList == null) {
            if (dom.childList == null) {
                return true;
            }
        }
        else if (this.childList.equals(dom.childList)) {
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        int result = 17;
        result = 37 * result + ((this.name != null) ? this.name.hashCode() : 0);
        result = 37 * result + ((this.value != null) ? this.value.hashCode() : 0);
        result = 37 * result + ((this.attributes != null) ? this.attributes.hashCode() : 0);
        result = 37 * result + ((this.childList != null) ? this.childList.hashCode() : 0);
        return result;
    }
    
    public String toString() {
        final StringWriter writer = new StringWriter();
        final XMLWriter xmlWriter = new PrettyPrintXMLWriter(writer, "UTF-8", null);
        Xpp3DomWriter.write(xmlWriter, this);
        return writer.toString();
    }
    
    public String toUnescapedString() {
        final StringWriter writer = new StringWriter();
        final XMLWriter xmlWriter = new PrettyPrintXMLWriter(writer, "UTF-8", null);
        Xpp3DomWriter.write(xmlWriter, this, false);
        return writer.toString();
    }
    
    public static boolean isNotEmpty(final String str) {
        return str != null && str.length() > 0;
    }
    
    public static boolean isEmpty(final String str) {
        return str == null || str.trim().length() == 0;
    }
    
    static {
        EMPTY_DOM_ARRAY = new Xpp3Dom[0];
    }
}
