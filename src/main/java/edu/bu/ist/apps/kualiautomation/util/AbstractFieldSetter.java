package edu.bu.ist.apps.kualiautomation.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

/**
 * When populating an entity as the result of any update or create ("C"r"U"d) service call, fields are 
 * set against the entity using this class to modify or skip certain values that are provided as 
 * indicated by the implementations of the abstract methods provided by this class.
 * 
 * @author whennemuth
 *
 */

public abstract class AbstractFieldSetter {
	
	private String currentUsername;
	protected EntityManager em;
	protected Object targetObj;	
	public static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("MMddyyyy");
	private static String defaultDatePattern = "\\d{8}";
	public static SimpleDateFormat defaultTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String defaultTimePattern = "\\d{4}\\-\\d{2}-\\d{2} \\d{2}\\:\\d{2}\\:\\d{2}";
	private static String defaultTimestampPattern = defaultTimePattern + "\\.\\d+";
	private Map<String, String> dateFormatMap = new HashMap<String, String>();	
	public static final String BAD_DATE_FORMAT = "Unsupported date format detected! Format must be MMDDYYYY and must parse to an actual date";
	public static final String BAD_TIMESTAMP_FORMAT = "Unsupported timestamp format detected! Format must be yyyy-MM-dd HH:mm:ss and must parse to an actual timestamp";
	public static final String BAD_INTEGER_FORMAT = "Unsupported number format detected! No non-digit characters allowed";

	public abstract Object getFieldValue(String fldName, Object ExistingValue) throws Exception;
	
	public abstract boolean ignoreField(String fldName);

	public AbstractFieldSetter(Object targetObj) {
		this.targetObj = targetObj;
	}

	public AbstractFieldSetter(EntityManager em, Object targetObj) {
		this.em = em;
		this.targetObj = targetObj;
	}
	
	public String getCurrentUsername() {
		return currentUsername;
	}

	public void setCurrentUsername(String currentUsername) {
		this.currentUsername = currentUsername;
	}

	/**
	 * The mutator indicated by fldName is to be set with fldValue. However if the parameter of the mutator
	 * is found to be an entity, assume fldValue is the primary key and obtain the entity itself that 
	 * matches that key. If match is found, return that entity, else return fldValue unsubstituted.
	 *  
	 * @param fldName
	 * @param fldValue
	 * @return
	 * @throws CRUDException 
	 */
	protected Object checkEntity(String fldName, Object fldValue) throws Exception {
		
		EntityInspector ei1 = new EntityInspector(targetObj);
		
		if(ei1.isEntity() && !Utils.isEmpty(fldValue)) {
			Class<?> entityParmType = ei1.getEntityParm(fldName);
			// The parameter of the setter is an entity, so first get the type of its @Id ( primary key) field.			
			if(entityParmType != null) {
				EntityInspector ei2 = new EntityInspector(entityParmType);
				if(ei2.hasPrimaryKey()) {
					Class<?> keyType = ei2.getPrimaryKeyType();
					// Next, return an instance of the entity based on fldValue cast to the type of the primary key field.
					if(String.class.isInstance(fldValue)) {
						fldValue = getObject(keyType, fldValue.toString(), fldName, dateFormatMap);
					}
					Object entity = em.find(entityParmType, fldValue);		
					return entity;
				}
				else {
					System.out.println("the field to set is an entity, but it has no primary key field. This might be a problem.");	
				}
			}
		}
		return fldValue;
	}	
	
	public <T> T convertStringValue(Class<T> parmType, String stringParm, String setterName, AbstractFieldSetter valueFilter) throws Exception {
		
		@SuppressWarnings("unchecked")
		T fv = (T) valueFilter.getFieldValue(setterName, stringParm);
		
		if(fv == null) {
			return null;
		}
		else if(String.class.isInstance(fv)) {
			fv = getObject(parmType, String.valueOf(fv), setterName, valueFilter.getDateFormatMap());
		}
		
		return fv;
	}
	
	public static <T> T getObject(Class<T> type, String formVal, String setterName) throws Exception {
		return getObject(type, formVal, setterName, new HashMap<String, String>());
	}

	/**
	 * Given a type T and a string value, convert the string value into an object of type T.
	 * 
	 * @param parmType A string obtained by having called Object.getClass().getName()
	 * @param sValue A string representation of a value to set for a field.
	 * @param setterName The name of the setter method of a field.
	 * @param customDateFormatsMap A map to check before converting sValue to a date following the default date format. If setterName
	 * can be found as a key in this map, then the corresponding value is the date format to use in the date conversion. 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getObject(Class<T> parmType, String sValue, String setterName, Map<String, String> customDateFormatsMap) throws Exception {
		
		Object value = null;
		String sType = parmType.getSimpleName().toLowerCase();
		
		switch(sType) {
		case "string":
			value = sValue;
			break;
		case "date":
			try {				
				String customDateFormatStr = customDateFormatsMap.get(Utils.getFieldName(setterName));
				if(customDateFormatStr == null) {
					if(sValue.matches(defaultDatePattern)) {
						value = defaultDateFormat.parse(sValue);
					}
					else if(sValue.matches(defaultTimePattern)) {
						value = defaultTimeFormat.parse(sValue);
					}
					else if(sValue.matches(defaultTimestampPattern)) {
						value = defaultTimeFormat.parse(sValue.substring(0, defaultTimeFormat.toPattern().length()));
					}
					else {
						throw new ParseException("Invalid date " + sValue, 0);
					}
				}
				else {
					value = new SimpleDateFormat(customDateFormatStr).parse(sValue);
				}
			} 
			catch (ParseException e) {
				throw new Exception(BAD_DATE_FORMAT + " " + setterName + " = " + sValue);
			}
			break;
		case "timestamp":
			try {
				String customTimeFormatStr = customDateFormatsMap.get(Utils.getFieldName(setterName));
				if(customTimeFormatStr == null) {
					if(sValue.matches(defaultTimePattern)) {
						value = new Timestamp(defaultTimeFormat.parse(sValue).getTime());
					}
					else if(sValue.matches(defaultTimestampPattern)) {
						value = defaultTimeFormat.parse(sValue.substring(0, defaultTimeFormat.toPattern().length()));
					}
					else if(sValue.matches(defaultDatePattern)) {
						value = new Timestamp(defaultDateFormat.parse(sValue).getTime());
					}
					else {
						throw new ParseException("Invalid time " + sValue, 0);
					}
				}
				else {
					value = new Timestamp(new SimpleDateFormat(customTimeFormatStr).parse(sValue).getTime());
				}
			} 
			catch (ParseException e) {
				throw new Exception(BAD_TIMESTAMP_FORMAT + " " + setterName + " = " + sValue);
			}
			break;
		case "integer": case "int":
			try {
				if(sValue.matches("\\d+")) {
					value = Integer.parseInt(sValue);
				}
				else {
					throw new ParseException("Invalid number " + sValue, 0);
				}
			} 
			catch (ParseException e) {
				throw new Exception(BAD_INTEGER_FORMAT + " " + setterName + " = " + sValue);
			}
			break;
		case "byte":
			boolean isTrue = sValue.matches("(?i)(1)|(true)|(y)|(yes)|(on)");
			value = new Byte((byte) (isTrue ? 1 : 0));
			break;
		default:
			System.out.println(sType);
			break;
		}	
		
		return (T) value;
	}
	
	public void addDateFormat(String fldName, String dateFormat) {
		dateFormatMap.put(fldName, dateFormat);
	}
	
	public Map<String, String> getDateFormatMap() {
		return dateFormatMap;
	}

	public void setDateFormatMap(Map<String, String> dateFormatMap) {
		this.dateFormatMap = dateFormatMap;
	}

	@SuppressWarnings("unused")
	private boolean isEntity(Object o) {
		return (new EntityInspector(o)).isEntity();
	}
	
	public EntityManager getEntityManager() {
		return em;
	}
}
