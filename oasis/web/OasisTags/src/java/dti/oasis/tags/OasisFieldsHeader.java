package dti.oasis.tags;

import dti.oasis.util.LogUtils;
import dti.oasis.util.FormatUtils;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Special class for laying out fields in a header.
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 10, 2007
 *
 * @author GCCarney
 */
/*
 * Revision Date    Revised By  Description
 * ----------------------------------------------------------------------------
 * 08/21/2007       GCC         Created new method populateFieldsMaps to
 *                              replace three separate methods used previously.
 * 06/27/2016       Parker      Issue#177786 Remove final String in local method
 *
 * ----------------------------------------------------------------------------
*/

public class OasisFieldsHeader {

    private OasisFields fieldsMapHidden = null;
    private OasisFields fieldsMapFirstCol = null;
    private OasisFields fieldsMapSecondCol = null;
    private OasisFields fieldsMapThirdCol = null;
    private OasisFields fieldsMapDisplayAtBottom = null;


    protected OasisFieldsHeader() {
    }

    /**
     * Constructor with OasisFields argument
     * @param fieldsMap   OasisFields object from which to create this object
     */
    protected OasisFieldsHeader(OasisFields fieldsMap) {
        this.populateFieldsMaps(fieldsMap, null, null);
    }


    /**
     * Constructor with OasisFields argument
     * @param fieldsMap   OasisFields object from which to create this object
     * @param fldsToExcludeFromDisplay   String array of field IDs to exclude from display
     * @param fldsToDisplayAtBottom   String array of field IDs to display at bottom of header
     */
    protected OasisFieldsHeader(OasisFields fieldsMap, String[] fldsToExcludeFromDisplay,
            String[] fldsToDisplayAtBottom) {
        this.populateFieldsMaps(fieldsMap,
            fldsToExcludeFromDisplay, fldsToDisplayAtBottom);
    }

    public static OasisFieldsHeader createInstance(OasisFields fieldsMap) {
        return new OasisFieldsHeader(fieldsMap);
    }

    public static OasisFieldsHeader createInstance(OasisFields fieldsMap,
            String[] fldsToExclude, String[] fldsToDisplayAtBottom) {
        return new OasisFieldsHeader(fieldsMap, fldsToExclude,
                fldsToDisplayAtBottom);
    }

    public OasisFields getFieldsMapHidden() {
        return fieldsMapHidden;
    }

    public OasisFields getFieldsMapFirstCol() {
        return fieldsMapFirstCol;
    }

    public OasisFields getFieldsMapSecondCol() {
        return fieldsMapSecondCol;
    }

    public OasisFields getFieldsMapThirdCol() {
        return fieldsMapThirdCol;
    }

    public OasisFields getFieldsMapDisplayAtBottom() {
        return fieldsMapDisplayAtBottom;
    }

    public String toString() {
        return new StringBuffer().
                append("OasisFieldsHeader{").
                append("fieldsMapHidden=").
                append(fieldsMapHidden).
                append(", fieldsMapFirstCol=").
                append(fieldsMapFirstCol).
                append(", fieldsMapSecondCol=").
                append(fieldsMapSecondCol).
                append(", fieldsMapThirdCol=").
                append(fieldsMapThirdCol).
                append(", fieldsMapDisplayAtBottom=").
                append(fieldsMapDisplayAtBottom).
                append('}').toString();
    }


    /**
     * Populates the various OasisFields properties of this object.
     * @param fieldsMap   OasisFields object
     * @param fldsToExcludeFromDisplay   String array of field IDs to exclude from display
     * @param fldsToDisplayAtBottom      String array of field IDs to display at bottom of header
     */
    private void populateFieldsMaps(OasisFields fieldsMap,
            String[] fldsToExcludeFromDisplay, String[] fldsToDisplayAtBottom) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "populateFieldsMaps", new Object[]{fieldsMap, fldsToExcludeFromDisplay, fldsToDisplayAtBottom});
        }
        if (fieldsMap == null) {
            l.logp(Level.WARNING, getClass().getName(), "populateFieldsMaps", "fields map arg is null");
            return;
        }

        fieldsMapFirstCol = new OasisFields();
        fieldsMapSecondCol = new OasisFields();
        fieldsMapThirdCol = new OasisFields();
        fieldsMapHidden = new OasisFields();
        fieldsMapDisplayAtBottom = new OasisFields();

        Iterator itr = fieldsMap.keySet().iterator();
        String mapKey = null;
        OasisFormField tmpFld = null;
        ArrayList fldsToDisplay = new ArrayList(fieldsMap.size());
        boolean excludeFld = false;
        boolean fldIsAtBottom = false;

        while (itr.hasNext()) {
            mapKey = (String) itr.next();
            if (mapKey != null) {
                if (fieldsMap.get(mapKey) instanceof OasisFormField) {
                    // Get a pointer to the field.
                    tmpFld = (OasisFormField) fieldsMap.get(mapKey);
                    if (tmpFld != null) {

                        if (!tmpFld.getIsVisible()) {
                            // Put the field in the hidden fields map.
                            fieldsMapHidden.put(tmpFld.getFieldId(), tmpFld);
                            continue;
                        }

                        fldIsAtBottom = false;
                        if (fldsToDisplayAtBottom != null
                                && fldsToDisplayAtBottom.length >= 1) {
                            for (int i = 0; i < fldsToDisplayAtBottom.length; i++) {
                                if (tmpFld.getFieldId().equals(fldsToDisplayAtBottom[i]) ) {
                                    fldIsAtBottom = true;
                                    break;
                                }
                            }
                        }
                        if (fldIsAtBottom) {
                            // Put the field in the fields at bottom map.
                            fieldsMapDisplayAtBottom.put(tmpFld.getFieldId(), tmpFld);
                            continue;
                        }

                        if (tmpFld.getIsVisible()) {
                            excludeFld = false;
                            if (fieldsMapDisplayAtBottom != null
                                    && fieldsMapDisplayAtBottom.size() >= 1
                                    && fieldsMapDisplayAtBottom.containsKey(tmpFld.getFieldId())) {
                                // Field is being displayed at the bottom of the
                                // header (not in one of the three columns);  exclude it.
                                excludeFld = true;
                            }
                            if (!excludeFld && fldsToExcludeFromDisplay != null
                                    && fldsToExcludeFromDisplay.length >= 1) {
                                for (int i = 0; i < fldsToExcludeFromDisplay.length; i++) {
                                    if (fldsToExcludeFromDisplay[i].equals(tmpFld.getFieldId())) {
                                        // Field is supposed to be excluded.
                                        excludeFld = true;
                                        break;
                                    }
                                }
                            }
                            if (!excludeFld) {
                                // Add the field to the ArrayList.
                                fldsToDisplay.add(tmpFld);
                            }
                        }
                    }
                }
            }
        }

        // DEBUGGING ONLY BELOW
//        if (fieldsMapDisplayAtBottom != null && fieldsMapDisplayAtBottom.size() >= 1) {
//            Iterator debugItr = fieldsMapDisplayAtBottom.keySet().iterator();
//            int i = 0;
//            while (debugItr.hasNext()) {
//                mapKey = (String) debugItr.next();
//                if (mapKey != null) {
//                    if (fieldsMap.get(mapKey) instanceof OasisFormField) {
//                        // Get a pointer to the field.
//                        tmpFld = (OasisFormField) fieldsMapDisplayAtBottom.get(mapKey);
//                        lggr.log(Level.FINE, new StringBuffer().
//                                append("element ").append(i).
//                                append(" of fieldsMapDisplayAtBottom is ").append(tmpFld.getFieldId()).
//                                toString());
//                    }
//                }
//            }
//        }
//        else {
//            if (fieldsMapDisplayAtBottom == null) {
//                lggr.log(Level.FINE, "fieldsMapDisplayAtBottom is null");
//            }
//            else if (fieldsMapDisplayAtBottom.size() == 0) {
//                lggr.log(Level.FINE, "fieldsMapDisplayAtBottom size is 0");
//            }
//        }
        // DEBUGGING ONLY ABOVE

        // DEBUGGING ONLY BELOW
//        if (fieldsMapHidden != null && fieldsMapHidden.size() >= 1) {
//            Iterator debugItr = fieldsMapHidden.keySet().iterator();
//            int i = 0;
//            while (debugItr.hasNext()) {
//                mapKey = (String) debugItr.next();
//                if (mapKey != null) {
//                    if (fieldsMap.get(mapKey) instanceof OasisFormField) {
//                        // Get a pointer to the field.
//                        tmpFld = (OasisFormField) fieldsMapHidden.get(mapKey);
//                        lggr.log(Level.FINE, new StringBuffer().
//                                append("element ").append(i).
//                                append(" of fieldsMapHidden is ").append(tmpFld.getFieldId()).
//                                toString());
//                        i++;
//                    }
//                }
//            }
//        }
//        else {
//            if (fieldsMapHidden == null) {
//                lggr.log(Level.FINE, "fieldsMapHidden is null");
//            }
//            else if (fieldsMapHidden.size() == 0) {
//                lggr.log(Level.FINE, "fieldsMapHidden size is 0");
//            }
//        }
        // DEBUGGING ONLY ABOVE

        String currentRow = "-999999";
        boolean firstRow = true;
        tmpFld = null;
        LinkedHashMap rowsToProcess = new LinkedHashMap(15);
        ArrayList visibleFldsInRow = new ArrayList(5);
        // Loop through the ArrayList of fields that we are going to
        // display in the header.
        for (int i = 0; i < fldsToDisplay.size(); i++) {
            tmpFld = (OasisFormField) fldsToDisplay.get(i);
            if (!tmpFld.getRowNum().equals(currentRow)) {
                // Row change.
                if (firstRow) {
                    // We are on the first row.
                    firstRow = false;
                }
                else {
                    // We are on the second row or later.
                    // Put the visible fields in row ArrayList into the
                    // LinkedHashMap with the row number as the key.
                    rowsToProcess.put(currentRow, visibleFldsInRow);
                    // Re-initialize the visible fields in row ArrayList.
                    visibleFldsInRow = new ArrayList(5);
                }
                currentRow = tmpFld.getRowNum();
            }
            // Add the current field to the visible fields in row ArrayList.
            visibleFldsInRow.add(tmpFld.getFieldId());
        }
        // Put the visible fields in row ArrayList for the last row
        // into theLinkedHashMap with the last row number as the key.
        rowsToProcess.put(currentRow, visibleFldsInRow);

        // DEBUGGING ONLY BELOW
//        ArrayList tmpAl = null;
//        Iterator debugItr = rowsToProcess.keySet().iterator();
//        while (debugItr.hasNext()) {
//            mapKey = (String) debugItr.next();
//            if (mapKey != null) {
//                lggr.log(Level.FINE, new StringBuffer().append("key for row is ").
//                        append(mapKey).toString());
//                if (rowsToProcess.get(mapKey) instanceof ArrayList) {
//                    tmpAl = (ArrayList) rowsToProcess.get(mapKey);
//                    if (tmpAl != null && tmpAl.size() >= 1) {
//                        for (int i = 0; i < tmpAl.size(); i++) {
//                            lggr.log(Level.FINE, new StringBuffer().append("element ").
//                                    append(i).append(" of ").append(mapKey).
//                                    append(" is ").append(tmpAl.get(i)).toString());
//                        }
//                    }
//
//                }
//            }
//        }
        // DEBUGGING ONLY ABOVE

        ArrayList alFldsInRow = null;
        itr = rowsToProcess.keySet().iterator();

        int visibleFldsInRowCount = 0;

        int moduloVal = 0;

        String curFldID = null;
        String curFldID2 = null;
        OasisFormField tmpFld2 = null;

        int emptyCellsBeforeFirstFld = 0;
        int emptyCellsAfterFirstFld = 0;
        int emptyCellsBeforeSecondFld = 0;
        int emptyCellsAfterSecondFld = 0;

        OasisFormField emptyFormField = new OasisFormField();
        final String szEmpty = "EMPTY";
        emptyFormField.setDisplayType(szEmpty);

        int emptyFldsCnt = 0;

        // Iterate through the rows to process LinkedHashMap.
        while (itr.hasNext()) {
            mapKey = (String) itr.next();
            if (mapKey != null) {
                if (rowsToProcess.get(mapKey) instanceof ArrayList) {
                    alFldsInRow = (ArrayList) rowsToProcess.get(mapKey);
                    if (alFldsInRow != null) {
                        visibleFldsInRowCount = 0;
                        if (alFldsInRow.size() >= 3) {
                            // If three or more fields in the row (there
                            // should not be mor ethan three), then loop
                            // through the fields.
                            for (int i = 0; i < alFldsInRow.size(); i++) {
                                curFldID = (String) alFldsInRow.get(i);
                                tmpFld = (OasisFormField) fieldsMap.get(curFldID);
                                if (tmpFld != null && tmpFld.getIsVisible()) {
                                    visibleFldsInRowCount++;
                                    moduloVal = visibleFldsInRowCount % 3;
                                    if (moduloVal == 1) {
                                        // Put field 1 (or 4 or 7 or 10, etc.) into
                                        // the first column.
                                        fieldsMapFirstCol.put(tmpFld.getFieldId(), tmpFld);
                                    }
                                    else if (moduloVal == 2) {
                                        // Put field 2 (or 5 or 8 or 11, etc.) into
                                        // the second column.
                                        fieldsMapSecondCol.put(tmpFld.getFieldId(), tmpFld);
                                    }
                                    else {
                                        // Put field 3 (or 6 or 9 or 12, etc.) into
                                        // the third column.
                                        fieldsMapThirdCol.put(tmpFld.getFieldId(), tmpFld);
                                    }
                                }
                            }
                        }
                        else if (alFldsInRow.size() == 1) {
                            // Only one field in the row.
                            // Take empty cells into account.
                            curFldID = (String) alFldsInRow.get(0);
                            tmpFld = (OasisFormField) fieldsMap.get(curFldID);
                            emptyCellsBeforeFirstFld = 0;
                            emptyCellsAfterFirstFld = 0;
                            if (FormatUtils.isInt(tmpFld.getEmptyCellsBeforeFld())) {
                                emptyCellsBeforeFirstFld = Integer.parseInt(tmpFld.getEmptyCellsBeforeFld());
                            }
                            if (FormatUtils.isInt(tmpFld.getEmptyCellsAfterFld())) {
                                emptyCellsAfterFirstFld = Integer.parseInt(tmpFld.getEmptyCellsAfterFld());
                            }
                            if (emptyCellsBeforeFirstFld == 2) {
                                emptyFldsCnt++;
                                // Put empty field in col. 1 and first real
                                // field in col. 2.
                                fieldsMapFirstCol.put(
                                        new StringBuffer().append(szEmpty).
                                                append(emptyFldsCnt).toString(),
                                        emptyFormField);
                                fieldsMapSecondCol.put(tmpFld.getFieldId(), tmpFld);

                            }
                            else if (emptyCellsBeforeFirstFld == 4) {
                                emptyFldsCnt++;
                                // Put empty fields in cols. 1 and 2 and first
                                // real field in col. 3.
                                fieldsMapFirstCol.put(
                                        new StringBuffer().append(szEmpty).
                                                append(emptyFldsCnt).toString(),
                                        emptyFormField);
                                emptyFldsCnt++;
                                fieldsMapSecondCol.put(
                                        new StringBuffer().append(szEmpty).
                                                append(emptyFldsCnt).toString(),
                                        emptyFormField);
                                fieldsMapThirdCol.put(tmpFld.getFieldId(), tmpFld);
                            }
                            else {
                                fieldsMapFirstCol.put(tmpFld.getFieldId(), tmpFld);
                                // Put first real field in col. 1.
                                if (emptyCellsAfterFirstFld == 2
                                        || emptyCellsAfterFirstFld == 4) {
                                    // Put empty field in col. 1.
                                    fieldsMapSecondCol.put(
                                            new StringBuffer().append(szEmpty).
                                                    append(emptyFldsCnt).toString(),
                                            emptyFormField);
                                }
                                if (emptyCellsAfterFirstFld == 4) {
                                    // Put empty field in col. 3.
                                    fieldsMapThirdCol.put(
                                            new StringBuffer().append(szEmpty).
                                                    append(emptyFldsCnt).toString(),
                                            emptyFormField);
                                }
                            }
                        }
                        else if (alFldsInRow.size() == 2) {
                            // Only two fields in the row.
                            // Take empty cells into account.

                            curFldID = (String) alFldsInRow.get(0);
                            tmpFld = (OasisFormField) fieldsMap.get(curFldID);
                            curFldID2 = (String) alFldsInRow.get(1);
                            tmpFld2 = (OasisFormField) fieldsMap.get(curFldID2);

                            emptyCellsBeforeFirstFld = 0;
                            emptyCellsAfterFirstFld = 0;
                            emptyCellsBeforeSecondFld = 0;
                            emptyCellsAfterSecondFld = 0;

                            if (FormatUtils.isInt(tmpFld.getEmptyCellsBeforeFld())) {
                                emptyCellsBeforeFirstFld = Integer.parseInt(tmpFld.getEmptyCellsBeforeFld());
                            }
                            if (FormatUtils.isInt(tmpFld.getEmptyCellsAfterFld())) {
                                emptyCellsAfterFirstFld = Integer.parseInt(tmpFld.getEmptyCellsAfterFld());
                            }
                            if (FormatUtils.isInt(tmpFld2.getEmptyCellsBeforeFld())) {
                                emptyCellsBeforeSecondFld = Integer.parseInt(tmpFld2.getEmptyCellsBeforeFld());
                            }
                            if (FormatUtils.isInt(tmpFld2.getEmptyCellsAfterFld())) {
                                emptyCellsAfterSecondFld = Integer.parseInt(tmpFld2.getEmptyCellsAfterFld());
                            }

                            if (emptyCellsBeforeFirstFld >= 2) {
                                emptyFldsCnt++;

                                // Put empty field in col. 1, first real field
                                // in col. 2, and second real field in col. 3.
                                fieldsMapFirstCol.put(
                                        new StringBuffer().append(szEmpty).
                                                append(emptyFldsCnt).toString(),
                                        emptyFormField);

                                fieldsMapSecondCol.put(tmpFld.getFieldId(), tmpFld);
                                fieldsMapThirdCol.put(tmpFld2.getFieldId(), tmpFld2);

                            }
                            else if (emptyCellsAfterFirstFld >= 2
                                    || emptyCellsBeforeSecondFld >= 2) {
                                emptyFldsCnt++;

                                // Put first real field in col. 1, empty field
                                // in col. 2, and second real field in col. 3.

                                fieldsMapFirstCol.put(tmpFld.getFieldId(), tmpFld);

                                fieldsMapSecondCol.put(
                                        new StringBuffer().append(szEmpty).
                                                append(emptyFldsCnt).toString(),
                                        emptyFormField);

                                fieldsMapThirdCol.put(tmpFld2.getFieldId(), tmpFld2);

                            }
                            else {

                                // Put first real field in col. 1 and second real field
                                // in col. 2.

                                fieldsMapFirstCol.put(tmpFld.getFieldId(), tmpFld);
                                fieldsMapSecondCol.put(tmpFld2.getFieldId(), tmpFld2);
                                if (emptyCellsAfterSecondFld >= 1) {
                                    // Put empty field in col. 3.
                                    fieldsMapThirdCol.put(
                                            new StringBuffer().append(szEmpty).
                                                    append(emptyFldsCnt).toString(),
                                            emptyFormField);

                                }
                            }
                        }
                    }

                }
            }
        }

        // DEBUGGING ONLY BELOW
//        debugItr = fieldsMapFirstCol.keySet().iterator();
//        int debugCnt = 0;
//        while (debugItr.hasNext()) {
//            debugCnt++;
//            mapKey = (String) debugItr.next();
//            lggr.log(Level.FINE, new StringBuffer().append("element ").
//                    append(debugCnt).append(" of fieldsMapFirstCol is ").
//                    append(mapKey).toString());
//
//        }
//        debugItr = fieldsMapSecondCol.keySet().iterator();
//        debugCnt = 0;
//        while (debugItr.hasNext()) {
//            debugCnt++;
//            mapKey = (String) debugItr.next();
//            lggr.log(Level.FINE, new StringBuffer().append("element ").
//                    append(debugCnt).append(" of fieldsMapSecondCol is ").
//                    append(mapKey).toString());
//
//        }
//        debugItr = fieldsMapThirdCol.keySet().iterator();
//        debugCnt = 0;
//        while (debugItr.hasNext()) {
//            debugCnt++;
//            mapKey = (String) debugItr.next();
//            lggr.log(Level.FINE, new StringBuffer().append("element ").
//                    append(debugCnt).append(" of fieldsMapThirdCol is ").
//                    append(mapKey).toString());
//
//        }
        // DEBUGGING ONLY ABOVE

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "populateFieldsMaps");
        }
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
