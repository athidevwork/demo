/**
 * 
 */
package com.vfi.demo.rest.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

/**
 * @author athi
 *
 */
@Entity(name="num")
@Table(name="num")
@Audited
public class Num {
	@Id
	@GeneratedValue(generator="num-gen")
	@GenericGenerator(name = "num-gen", strategy ="increment")
    private Long id;
	private String adsh;
	private String tag;
	private String version;
	private String coreg;
	private String ddate;
	private String qtrs;
	private String uom;
	private String value;
	//private String footnote;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "num_id", nullable = true)
    private Sub numSub;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(
            name = "tag_id",
            referencedColumnName = "tag"),
        @JoinColumn(
            name = "version_id",
            referencedColumnName = "version")
    })
    private Tag numTag;
    
    /*@OneToMany(mappedBy = "preNum")
    private List<Pre> num = new ArrayList<Pre>();*/
    
	public Num(String adsh, String tag, String version, String coreg, String ddate, String qtrs, String uom,
			String value) {
		super();
		this.adsh = adsh;
		this.tag = tag;
		this.version = version;
		this.coreg = coreg;
		this.ddate = ddate;
		this.qtrs = qtrs;
		this.uom = uom;
		this.value = value;
		//this.footnote = footnote;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAdsh() {
		return adsh;
	}

	public void setAdsh(String adsh) {
		this.adsh = adsh;
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

	public String getCoreg() {
		return coreg;
	}

	public void setCoreg(String coreg) {
		this.coreg = coreg;
	}

	public String getDdate() {
		return ddate;
	}

	public void setDdate(String ddate) {
		this.ddate = ddate;
	}

	public String getQtrs() {
		return qtrs;
	}

	public void setQtrs(String qtrs) {
		this.qtrs = qtrs;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Sub getNumSub() {
		return numSub;
	}

	public void setNumSub(Sub numSub) {
		this.numSub = numSub;
	}

	public Tag getNumTag() {
		return numTag;
	}

	public void setNumTag(Tag numTag) {
		this.numTag = numTag;
	}

	public Num() {
		super();
	}

	@Override
	public String toString() {
		return "Num {id=" + id + ", adsh=" + adsh + ", tag=" + tag + ", version=" + version + ", coreg=" + coreg
				+ ", ddate=" + ddate + ", qtrs=" + qtrs + ", uom=" + uom + ", value=" + value //+ ", footnote=" + footnote
				+ "}";
	}
}
