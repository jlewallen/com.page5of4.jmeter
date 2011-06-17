package com.page5of4.jmeter.control;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.NextIsNullException;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class UntilNumberOfThreadsController extends LoopController {

   private static final long serialVersionUID = 1L;

   private static final Logger log = LoggingManager.getLoggerForClass();

   @Override
   protected Sampler nextIsNull() throws NextIsNullException {
      reInitialize();
      if(endOfLoop(true)) {
         setDone(true);
         return null;
      }
      return next();
   }

   /**
    * This skips controller entirely if the condition is false on first entry.
    * <p>
    * {@inheritDoc}
    */
   @Override
   public Sampler next() {
      if(isFirst()) {
         if(endOfLoop(false)) {
            setDone(true);
            return null;
         }
      }
      return super.next();
   }

   private boolean endOfLoop(boolean loopEnd) {
      int numberOfThreads = JMeterContextService.getNumberOfThreads();
      log.info("Number of Threads: " + numberOfThreads);
      return numberOfThreads == 1;
   }

}
