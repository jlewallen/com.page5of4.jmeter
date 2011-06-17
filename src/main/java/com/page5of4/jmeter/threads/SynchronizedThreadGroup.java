package com.page5of4.jmeter.threads;

import org.apache.jmeter.threads.AbstractThreadGroup;
import org.apache.jmeter.threads.JMeterThread;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class SynchronizedThreadGroup extends AbstractThreadGroup {

   private static final Logger log = LoggingManager.getLoggerForClass();

   private static final long serialVersionUID = 1L;

   public SynchronizedThreadGroup() {
      super();
   }

   @Override
   public void scheduleThread(JMeterThread thread) {
      // Nothing to really do. We'll only ever schedule one of these...
   }

}
