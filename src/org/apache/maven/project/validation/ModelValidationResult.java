// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.validation;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class ModelValidationResult
{
    private static final String NEWLINE;
    private List messages;
    
    public ModelValidationResult() {
        this.messages = new ArrayList();
    }
    
    public int getMessageCount() {
        return this.messages.size();
    }
    
    public String getMessage(final int i) {
        return this.messages.get(i).toString();
    }
    
    public List getMessages() {
        return Collections.unmodifiableList((List<?>)this.messages);
    }
    
    public void addMessage(final String message) {
        this.messages.add(message);
    }
    
    @Override
    public String toString() {
        return this.render("");
    }
    
    public String render(final String indentation) {
        if (this.messages.size() == 0) {
            return indentation + "There were no validation errors.";
        }
        final StringBuffer message = new StringBuffer();
        for (int i = 0; i < this.messages.size(); ++i) {
            message.append(indentation + "[" + i + "]  " + this.messages.get(i).toString() + ModelValidationResult.NEWLINE);
        }
        return message.toString();
    }
    
    static {
        NEWLINE = System.getProperty("line.separator");
    }
}
