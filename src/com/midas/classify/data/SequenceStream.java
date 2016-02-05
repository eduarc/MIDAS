package com.midas.classify.data;

import static com.midas.data.ARFFStream.COMMENT_SEQ;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import unalcol.types.collection.vector.Vector;

/**
 *
 * @author tkd
 */
public class SequenceStream {

    private static int currentLine;

    public static Instances read(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        currentLine = 0;
        Instances ret = new Instances("sequence", new ArrayList<Attribute>());
        Map<String, Integer> atts = new HashMap<>();
        double values[];
        while ((line = reader.readLine()) != null) {
            currentLine++;
            line = line.trim();
            if (line.length() == 0 || line.startsWith(COMMENT_SEQ)) {
                continue;
            }
            line = normalice(line);
            Vector r = parse(line);
            values = new double[r.size()];
            for (int i = 0; i < r.size(); i++) {
                if (!atts.containsKey((String) r.get(i))) {
                    atts.put((String) r.get(i), atts.size());
                }
                values[i] = atts.get((String) r.get(i));
            }
            ret.add(new Instance(0, values));
        }
        ret.getAttributes().add(new Attribute("values", new ArrayList<String>(atts.keySet())));
        return ret;
    }

    private static Vector<String> parse(String src) throws IOException {
        Vector list = new Vector();
        src += ",";
        int len = src.length();
        String curr = "";

        for (int i = 0; i < len; ++i) {
            char c = src.charAt(i);
            // skip leading spaces
            if (c == ' ') {
                continue;
            }
            // add current token
            if (c == ',') {
                list.add(curr);
                curr = "";
                continue;
            } // -------------
            // Quoted string
            else if (c == '\'' || c == '\"') {
                for (++i; i < len; ++i) {
                    char v = src.charAt(i);
                    if (v == c) {
                        ++i;
                        break;
                    }
                    if (v == '\\') {
                        ++i;
                    }
                    if (i == len) {
                        throw new IOException("FileStream: Invalid scape sequence. line " + currentLine);
                    }
                    curr += src.charAt(i);
                }
                if (i == len) {
                    throw new IOException("FileStream: Missing " + c + " character. line " + currentLine);
                }
            } // ----------------
            // No spaced string
            else if (c != ' ') {
                for (; i < len; ++i) {
                    char v = src.charAt(i);
                    if (v == ' ' || v == ',') {
                        break;
                    }
                    if (c == '\\') {
                        ++i;
                    }
                    if (i == len) {
                        throw new IOException("FileStream: Invalid scape sequence. line " + currentLine);
                    }
                    curr += src.charAt(i);

                }
            }
            // skip trailing spaces
            while (i < len) {
                char v = src.charAt(i);
                if (v == ' ') {
                    ++i;
                } else if (v == ',') {
                    break;
                } else {
                    throw new IOException("FileStream: Unexpected token. line " + currentLine + " i: " + i);
                }
            }
            --i;
        }
        return list;
    }

    public static String normalice(String s) {
        return s.replace('\t', ' ');
    }
}
