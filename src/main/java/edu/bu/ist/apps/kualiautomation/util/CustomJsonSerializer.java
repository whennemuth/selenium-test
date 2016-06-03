package edu.bu.ist.apps.kualiautomation.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Due to bi-directional references between entity classes, the json ObjectMapper gets stuck in a 
 * recursion loop when serializing them to json. This custom serializer will avoid this by only
 * serializing the id of the tournament field.
 * 
 * @author Warren
 *
 */
public class CustomJsonSerializer <T> {

	@SuppressWarnings("unchecked")
	public void serialize(
			T obj, 
			JsonGenerator generator, 
			SerializerProvider provider) throws IOException, JsonProcessingException {

		if(obj == null) {
			generator.writeObject(null);
			return;
		}
		else if(obj instanceof Collection) {
			serialize((Collection<T>) obj, generator, provider);
			return;
		}
		
		Integer id = getId(obj);
		if(id == null) {
			generator.writeNull();
		}
		else {
			generator.writeStartObject();
			generator.writeNumberField("id", id);
			generator.writeEndObject();
		}
	}

	public void serialize(
			Collection<T> collection, 
			JsonGenerator generator, 
			SerializerProvider provider) throws IOException, JsonProcessingException {

		if(collection == null) {
			generator.writeObject(null);
			return;
		}
		
		List<Integer> ids = new ArrayList<Integer>();
		for(T obj : collection) {
			ids.add(getId(obj));
		}
		generator.writeObject(ids);
	}

	private Integer getId(T obj) {		
		Integer id = null;
		try {
			id = (Integer) Utils.getAccessorValue(obj, "id");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
}
