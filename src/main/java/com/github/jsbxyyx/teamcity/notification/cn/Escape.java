package com.github.jsbxyyx.teamcity.notification.cn;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author jsbxyyx
 */
public class Escape {

    public static String escapeJson(final String input) {
        if (input == null)
            return null;
        try {
            final StringWriter out = new StringWriter(input.length() * 2);
            escape(input, out);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void escape(final String input, final Writer out) throws IOException {
        final int len = input.length();
        for (int i = 0; i < len; i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '"':
                    out.append("\\\"");
                    break;
                case '\\':
                    out.append("\\\\");
                    break;
                case '\b':
                    out.append("\\b");
                    break;
                case '\f':
                    out.append("\\f");
                    break;
                case '\n':
                    out.append("\\n");
                    break;
                case '\r':
                    out.append("\\r");
                    break;
                case '\t':
                    out.append("\\t");
                    break;
                case '/':
                    out.append("\\/");
                    break;
                default:
                    // Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F')
                            || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        out.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            out.append('0');
                        }
                        out.append(ss.toUpperCase());
                    } else {
                        out.append(ch);
                    }
            }
        }
    }

}