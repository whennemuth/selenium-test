package edu.bu.ist.apps.kualiautomation.services.automate.locate;

public abstract class ComparableLabel implements Comparable<ComparableLabel> {

	private String label;
	private String basis;
	private String text;
	private boolean useDefaultMethodIfIndeterminate;
	private static final int THIS_LABEL_IS_BETTER = -1;
	private static final int OTHER_LABEL_IS_BETTER = 1;
	private static final String BASIS_NOT_SHARED = "Comparison occurring between two "
			+ "ComparableLabel instances that do not share the same basis value!";
	
	/**
	 * Constructor for Comparable Label
	 * 
	 * @param label The value the web element this label is based on was located by.
	 * @param text The actual text of the web element this label is based on.
	 * @param basis The value labels are "competing" to be most like when they are compare with one another.
	 * @param useDefaultMethodIfIndeterminate Invoke the default comparison method if implementors of this class 
	 * find two unequal labels to compare as zero using their custom compare method.
	 */
	public ComparableLabel(String label, String text, String basis, boolean useDefaultMethodIfIndeterminate) {
		this.label = label;
		this.text = text;
		this.basis = basis;
		this.useDefaultMethodIfIndeterminate = useDefaultMethodIfIndeterminate;
	}
	
	protected abstract int customCompareTo(ComparableLabel lbl);
	
	protected int requiredCompareTo(ComparableLabel lbl) {
		if (this == lbl)
			return 0;		
		if (lbl == null)
			return THIS_LABEL_IS_BETTER;
		if (!(lbl instanceof ComparableLabel))
			return THIS_LABEL_IS_BETTER;
		ComparableLabel other = (ComparableLabel) lbl;
		if (basis == null) {
			if (other.basis != null)
				throw new IllegalStateException(BASIS_NOT_SHARED);
		} 
		else if (!basis.equals(other.basis)) {
			throw new IllegalStateException(BASIS_NOT_SHARED);
		}
		if (label == null) {
			if (other.label != null)
				return OTHER_LABEL_IS_BETTER;
		} 
		else if (other.label == null) {
			return THIS_LABEL_IS_BETTER;
		}
		
		return 0;
	}
	
	protected int defaultCompareTo(ComparableLabel lbl) {
		String thislabel = getCleanedValue(label).toLowerCase();
		String thistext = getCleanedValue(text).toLowerCase();
		String otherlabel = getCleanedValue(lbl.label).toLowerCase();
		String othertext = getCleanedValue(lbl.text).toLowerCase();
		
		if(thistext.startsWith(thislabel) && othertext.startsWith(otherlabel)) {			
			if(thistext.length() < othertext.length())
				return THIS_LABEL_IS_BETTER;
			if(othertext.length() < thistext.length())
				return OTHER_LABEL_IS_BETTER;
		}
		else if(thistext.startsWith(thislabel)) {
			return THIS_LABEL_IS_BETTER;
		}
		else if(othertext.startsWith(otherlabel)) {
			return OTHER_LABEL_IS_BETTER;
		}
		else {
			if(thistext.contains(thislabel) && othertext.contains(otherlabel)) { 
				thistext = trimLeftNonAlphaNumeric(thistext, thislabel);
				othertext = trimLeftNonAlphaNumeric(othertext, otherlabel);
				ComparableLabel newThis = new ComparableLabel(thislabel, thistext, basis, true) {
					@Override protected int customCompareTo(ComparableLabel lbl) {
						return 0;
					}
				};
				ComparableLabel newOther = new ComparableLabel(otherlabel, othertext, basis, true) {
					@Override protected int customCompareTo(ComparableLabel lbl) {
						return 0;
					}
				};
// RESUME NEXT: test this.				
				return newThis.compareTo(newOther);
			}
			else if(thistext.contains(thislabel)) {
				return THIS_LABEL_IS_BETTER;
			}
			else if(othertext.contains(thistext)) {
				return OTHER_LABEL_IS_BETTER;
			}
		}

		return 0;
	}

	/**
	 * Returns a negative integer, zero, or a positive integer as this object is less than, equal to, 
	 * or greater than the specified object. 
	 */
	@Override
	public int compareTo(ComparableLabel o) {
		int compared = requiredCompareTo(o);
		if(compared == 0) {
			compared = customCompareTo(o);
			if(compared == 0 && useDefaultMethodIfIndeterminate) {
				compared = defaultCompareTo(o);
			}
		}
		return compared;
	}
	
	/**
	 * Clean out all parts of a string that are not to be considered during a search to determine if an elements text
	 * is a match for the search text.
	 * 
	 * @param s
	 * @return
	 */
	public static String getCleanedValue(String s) {
		if(s == null)
			return "";
		// Normalize the spaces (trim from edges, and replace multiple contiguous whitespace with single space character)
		String cleaned = s.trim().replaceAll("\\s+", " ");
		// Trim off any sequence of colons and whitespace from the end of the text.
		cleaned = cleaned.replaceAll("(\\s*:\\s*)*$", "");
		return cleaned;
	}
	
	public static String trimLeftNonAlphaNumeric(String s, String contained) {
		int idx = s.indexOf(contained);
		String start = s.substring(0, idx);
		if(start.matches("[^a-zA-Z\\d]*")) {
			s = s.substring(idx);
		}
		return s;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
