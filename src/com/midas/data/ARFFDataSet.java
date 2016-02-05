package com.midas.data;

import java.util.Locale;
import unalcol.data.database.Record;
import unalcol.types.collection.vector.Vector;

/**
 *
 * @author tkd
 */
public class ARFFDataSet extends Vector<Record> {

    private String name;
    private Vector<ARFFAttribute> attributes;
    private Vector<ARFFAttribute> allNumericAttributes;
    private Vector<ARFFAttribute> allNominalAttributes; // including 'class'

    public ARFFDataSet(String name, Vector<ARFFAttribute> attributes) {
        this.name = name;
        this.attributes = new Vector<>();
        allNominalAttributes = new Vector<>();
        allNumericAttributes = new Vector<>();
        for (ARFFAttribute att : attributes) {
            if (att.getType().toUpperCase().equals(ARFFStream.NOMINAL_DTYPE)) {
                allNominalAttributes.add(att);
            } else {
                allNumericAttributes.add(att);
            }
            this.attributes.add(att);
        }
    }

    // // <value, count>
    public String[][] getValsNominalAttribute(ARFFAttribute att) {
        String ans[][] = new String[att.getValues().size()][2];
        int index = 0;
        for (String val : att.getValues()) {
            ans[index][0] = val;
            ans[index++][1] = String.valueOf(0);
        }
        int field = -1;
        index = 0;
        for (ARFFAttribute att2 : getAttributes()) {
            if (att2.getName().equals(att.getName())) {
                field = index;
                break;
            }
            index++;
        }
        for (Record rec : this) {
            for (int i = 0; i < ans.length; i++) {
                if (ans[i][0].equals(rec.field(field))) {
                    ans[i][1] = String.valueOf((int) (Integer.parseInt(ans[i][1]) + 1));
                    break;
                }
            }
        }
        return ans;
    }

    public String[] getValsNumericAttribute(ARFFAttribute att) {
        double summary[] = getSummary(att);
        return new String[]{String.format(Locale.US, "%.2f", summary[0]),
            String.format(Locale.US, "%.2f", summary[1]),
            String.format(Locale.US, "%.2f", summary[2]),
            String.format(Locale.US, "%.2f", summary[3])};
    }

    // min - max - media - stdDev
    private double[] getSummary(ARFFAttribute att) {
        int field = -1;
        int index = 0;
        for (ARFFAttribute att2 : getAttributes()) {
            if (att2.getName().equals(att.getName())) {
                field = index;
                break;
            }
            index++;
        }
        double ans[] = new double[4];
        ans[0] = Double.MAX_VALUE;
        ans[1] = Double.MIN_VALUE;
        int cnt = 0;
        for (Record rec : this) {
            if (rec.field(field) instanceof String) {
                ans[0] = Math.min(ans[0], Double.parseDouble((String) rec.field(field)));
                ans[1] = Math.max(ans[1], Double.parseDouble((String) rec.field(field)));
                ans[2] += Double.parseDouble((String) rec.field(field));
            } else {
                ans[0] = Math.min(ans[0], (Double) rec.field(field));
                ans[1] = Math.max(ans[1], (Double) rec.field(field));
                ans[2] += (Double) rec.field(field);
            }
            cnt++;
        }
        ans[2] /= (cnt == 0 ? 1 : cnt);
        for (Record rec : this) {
            if (rec.field(field) instanceof String) {
                ans[3] += Math.pow(Double.parseDouble((String) rec.field(field)) - ans[2], 2);
            } else {
                ans[3] += Math.pow(((Double) rec.field(field)) - ans[2], 2);
            }
        }
        ans[3] = Math.sqrt(ans[3] / (cnt == 0 ? 1 : cnt));
        return ans;
    }

    public String getName() {
        return name;
    }

    public Vector<ARFFAttribute> getAttributes() {
        return attributes;
    }

    public int getNumAttributes() {
        return attributes.size();
    }

    public int getNumNumericAttributes() {
        return allNumericAttributes.size();
    }

    public int getNumNominalAttributes() {
        return allNominalAttributes.size();
    }

    public Vector<ARFFAttribute> getNumericAttributes() {
        return allNumericAttributes;
    }

    public Vector<ARFFAttribute> getNominalAttributes() {
        return allNominalAttributes;
    }

    public int getCountForEachValue(ARFFAttribute att, String value) {
        int answer = 0;
        int attId = 0;
        for (ARFFAttribute attribute : attributes) {
            if (attribute.getName().equals(att.getName())) {
                break;
            }
            attId++;
        }
        for (Record rec : this) {
            if (rec.field(attId).toString().equals(value)) {
                answer++;
            }
        }
        return answer;
    }

    public Vector<Record> getRecordsForValue(ARFFAttribute att, String value) {
        Vector<Record> answer = new Vector<>();
        int attId = 0;
        for (ARFFAttribute attribute : attributes) {
            if (attribute.getName().equals(att.getName())) {
                break;
            }
            attId++;
        }
        for (Record rec : this) {
            if (rec.field(attId).toString().equals(value)) {
                answer.add(rec);
            }
        }
        return answer;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@RELATION ").append(name).append("\n");
        for (ARFFAttribute att : attributes) {
            stringBuilder.append("@ATTRIBUTE ").append(att.getName()).append("\t");
            if (att.getValues() == null) {
                stringBuilder.append(att.getType()).append("\n");
            } else {
                stringBuilder.append("{");
                boolean first = true;
                for (String value : att.getValues()) {
                    if (!first) {
                        stringBuilder.append(",");
                    }
                    first = false;
                    stringBuilder.append(value);
                }
                stringBuilder.append("}\n");
            }
        }
        stringBuilder.append("\n@DATA\n");
        for (Record rec : this) {
            boolean first = true;
            for (int i = 0; i < rec.size(); i++) {
                if (!first) {
                    stringBuilder.append(",");
                }
                first = false;
                stringBuilder.append(rec.field(i));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
