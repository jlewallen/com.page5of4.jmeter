package com.page5of4.jmeter.sampler;

import java.io.Serializable;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.StringProperty;

public class JolokiaQuery extends Argument implements Serializable {

   private static final long serialVersionUID = 1L;

   public static final String DOMAIN = "JolokiaQueryArgument.domain";
   public static final String FILTER = "JolokiaQueryArgument.filter";
   public static final String QUERY = "JolokiaQueryArgument.query";

   public JolokiaQuery(String domain, String filter, String query) {
      super();
      setDomain(domain);
      setFilter(filter);
      setQuery(query);
   }

   public JolokiaQuery(String query) {
      super();
      setQuery(query);
   }

   public JolokiaQuery(String domain, String filter) {
      super();
      setDomain(domain);
      setFilter(filter);
   }

   public String getDomain() {
      return getPropertyAsString(DOMAIN);
   }

   public void setDomain(String domain) {
      setProperty(DOMAIN, domain);
   }

   public String getFilter() {
      return getPropertyAsString(FILTER);
   }

   public void setFilter(String filter) {
      setProperty(FILTER, filter);
   }

   public String getQuery() {
      return getPropertyAsString(QUERY);
   }

   public void setQuery(String query) {
      setProperty(QUERY, query);
   }

   @Override
   public String toString() {
      return "JolokiaQuery<" + getDomain() + ":" + getFilter() + " or '" + getQuery() + "'>";
   }

   public static CollectionProperty build(String domain, String filter) {
      CollectionProperty row = new CollectionProperty();
      row.addProperty(new StringProperty(JolokiaQuery.DOMAIN, domain));
      row.addProperty(new StringProperty(JolokiaQuery.FILTER, filter));
      row.addProperty(new StringProperty(JolokiaQuery.QUERY, ""));
      return row;
   }

   public static CollectionProperty build(CollectionProperty... properties) {
      CollectionProperty queries = new CollectionProperty();
      queries.setName(JolokiaSampler.QUERIES);
      for(CollectionProperty property : properties) {
         queries.addProperty(property);
      }
      return queries;
   }
}
