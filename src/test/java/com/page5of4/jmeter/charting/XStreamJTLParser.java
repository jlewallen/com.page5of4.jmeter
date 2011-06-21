package com.page5of4.jmeter.charting;

import java.io.File;
import java.io.FileInputStream;

import org.apache.jmeter.reporters.ResultCollectorHelper;
import org.apache.jmeter.save.SaveService;

public class XStreamJTLParser {
   private final File f;
   private final ResultCollectorHelper rch;

   public XStreamJTLParser(File f, ResultCollectorHelper rch) {
      this.f = f;
      this.rch = rch;
   }

   public void parse() throws Exception {
      FileInputStream fis = null;
      try {
         fis = new FileInputStream(f);
         SaveService.loadTestResults(fis, rch);
      }
      finally {
         if(fis != null) {
            fis.close();
         }
      }
   }

}
