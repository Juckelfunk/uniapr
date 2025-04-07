// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import java.lang.management.MemoryUsage;
import java.util.Iterator;
import java.util.List;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import javax.management.NotificationFilter;
import javax.management.NotificationEmitter;
import java.lang.management.ManagementFactory;
import javax.management.NotificationListener;

public class MemoryWatchdog
{
    public static void addWatchDogToAllPools(final long threshold, final NotificationListener listener) {
        final MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        final NotificationEmitter ne = (NotificationEmitter)memBean;
        ne.addNotificationListener(listener, null, null);
        final List<MemoryPoolMXBean> memPools = ManagementFactory.getMemoryPoolMXBeans();
        for (final MemoryPoolMXBean mp : memPools) {
            if (mp.isUsageThresholdSupported()) {
                final MemoryUsage mu = mp.getUsage();
                final long max = mu.getMax();
                final long alert = max * threshold / 100L;
                mp.setUsageThreshold(alert);
            }
        }
    }
}
