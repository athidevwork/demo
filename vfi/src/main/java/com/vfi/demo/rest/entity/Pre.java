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
@Entity(name="pre")
@Table(name="pre")
@Audited
public class Pre {
	@Id
	@GeneratedValue(generator="pre-gen")
	@GenericGenerator(name = "pre-gen", strategy ="increment")
    private Long id;
	private String adsh;
	private String report;
	private String line;
	private String stmt;
	private String inpth;
	private String rfile;
	private String tag;
	private String version;
	private String plabel;
	private String negating;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_id", nullable = true)
    private Sub preSub;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(
            name = "tag_pre",
            referencedColumnName = "tag"),
        @JoinColumn(
            name = "version_pre",
            referencedColumnName = "version")
    })
    private Tag preTag;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(
                name = "adsh_num",
                referencedColumnName = "adsh"),
        @JoinColumn(
            name = "tag_num",
            referencedColumnName = "tag"),
        @JoinColumn(
            name = "version_num",
            referencedColumnName = "version")
    })
    private Tag preNum;*/
    
	public Pre(String adsh, String report, String line, String stmt, String inpth, String rfile, String tag,
			String version, String plabel, String negating) {
		super();
		this.adsh = adsh;
		this.report = report;
		this.line = line;
		this.stmt = stmt;
		this.inpth = inpth;
		this.rfile = rfile;
		this.tag = tag;
		this.version = version;
		this.plabel = plabel;
		this.negating = negating;
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

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getStmt() {
		return stmt;
	}

	public void setStmt(String stmt) {
		this.stmt = stmt;
	}

	public String getInpth() {
		return inpth;
	}

	public void setInpth(String inpth) {
		this.inpth = inpth;
	}

	public String getRfile() {
		return rfile;
	}

	public void setRfile(String rfile) {
		this.rfile = rfile;
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

	public String getPlabel() {
		return plabel;
	}

	public void setPlabel(String plabel) {
		this.plabel = plabel;
	}

	public String getNegating() {
		return negating;
	}

	public void setNegating(String negating) {
		this.negating = negating;
	}

	public Sub getPreSub() {
		return preSub;
	}

	public void setPreSub(Sub preSub) {
		this.preSub = preSub;
	}

	public Tag getPreTag() {
		return preTag;
	}

	public void setPreTag(Tag preTag) {
		this.preTag = preTag;
	}

	public Pre() {
		super();
	}

	@Override
	public String toString() {
		return "Pre {id=" + id + ", adsh=" + adsh + ", report=" + report + ", line=" + line + ", stmt=" + stmt
				+ ", inpth=" + inpth + ", rfile=" + rfile + ", tag=" + tag + ", version=" + version + ", plabel="
				+ plabel + ", negating=" + negating + "}";
	}
}
