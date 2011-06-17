package com.page5of4.jmeter.sampler;

import java.util.ArrayList;
import java.util.List;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestListener;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jolokia.client.J4pClient;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pReadResponse;
import org.jolokia.client.request.J4pSearchRequest;
import org.jolokia.client.request.J4pSearchResponse;

public class JolokiaSampler extends AbstractSampler implements TestListener {

   private static final Logger log = LoggingManager.getLoggerForClass();

   private static final long serialVersionUID = 232L;

   public static final String URL = "URL";

   public static final String QUERIES = "QUERIES";

   public String getUrl() {
      return getPropertyAsString(URL);
   }

   public void setUrl(String url) {
      setProperty(URL, url);
   }

   public JMeterProperty getQueries() {
      return getProperty(QUERIES);
   }

   public void setQueries(CollectionProperty queries) {
      setProperty(queries);
   }

   public List<JolokiaQuery> getQueriesAsJolokiaQueries() {
      ArrayList<JolokiaQuery> queries = new ArrayList<JolokiaQuery>();
      JMeterProperty queriesProperty = getQueries();
      if(queriesProperty instanceof CollectionProperty) {
         CollectionProperty collection = (CollectionProperty)queriesProperty;
         List<CollectionProperty> rows = (List<CollectionProperty>)collection.getObjectValue();
         for(CollectionProperty rowCollection : rows) {
            ArrayList<JMeterProperty> row = (ArrayList<JMeterProperty>)rowCollection.getObjectValue();
            queries.add(new JolokiaQuery(row.get(0).getStringValue(), row.get(1).getStringValue(), row.get(2).getStringValue()));
         }
      }
      return queries;
   }

   public SampleResult sample(Entry entry) {
      try {
         J4pClient j4pClient = new J4pClient(getUrl());
         List<String> names = new ArrayList<String>();
         List<J4pReadRequest> reads = new ArrayList<J4pReadRequest>();
         for(JolokiaQuery query : getQueriesAsJolokiaQueries()) {
            J4pSearchResponse response = j4pClient.execute(new J4pSearchRequest(query.getDomain() + ":" + query.getFilter()));
            for(String name : response.getMBeanNames()) {
               reads.add(new J4pReadRequest(name));
               names.add(name);
            }
         }

         List<J4pReadResponse> responses = j4pClient.execute(reads.toArray(new J4pReadRequest[0]));

         JolokiaSampleResult sample = new JolokiaSampleResult(responses);
         sample.setSampleLabel(getName());
         sample.sampleStart();
         sample.setSuccessful(true);
         sample.setResponseCodeOK();
         sample.setResponseMessageOK();
         sample.sampleEnd();
         return sample;
      }
      catch(Exception e) {
         throw new RuntimeException("Error sampling: ", e);
      }
   }

   public void testStarted(String arg0) {
      log.info("testStarted: " + arg0);
   }

   public void testStarted() {
      log.info("testStarted");
   }

   public void testIterationStart(LoopIterationEvent arg0) {
      log.info("testIterationStart: " + arg0);
   }

   public void testEnded() {
      log.info("testEnded");
   }

   public void testEnded(String arg0) {
      log.info("testEnded: " + arg0);
   }
}
