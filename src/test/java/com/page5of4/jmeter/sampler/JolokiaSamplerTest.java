package com.page5of4.jmeter.sampler;

import static org.fest.assertions.Assertions.assertThat;

import javax.management.MalformedObjectNameException;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.jolokia.client.exception.J4pException;
import org.junit.Test;

public class JolokiaSamplerTest {

   @Test
   public void test_querying_diagnostics() throws MalformedObjectNameException, J4pException {
      CollectionProperty properties = JolokiaQuery.build(JolokiaQuery.build("java.lang", "type=OperatingSystem"));
      JolokiaSampler sampler = new JolokiaSampler();
      sampler.setUrl("http://127.0.0.1:8080/jolokia/");
      sampler.setUrl("http://192.168.0.133:7080/jolokia/");
      sampler.setQueries(properties);
      JolokiaSampleResult sample = (JolokiaSampleResult)sampler.sample(new Entry());
      assertThat(sample).isNotNull();
      assertThat(sample.getResponses()).isNotEmpty();
   }
}
