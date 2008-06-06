package org.talend.designer.components.persistent;

public class MemoryHelper {

    private static final Runtime S_RUNTIME = Runtime.getRuntime();

    public static long usedMemory() {
        return S_RUNTIME.totalMemory() - S_RUNTIME.freeMemory();
    }

    public static long freeMemory() {
        return S_RUNTIME.freeMemory();
    }

    public static long maxMemory() {
        return S_RUNTIME.maxMemory();
    }

    public static long totalMemory() {
        return S_RUNTIME.totalMemory();
    }

    public static boolean hasFreeMemory(float margin) {
        return usedMemory() < (1f - margin) * ((float) maxMemory());
    }

    public static void gc() {
        S_RUNTIME.gc();
        // long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
        // for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++i) {
        // S_RUNTIME.runFinalization();
        // S_RUNTIME.gc();
        // Thread.yield();
        // usedMem2 = usedMem1;
        // usedMem1 = usedMemory();
        // System.out.println("totalMemory =" +totalMemory());
        // System.out.println("maxMemory =" +maxMemory());
        // System.out.println("usedMem1 =" +usedMem1);
        // System.out.println("usedMem2 =" +usedMem2);
        // }

    }

}
