package edu.bu.ist.apps.kualiautomation.entity;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.bu.ist.apps.kualiautomation.entity.ConfigEnvironment.ConfigFieldSerializer;
import edu.bu.ist.apps.kualiautomation.util.Utils;

import java.util.Arrays;
import java.util.Date;


/**
 * The persistent class for the config_shortcut database table.
 * 
 */
@Entity
@Table(name="config_shortcut")
@NamedQuery(name="ConfigShortcut.findAll", query="SELECT c FROM ConfigShortcut c")
public class ConfigShortcut implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date", nullable=false)
	private Date createdDate;

	@Column(length=255)
	private String description;

	@Column(name="element_type", nullable=false, length=45)
	private String elementType;

	@Column(nullable=false, length=45)
	private String identifier;

	@Column(nullable=false)
	private byte include;

	@Column(name="label_hierarchy", length=255)
	private String labelHierarchy;

	@Column(unique=true, nullable=false, length=45)
	private String name;

	@Column(nullable=false)
	private int sequence;

	//bi-directional many-to-one association to Config
	@ManyToOne
	@JoinColumn(name="config_id", nullable=false)
	private Config config;
	
	/**
	 * JPA lifecycle callback methods
	 */
	@PrePersist
	public void prePersist() {
		createdDate = new Date(System.currentTimeMillis());
	}

	public ConfigShortcut() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getElementType() {
		return this.elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public byte getInclude() {
		return this.include;
	}

	public void setInclude(byte include) {
		this.include = include;
	}

	public String getLabelHierarchy() {
		return this.labelHierarchy;
	}

	public void setLabelHierarchy(String labelHierarchy) {
		this.labelHierarchy = labelHierarchy;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	@JsonSerialize(using=ConfigFieldSerializer.class)
	public Config getConfig() {
		return this.config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * This is a hack. I'm adding this property to be included into the json object created
	 * from an instance of this class for convenience in angularjs 2-way binding UI manipulation.
	 */
	@Transient
	private boolean checked;
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	

	public static final String LABEL_HIERARCHY_SEPARATOR = " >>> ";
	public String getSeparator() {
		return LABEL_HIERARCHY_SEPARATOR;
	}
	@Transient
	public LabelHierarchy getLabelHierarchyObject() {
		String h = getLabelHierarchy();
		if(Utils.isEmpty(h) || h.trim().isEmpty() || h.trim().equals(LABEL_HIERARCHY_SEPARATOR.trim())) {
			return null;
		}
		String[] labels = h.trim().split(LABEL_HIERARCHY_SEPARATOR.trim());
		return getLabelHierarchyObject(null, labels);
	}
	private LabelHierarchy getLabelHierarchyObject(LabelHierarchy parent, String[] labels) {
		if(labels == null || labels.length == 0)
			return null;
		if(parent == null) {
			parent = new LabelHierarchy(labels[0].trim());
			if(labels.length == 1) {
				return parent;
			}
			return getLabelHierarchyObject(parent, Arrays.copyOfRange(labels, 1, labels.length));
		}
		if(labels.length == 1) {
			LabelHierarchy child = new LabelHierarchy(labels[0].trim());
			parent.setChildHierachy(child);
		}
		else {
			LabelHierarchy child = new LabelHierarchy(labels[0].trim());
			String[] childLabels = Arrays.copyOfRange(labels, 1, labels.length);
			parent.setChildHierachy(getLabelHierarchyObject(child, childLabels));
		}			
		return parent;
	}
	
	public void setLabelHierarchyObject(LabelHierarchy hierarchy) {
		StringBuilder s = buildLabelHierarchy(null, hierarchy);
		if(s != null && !s.toString().trim().isEmpty())
			labelHierarchy = s.toString();
	}
	
	private StringBuilder buildLabelHierarchy(StringBuilder s, LabelHierarchy hierarchy) {
		if(s == null)
			s = new StringBuilder();
		if(hierarchy == null)
			return s;
		if(hierarchy.getLabel() == null || hierarchy.getLabel().trim().isEmpty())
			return buildLabelHierarchy(s, hierarchy.getChildHierachy());
		if(!s.toString().isEmpty()) {
			s.append(LABEL_HIERARCHY_SEPARATOR);
		}
		s.append(hierarchy.getLabel().trim());
		return buildLabelHierarchy(s, hierarchy.getChildHierachy());
	}

	public static class LabelHierarchy {
		private String label;
		private LabelHierarchy childHierachy;
		public LabelHierarchy() { }
		public LabelHierarchy(String label) {
			this.label = label;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public LabelHierarchy getChildHierachy() {
			return childHierachy;
		}
		public void setChildHierachy(LabelHierarchy childHierachy) {
			this.childHierachy = childHierachy;
		}
	}
}