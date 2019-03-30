/**
 * 
 */
package com.vfi.demo.rest.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

/**
 * @author athi
 *
 */
@Entity(name="tag")
@Table(name="tag")
@Audited
public class Tag implements Serializable {
	@Id
	@GeneratedValue(generator="tag-gen")
	@GenericGenerator(name = "tag-gen", strategy ="increment")
    private Long id;
	private String tag;
	private String version;
	private String custom;
	private String abstractLevel;
	private String datatype;
	private String iord;
	private String crdr;
	private String tlabel;
	@Column(name = "doc", length = 400, nullable = true)
	private String doc;
	
    @OneToMany(mappedBy = "numTag")
    private List<Num> num = new ArrayList<Num>();
 
    @OneToMany(mappedBy = "preTag")
    private List<Pre> pre = new ArrayList<Pre>();
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public String getAbstractLevel() {
		return abstractLevel;
	}

	public void setAbstractLevel(String abstractLevel) {
		this.abstractLevel = abstractLevel;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getIord() {
		return iord;
	}

	public void setIord(String iord) {
		this.iord = iord;
	}

	public String getCrdr() {
		return crdr;
	}

	public void setCrdr(String crdr) {
		this.crdr = crdr;
	}

	public String getTlabel() {
		return tlabel;
	}

	public void setTlabel(String tlabel) {
		this.tlabel = tlabel;
	}

	public String getDoc() {
		return doc;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}

	public List<Num> getNum() {
		return num;
	}

	public void setNum(List<Num> num) {
		this.num = num;
	}

	public List<Pre> getPre() {
		return pre;
	}

	public void setPre(List<Pre> pre) {
		this.pre = pre;
	}

	public Tag() {
		super();
	}

	public Tag(String tag, String version, String custom, String abstractLevel, String datatype, String iord,
			String crdr, String tlabel, String doc) {
		super();
		this.tag = tag;
		this.version = version;
		this.custom = custom;
		this.abstractLevel = abstractLevel;
		this.datatype = datatype;
		this.iord = iord;
		this.crdr = crdr;
		this.tlabel = tlabel;
		this.doc = doc;
	}

	@Override
	public String toString() {
		return "Tag {id=" + id + ", tag=" + tag + ", version=" + version + ", custom=" + custom + ", abstractLevel="
				+ abstractLevel + ", datatype=" + datatype + ", iord=" + iord + ", crdr=" + crdr + ", tlabel=" + tlabel
				+ ", doc=" + doc + "{";
	}
}
