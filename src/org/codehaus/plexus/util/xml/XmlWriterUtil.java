// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import org.codehaus.plexus.util.StringUtils;

public class XmlWriterUtil
{
    public static final String LS;
    public static final int DEFAULT_INDENTATION_SIZE = 2;
    public static final int DEFAULT_COLUMN_LINE = 80;
    
    public static void writeLineBreak(final XMLWriter writer) {
        writeLineBreak(writer, 1);
    }
    
    public static void writeLineBreak(final XMLWriter writer, final int repeat) {
        for (int i = 0; i < repeat; ++i) {
            writer.writeMarkup(XmlWriterUtil.LS);
        }
    }
    
    public static void writeLineBreak(final XMLWriter writer, final int repeat, final int indent) {
        writeLineBreak(writer, repeat, indent, 2);
    }
    
    public static void writeLineBreak(final XMLWriter writer, final int repeat, int indent, int indentSize) {
        writeLineBreak(writer, repeat);
        if (indent < 0) {
            indent = 0;
        }
        if (indentSize < 0) {
            indentSize = 0;
        }
        writer.writeText(StringUtils.repeat(" ", indent * indentSize));
    }
    
    public static void writeCommentLineBreak(final XMLWriter writer) {
        writeCommentLineBreak(writer, 80);
    }
    
    public static void writeCommentLineBreak(final XMLWriter writer, int columnSize) {
        if (columnSize < 10) {
            columnSize = 80;
        }
        writer.writeMarkup("<!-- " + StringUtils.repeat("=", columnSize - 10) + " -->" + XmlWriterUtil.LS);
    }
    
    public static void writeComment(final XMLWriter writer, final String comment) {
        writeComment(writer, comment, 0, 2);
    }
    
    public static void writeComment(final XMLWriter writer, final String comment, final int indent) {
        writeComment(writer, comment, indent, 2);
    }
    
    public static void writeComment(final XMLWriter writer, final String comment, final int indent, final int indentSize) {
        writeComment(writer, comment, indent, indentSize, 80);
    }
    
    public static void writeComment(final XMLWriter writer, String comment, int indent, int indentSize, int columnSize) {
        if (comment == null) {
            comment = "null";
        }
        if (indent < 0) {
            indent = 0;
        }
        if (indentSize < 0) {
            indentSize = 0;
        }
        if (columnSize < 0) {
            columnSize = 80;
        }
        final String indentation = StringUtils.repeat(" ", indent * indentSize);
        final int magicNumber = indentation.length() + columnSize - "-->".length() - 1;
        final String[] sentences = StringUtils.split(comment, XmlWriterUtil.LS);
        StringBuffer line = new StringBuffer(indentation + "<!-- ");
        for (int i = 0; i < sentences.length; ++i) {
            final String sentence = sentences[i];
            final String[] words = StringUtils.split(sentence, " ");
            for (int j = 0; j < words.length; ++j) {
                final StringBuffer sentenceTmp = new StringBuffer(line.toString());
                sentenceTmp.append(words[j]).append(' ');
                if (sentenceTmp.length() > magicNumber) {
                    if (line.length() != indentation.length() + "<!-- ".length()) {
                        if (magicNumber - line.length() > 0) {
                            line.append(StringUtils.repeat(" ", magicNumber - line.length()));
                        }
                        line.append("-->").append(XmlWriterUtil.LS);
                        writer.writeMarkup(line.toString());
                    }
                    line = new StringBuffer(indentation + "<!-- ");
                    line.append(words[j]).append(' ');
                }
                else {
                    line.append(words[j]).append(' ');
                }
            }
            if (magicNumber - line.length() > 0) {
                line.append(StringUtils.repeat(" ", magicNumber - line.length()));
            }
        }
        if (line.length() <= magicNumber) {
            line.append(StringUtils.repeat(" ", magicNumber - line.length()));
        }
        line.append("-->").append(XmlWriterUtil.LS);
        writer.writeMarkup(line.toString());
    }
    
    public static void writeCommentText(final XMLWriter writer, final String comment) {
        writeCommentText(writer, comment, 0, 2);
    }
    
    public static void writeCommentText(final XMLWriter writer, final String comment, final int indent) {
        writeCommentText(writer, comment, indent, 2);
    }
    
    public static void writeCommentText(final XMLWriter writer, final String comment, final int indent, final int indentSize) {
        writeCommentText(writer, comment, indent, indentSize, 80);
    }
    
    public static void writeCommentText(final XMLWriter writer, final String comment, int indent, int indentSize, int columnSize) {
        if (indent < 0) {
            indent = 0;
        }
        if (indentSize < 0) {
            indentSize = 0;
        }
        if (columnSize < 0) {
            columnSize = 80;
        }
        writeLineBreak(writer, 1);
        writer.writeMarkup(StringUtils.repeat(" ", indent * indentSize));
        writeCommentLineBreak(writer, columnSize);
        writeComment(writer, comment, indent, indentSize, columnSize);
        writer.writeMarkup(StringUtils.repeat(" ", indent * indentSize));
        writeCommentLineBreak(writer, columnSize);
        writeLineBreak(writer, 1, indent, indentSize);
    }
    
    static {
        LS = System.getProperty("line.separator");
    }
}
