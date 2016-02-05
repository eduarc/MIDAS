package com.midas.data;

import com.midas.classify.data.SequenceStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import unalcol.data.database.Record;
import unalcol.types.collection.vector.Vector;

/**
 *
 * @author tkd
 */
public class ARFFStream {

    public static final String COMMENT_SEQ = "%";
    public static final String RELATION_TAG = "@RELATION";
    public static final String ATTRIBUTE_TAG = "@ATTRIBUTE";
    public static final String DATA_TAG = "@DATA";

    public static final String NUMERIC_DTYPE = "NUMERIC";
    public static final String REAL_DTYPE = "REAL";
    public static final String INTEGER_DTYPE = "INTEGER";
    public static final String STRING_DTYPE = "STRING";
    public static final String NOMINAL_DTYPE = "}";

    private static int currentLine;

    public static ARFFDataSet read(InputStream stream) throws IOException {

        currentLine = 0;
        Vector<String> attributeName = new Vector();
        Vector<String> attributeType = new Vector();
        Vector<Vector<String>> nominalValues = new Vector();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        String relationName = readRelationName(reader);
        String nextTag = readAttributes(reader, attributeName, attributeType, nominalValues);

        int n = attributeName.size();

        Vector<ARFFAttribute> attributes = new Vector();

        for (int i = 0; i < n; ++i) {
            ARFFAttribute attr = new ARFFAttribute();
            String t = attributeType.get(i);

            //dataSet.setAttributeType(i, t);
            //dataSet.setAttributeName(i,attributeName.get(i));
            if (t.equals(NOMINAL_DTYPE)) {
                //dataSet.setNominalValues(i,nominalValues.get(i));
                attr.setValues(nominalValues.get(i));
            }
            attr.setType(t);
            attr.setName(attributeName.get(i));
            attributes.add(attr);
        }

        ARFFDataSet dataSet = new ARFFDataSet(relationName, attributes);

        readData(reader, dataSet, nextTag);
        return dataSet;
    }

    private static String readRelationName(BufferedReader reader) throws IOException {

        String line;
        while ((line = reader.readLine()) != null) {
            ++currentLine;

            line = line.trim();
            if (line.length() == 0 || line.startsWith(COMMENT_SEQ)) {
                continue;
            }
            line = SequenceStream.normalice(line);

            String upper = line.toUpperCase();
            if (upper.startsWith(RELATION_TAG)) {
                line = line.substring(RELATION_TAG.length());
                return parse(line, 1).get(0);
            } else {
                throw new IOException("ARFFStream: @RELATION not found");
            }
        }
        return null;
    }

    private static String readAttributes(BufferedReader reader, Vector<String> attributeName,
            Vector<String> attributeType, Vector<Vector<String>> nominalValues) throws IOException {

        String line, upper;

        while ((line = reader.readLine()) != null) {
            ++currentLine;

            line = line.trim();
            if (line.length() == 0 || line.startsWith(COMMENT_SEQ)) {
                continue;
            }
            line = SequenceStream.normalice(line);
            upper = line.toUpperCase();

            if (upper.startsWith(ATTRIBUTE_TAG)) {

                line = line.substring(ATTRIBUTE_TAG.length());
                line = line.trim();
                upper = line.toUpperCase();

                // -----------------------
                // ATTRIBUTE, NOMINAL LIST
                if (upper.endsWith(NOMINAL_DTYPE)) {
                    String nominalList = null;
                    for (int i = line.length() - 2;; --i) {
                        if (line.charAt(i) == '{') {
                            nominalList = line.substring(i + 1, line.length() - 1);
                            line = line.substring(0, i);
                            break;
                        }
                    }
                    // error
                    if (nominalList == null) {
                        throw new IOException("ARFFStream: Invalid nominal list. line " + currentLine);
                    }
                    attributeName.add(parse(line, 1).get(0));
                    attributeType.add(NOMINAL_DTYPE);
                    nominalValues.add(parse(nominalList, Integer.MAX_VALUE));
                } else if (upper.endsWith(" " + NUMERIC_DTYPE)
                        || upper.endsWith(" " + REAL_DTYPE)
                        || upper.endsWith(" " + INTEGER_DTYPE)
                        || upper.endsWith(" " + STRING_DTYPE)) {

                    String type = "";

                    for (int i = upper.length() - 1; i >= 0; --i) {
                        if (upper.charAt(i) == ' ') {
                            type = upper.substring(i + 1);
                            line = line.substring(0, i);
                            break;
                        }
                    }
                    String name = parse(line, 1).get(0);
                    attributeName.add(name);
                    nominalValues.add(null);

                    switch (type) {
                        case NUMERIC_DTYPE:
                            attributeType.add(NUMERIC_DTYPE);
                            break;
                        case REAL_DTYPE:
                            attributeType.add(REAL_DTYPE);
                            break;
                        case INTEGER_DTYPE:
                            attributeType.add(INTEGER_DTYPE);
                            break;
                        case STRING_DTYPE:
                            attributeType.add(STRING_DTYPE);
                            break;
                    }
                }
            } // -----------------------
            // ended list of attributes
            else {
                break;
            }
        }
        if (attributeName.isEmpty()) {
            throw new IOException("ARFFStream: @ATTRIBUTE not found. line " + currentLine);
        }
        return line;
    }

    private static void readData(BufferedReader reader, ARFFDataSet dataSet, String header) throws IOException {
        // check header
        if (!header.toUpperCase().endsWith(ARFFStream.DATA_TAG)) {
            throw new IOException("ARFFStream: @DATA tag not found. line " + currentLine);
        }
        int n = dataSet.getNumAttributes();
        String line;

        while ((line = reader.readLine()) != null) {
            ++currentLine;

            line = line.trim();
            if (line.length() == 0 || line.startsWith(COMMENT_SEQ)) {
                continue;
            }
            line = SequenceStream.normalice(line);
            Vector r = parse(line, n);
            if (r.size() < n) {
                throw new IOException("ARFFStream: Missing tokens. line " + currentLine);
            }
            dataSet.add(new Record(r));
        }
    }

    private static Vector<String> parse(String src, int maxTokens) throws IOException {
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
            if (list.size() == maxTokens) {
                throw new IOException("ARFFStream: Unexpected token. line " + currentLine + " i: " + i);
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
                        throw new IOException("ARFFStream: Invalid scape sequence. line " + currentLine);
                    }
                    curr += src.charAt(i);
                }
                if (i == len) {
                    throw new IOException("ARFFStream: Missing " + c + " character. line " + currentLine);
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
                        throw new IOException("ARFFStream: Invalid scape sequence. line " + currentLine);
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
                    throw new IOException("ARFFStream: Unexpected token. line " + currentLine + " i: " + i);
                }
            }
            --i;
        }
        return list;
    }
}
