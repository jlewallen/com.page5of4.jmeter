package com.page5of4.jmeter.sampler;

import java.util.Hashtable;
import java.util.Map;

import javax.management.ObjectName;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.jmeter.save.converters.ConversionHelp;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jolokia.client.request.J4pReadResponse;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Add to saveservice.properties: _com.page5of4.jmeter.sampler.JolokiaSampleResultConverter=collection
 * jolokiaSample=com.page5of4.jmeter.sampler.JolokiaSampleResult
 * 
 * @author jlewallen
 */
public class JolokiaSampleResultConverter extends AbstractCollectionConverter {

   private static final Logger log = LoggingManager.getLoggerForClass();

   private static final String RESPONSE_NODE_NAME = "response";

   private static final String DOMAIN_ATTRIBUTE_NAME = "domain";

   private static final String OBJECT_NAME_ATTRIBUTE_NAME = "domain";

   public JolokiaSampleResultConverter(Mapper mapper) {
      super(mapper);
   }

   @Override
   public boolean canConvert(@SuppressWarnings("rawtypes") Class klass) {
      return JolokiaSampleResult.class.equals(klass);
   }

   @Override
   public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
      String[] exclusions = new String[] { "Dynamic MBean Description" };
      JolokiaSampleResult sample = (JolokiaSampleResult)obj;
      setAttributes(writer, context, sample);
      for(J4pReadResponse response : sample.getResponses()) {
         ObjectName objectName = response.getRequest().getObjectName();
         writer.startNode(RESPONSE_NODE_NAME);
         setAttribute(writer, DOMAIN_ATTRIBUTE_NAME, objectName.getDomain());
         setAttribute(writer, OBJECT_NAME_ATTRIBUTE_NAME, objectName.getCanonicalName());
         for(Map.Entry<String, String> entry : objectName.getKeyPropertyList().entrySet()) {
            setAttribute(writer, entry.getKey(), entry.getValue());
         }
         for(String attributeName : response.getAttributes()) {
            if(!ArrayUtils.contains(exclusions, attributeName)) {
               writer.addAttribute(attributeName, response.getValue(attributeName).toString());
            }
         }
         writer.endNode();
      }
   }

   private void setAttribute(HierarchicalStreamWriter writer, String name, String value) {
      writer.addAttribute(name, value.replaceAll("\"", ""));
   }

   private void setAttributes(HierarchicalStreamWriter writer, MarshallingContext context, JolokiaSampleResult sample) {
      writer.addAttribute("ts", Long.toString(sample.getTimeStamp()));
   }

   @Override
   public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
      JolokiaSampleResult res = (JolokiaSampleResult)createCollection(context.getRequiredType());
      retrieveAttributes(reader, context, res);
      while(reader.hasMoreChildren()) {
         reader.moveDown();
         Object subItem = readItem(reader, context, res);
         retrieveResponses(reader, context, res);
         reader.moveUp();
      }
      return res;
   }

   private void retrieveResponses(HierarchicalStreamReader reader, UnmarshallingContext context, JolokiaSampleResult res) {

   }

   private void retrieveAttributes(HierarchicalStreamReader reader, UnmarshallingContext context, JolokiaSampleResult res) {
      try {
         String domain = ConversionHelp.decode(reader.getAttribute(DOMAIN_ATTRIBUTE_NAME));
         Hashtable<String, String> keys = new Hashtable<String, String>();
         for(String attributeName : (String[])IteratorUtils.toArray(reader.getAttributeNames(), String.class)) {
            String value = ConversionHelp.decode(reader.getAttribute(attributeName));
            keys.put(attributeName, value);
         }
         ObjectName name = new ObjectName(domain, keys);
      }
      catch(Exception e) {
         throw new RuntimeException("Error Parsing", e);
      }
   }
}
