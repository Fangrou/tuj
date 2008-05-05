// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006 Talend Inc. - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================

package org.talend.designer.components.persistent;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.talend.designer.components.commons.AdvancedLookup.MATCHING_MODE;

import routines.system.IPersistableComparableLookupRow;
import routines.system.IPersistableLookupRow;

/**
 * DOC amaumont class global comment. Detailled comment <br/>
 * 
 * 
 */
public class PersistentSortedLookupManager<B extends IPersistableComparableLookupRow<B>> extends AbstractPersistentLookup<B>
        implements IPersistentLookupManager<B> {

    private static final float MARGIN_MAX = 0.35f;

    private String container;

    private MATCHING_MODE matchingMode;

    //
    private ILookupManagerUnit<B>[] lookupList;

    private int bufferSize = 10000000;
//    private int bufferSize = 100;
//    private int bufferSize = 20;

    // private int bufferSize = 100;
    // private int bufferSize = 3;

    private IPersistableLookupRow<B>[] buffer = null;

    private int fileIndex = 0;

    // ////////////////////////////////////////
    private int bufferBeanIndex = 0;

    private int lookupIndex = 0;

    ILookupManagerUnit<B> currLookup;

    private int lookupListSize;

    private boolean waitingNext;

    private B lookupKey;

    private boolean noMoreNext;

    private B previousResult;

    private boolean nextIsPreviousResult;

    private IRowCreator<B> rowCreator;

    private boolean lookupKeyIsInitialized;

    private boolean previousResultRetrieved;

    private int bufferMarkLimit = -1;

    private boolean bufferIsMarked;

    private boolean firstUnsifficientMemory = true;

    private boolean waitingHeapException;

    public PersistentSortedLookupManager(MATCHING_MODE matchingMode, String container, IRowCreator<B> rowCreator) {
        this.matchingMode = matchingMode;
        this.container = container;
        this.rowCreator = rowCreator;
        this.lookupKey = rowCreator.createRowInstance();
    }

    public PersistentSortedLookupManager(MATCHING_MODE matchingMode, String container, IRowCreator<B> rowCreator, int bufferSize) {
        this(matchingMode, container, rowCreator);
        this.bufferSize = bufferSize;
    }
    
    
    public void initPut() throws IOException {
        buffer = new IPersistableLookupRow[bufferSize];
        bufferBeanIndex = 0;
    }

    public void put(B bean) throws IOException {

        if (!MemoryHelper.hasFreeMemory(MARGIN_MAX)) {
            if (!bufferIsMarked) {
                if (firstUnsifficientMemory) {
                    firstUnsifficientMemory = false;
                    MemoryHelper.gc();
                    if (bufferBeanIndex == 0) {
                        waitingHeapException = true;
                    }
                }
                if (!waitingHeapException && !MemoryHelper.hasFreeMemory(MARGIN_MAX)) {
                    float _10P = ((float) bufferSize) * 0.1f;
                    if ((float) bufferBeanIndex >= _10P) {
                        bufferMarkLimit = bufferBeanIndex;
                    } else {
                        bufferMarkLimit = (int) _10P;
                    }
                    System.out.println("Buffer marked at index (1-Lookup) " + bufferMarkLimit);
                    bufferIsMarked = true;
                }
            }
        }

        if (bufferBeanIndex == bufferSize || bufferIsMarked && bufferBeanIndex == bufferMarkLimit) {
            writeBuffer();
            if (!bufferIsMarked) {
                bufferMarkLimit = bufferBeanIndex;
//                System.out.println("Buffer marked at index (2-Lookup) " + bufferMarkLimit);
                bufferIsMarked = true;
            }
            bufferBeanIndex = 0;
        }
        buffer[bufferBeanIndex++] = bean;
    }

    public void endPut() throws IOException {

        if (bufferBeanIndex > 0) {
            writeBuffer();
        }

        // Arrays.fill(buffer, null);

        buffer = null;

        MemoryHelper.gc();

    }

    private void writeBuffer() throws FileNotFoundException, IOException {
        Arrays.sort(buffer, 0, bufferBeanIndex);
        File keysDataFile = new File(buildKeysFilePath(fileIndex));
        File valuesDataFile = new File(buildValuesFilePath(fileIndex));
        DataOutputStream keysDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(keysDataFile)));
        DataOutputStream valuesDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
                valuesDataFile)));

        // System.out.println("Writing LOOKUP buffer " + fileIndex + "... ");

        for (int i = 0; i < bufferBeanIndex; i++) {

            IPersistableLookupRow<B> curBean = buffer[i];

            int writtenValuesDataSize = curBean.toValuesData(valuesDataOutputStream);
            curBean.toKeysData(keysDataOutputStream);
            keysDataOutputStream.writeInt(writtenValuesDataSize);

            // System.out.println(curBean);
        }
        // System.out.println("Write ended LOOKUP buffer " + fileIndex);
        keysDataOutputStream.close();
        valuesDataOutputStream.close();
        fileIndex++;
    }

    private String buildValuesFilePath(int i) {
        return container + "ValuesData_" + i + ".bin";
    }

    private String buildKeysFilePath(int i) {
        return container + "KeysData_" + i + ".bin";
    }

    public void initGet() throws IOException {
        lookupList = (ILookupManagerUnit<B>[]) new ILookupManagerUnit[fileIndex];
        for (int i = 0; i < fileIndex; i++) {
            RowProvider<B> rowProvider = new RowProvider<B>(rowCreator);
            lookupList[i] = getOrderedBeanLoohupInstance(buildKeysFilePath(i), buildValuesFilePath(i), i, rowProvider,
                    this.matchingMode);
        }
        lookupListSize = lookupList.length;
    }

    private ILookupManagerUnit<B> getOrderedBeanLoohupInstance(String keysFilePath, String valuesFilePath, int i,
            RowProvider<B> rowProvider, MATCHING_MODE keysManagement) throws IOException {
        switch (keysManagement) {
        case FIRST_MATCH:
            return new OrderedBeanLookupMatchFirst<B>(keysFilePath, valuesFilePath, i, rowProvider);

        case LAST_MATCH:
        case UNIQUE_MATCH:

            return new OrderedBeanLookupMatchLast<B>(keysFilePath, valuesFilePath, i, rowProvider);

        case ALL_MATCHES:

            return new OrderedBeanLookupMatchAll<B>(keysFilePath, valuesFilePath, i, rowProvider);

        case ALL_ROWS:

            return new OrderedBeanLookupAll<B>(valuesFilePath);

        default:
            throw new IllegalArgumentException();
        }
    }

    public void lookup(B key) throws IOException {

        waitingNext = false;
        if (matchingMode == MATCHING_MODE.ALL_MATCHES) {
            lookupIndex = 0;
            for (int lookupIndexLocal = 0; lookupIndexLocal < lookupListSize; lookupIndexLocal++) {
                ILookupManagerUnit<B> tempLookup = lookupList[lookupIndexLocal];
                tempLookup.lookup(key);
            }
        } else {
            try {
                if (lookupKey.compareTo(key) == 0 && previousResultRetrieved) {
                    nextIsPreviousResult = true;
                } else {
                    previousResult = null;
                }
            } catch (NullPointerException e) {
                previousResult = null;
            }
            noMoreNext = false;
        }
        key.copyDataTo(lookupKey);
        lookupKeyIsInitialized = true;
    }

    public boolean hasNext() throws IOException {

        if (waitingNext || nextIsPreviousResult) {
            return true;
        }

        if (!lookupKeyIsInitialized || noMoreNext) {
            return false;
        }

        if (matchingMode == MATCHING_MODE.LAST_MATCH || matchingMode == MATCHING_MODE.UNIQUE_MATCH) {
            for (int lookupIndexLocal = lookupListSize - 1; lookupIndexLocal >= 0; lookupIndexLocal--) {
                ILookupManagerUnit<B> tempLookup = lookupList[lookupIndexLocal];
                // System.out.println("########################################");
                // System.out.println(lookupKey);
                // System.out.println("lookupIndexLocal=" + lookupIndexLocal);
                tempLookup.lookup(lookupKey);
                if (tempLookup.hasNext()) {
                    // System.out.println("Found in " + lookupIndexLocal);
                    currLookup = tempLookup;
                    waitingNext = true;
                    noMoreNext = true;
                    previousResultRetrieved = false;
                    return true;
                }
            }
            noMoreNext = true;
            return false;

        } else if (matchingMode == MATCHING_MODE.ALL_MATCHES) {
            for (; lookupIndex < lookupListSize; lookupIndex++) {
                ILookupManagerUnit<B> tempLookup = lookupList[lookupIndex];
                if (tempLookup.hasNext()) {
                    currLookup = tempLookup;
                    waitingNext = true;
                    return true;
                }
            }
            return false;

        } else if (matchingMode == MATCHING_MODE.FIRST_MATCH) {
            for (int lookupIndexLocal = 0; lookupIndexLocal < lookupListSize; lookupIndexLocal++) {
                ILookupManagerUnit<B> tempLookup = lookupList[lookupIndexLocal];
                tempLookup.lookup(lookupKey);
                if (tempLookup.hasNext()) {
                    currLookup = tempLookup;
                    waitingNext = true;
                    noMoreNext = true;
                    previousResultRetrieved = false;
                    return true;
                }
            }
            noMoreNext = true;
            return false;

        } else {

            if (currLookup.hasNext()) {
                waitingNext = true;
                return true;
            }
            lookupIndex++;
            return false;

        }

    }

    public B next() throws IOException {

        if (nextIsPreviousResult) {
            nextIsPreviousResult = false;
            noMoreNext = true;
            return previousResult;
        }

        if (waitingNext) {
            waitingNext = false;
            previousResult = currLookup.next();

            if (matchingMode == MATCHING_MODE.LAST_MATCH || matchingMode == MATCHING_MODE.FIRST_MATCH) {
                previousResultRetrieved = true;
                noMoreNext = true;
            }

            return previousResult;
        } else {
            throw new NoSuchElementException();
        }
    }

    public void endGet() throws IOException {
        for (ILookupManagerUnit<B> orderedBeanLookup : lookupList) {
            orderedBeanLookup.close();
        }
        clear();
        lookupList = null;
    }

    public void clear() throws IOException {
        for (int i = 0; i < fileIndex; i++) {
            (new File(buildKeysFilePath(i))).delete();
            (new File(buildValuesFilePath(i))).delete();
        }
    }

    public B getNextFreeRow() {
        if (buffer.length > 0 && bufferBeanIndex != buffer.length) {
            B nextBean = (B) buffer[bufferBeanIndex];
            if (nextBean == null) {
                return this.rowCreator.createRowInstance();
            } else {
                return nextBean;
            }
        } else {
            return this.rowCreator.createRowInstance();
        }
    }

}
