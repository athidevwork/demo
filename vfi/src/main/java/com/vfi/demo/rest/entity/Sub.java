/**
 * 
 */
package com.vfi.demo.rest.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * @author athi
 *
 */
@Entity(name="sub")
@Table(name="sub")
@Audited
public class Sub {
	@Id
	@GeneratedValue
    private Long id;
    private String adsh;
    private String cik;
    private String name;
    private String sic;
    private String countryba;
    private String stprba;
    private String cityba;
    private String zipba;
    private String bas1;
    private String bas2;
    private String baph;
    private String countryma;
    private String stprma;
    private String cityma;
    private String zipma;
    private String mas1;
    private String mas2;
    private String countryinc;
    private String stprinc;
    private String ein;
    private String former;
    private String changed;
    private String afs;
    private String wksi;
    private String fye;
    private String form;
    private String period;
    private String fy;
    private String fp;
    private String filed;
    private String accepted;
    private String prevrpt;
    private String detail;
    private String instance;
    private String nciks;
    private String aciks;
    
    @OneToMany(mappedBy = "numSub")
    private List<Num> num = new ArrayList<Num>();

    @OneToMany(mappedBy = "preSub")
    private List<Pre> pre = new ArrayList<Pre>();
    
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

	public String getCik() {
		return cik;
	}

	public void setCik(String cik) {
		this.cik = cik;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSic() {
		return sic;
	}

	public void setSic(String sic) {
		this.sic = sic;
	}

	public String getCountryba() {
		return countryba;
	}

	public void setCountryba(String countryba) {
		this.countryba = countryba;
	}

	public String getStprba() {
		return stprba;
	}

	public void setStprba(String stprba) {
		this.stprba = stprba;
	}

	public String getCityba() {
		return cityba;
	}

	public void setCityba(String cityba) {
		this.cityba = cityba;
	}

	public String getZipba() {
		return zipba;
	}

	public void setZipba(String zipba) {
		this.zipba = zipba;
	}

	public String getBas1() {
		return bas1;
	}

	public void setBas1(String bas1) {
		this.bas1 = bas1;
	}

	public String getBas2() {
		return bas2;
	}

	public void setBas2(String bas2) {
		this.bas2 = bas2;
	}

	public String getBaph() {
		return baph;
	}

	public void setBaph(String baph) {
		this.baph = baph;
	}

	public String getCountryma() {
		return countryma;
	}

	public void setCountryma(String countryma) {
		this.countryma = countryma;
	}

	public String getStprma() {
		return stprma;
	}

	public void setStprma(String stprma) {
		this.stprma = stprma;
	}

	public String getCityma() {
		return cityma;
	}

	public void setCityma(String cityma) {
		this.cityma = cityma;
	}

	public String getZipma() {
		return zipma;
	}

	public void setZipma(String zipma) {
		this.zipma = zipma;
	}

	public String getMas1() {
		return mas1;
	}

	public void setMas1(String mas1) {
		this.mas1 = mas1;
	}

	public String getMas2() {
		return mas2;
	}

	public void setMas2(String mas2) {
		this.mas2 = mas2;
	}

	public String getCountryinc() {
		return countryinc;
	}

	public void setCountryinc(String countryinc) {
		this.countryinc = countryinc;
	}

	public String getStprinc() {
		return stprinc;
	}

	public void setStprinc(String stprinc) {
		this.stprinc = stprinc;
	}

	public String getEin() {
		return ein;
	}

	public void setEin(String ein) {
		this.ein = ein;
	}

	public String getFormer() {
		return former;
	}

	public void setFormer(String former) {
		this.former = former;
	}

	public String getChanged() {
		return changed;
	}

	public void setChanged(String changed) {
		this.changed = changed;
	}

	public String getAfs() {
		return afs;
	}

	public void setAfs(String afs) {
		this.afs = afs;
	}

	public String getWksi() {
		return wksi;
	}

	public void setWksi(String wksi) {
		this.wksi = wksi;
	}

	public String getFye() {
		return fye;
	}

	public void setFye(String fye) {
		this.fye = fye;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getFy() {
		return fy;
	}

	public void setFy(String fy) {
		this.fy = fy;
	}

	public String getFp() {
		return fp;
	}

	public void setFp(String fp) {
		this.fp = fp;
	}

	public String getFiled() {
		return filed;
	}

	public void setFiled(String filed) {
		this.filed = filed;
	}

	public String getAccepted() {
		return accepted;
	}

	public void setAccepted(String accepted) {
		this.accepted = accepted;
	}

	public String getPrevrpt() {
		return prevrpt;
	}

	public void setPrevrpt(String prevrpt) {
		this.prevrpt = prevrpt;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getNciks() {
		return nciks;
	}

	public void setNciks(String nciks) {
		this.nciks = nciks;
	}

	public String getAciks() {
		return aciks;
	}

	public void setAciks(String aciks) {
		this.aciks = aciks;
	}

	public Sub() {
		super();
	}

	public Sub(String adsh, String cik, String name, String sic, String countryba, String stprba,
			String cityba, String zipba, String bas1, String bas2, String baph, String countryma, String stprma,
			String cityma, String zipma, String mas1, String mas2, String countryinc, String stprinc, String ein,
			String former, String changed, String afs, String wksi, String fye, String form, String period, String fy,
			String fp, String filed, String accepted, String prevrpt, String detail, String instance, String nciks,
			String aciks) {
		super();
		this.adsh = adsh;
		this.cik = cik;
		this.name = name;
		this.sic = sic;
		this.countryba = countryba;
		this.stprba = stprba;
		this.cityba = cityba;
		this.zipba = zipba;
		this.bas1 = bas1;
		this.bas2 = bas2;
		this.baph = baph;
		this.countryma = countryma;
		this.stprma = stprma;
		this.cityma = cityma;
		this.zipma = zipma;
		this.mas1 = mas1;
		this.mas2 = mas2;
		this.countryinc = countryinc;
		this.stprinc = stprinc;
		this.ein = ein;
		this.former = former;
		this.changed = changed;
		this.afs = afs;
		this.wksi = wksi;
		this.fye = fye;
		this.form = form;
		this.period = period;
		this.fy = fy;
		this.fp = fp;
		this.filed = filed;
		this.accepted = accepted;
		this.prevrpt = prevrpt;
		this.detail = detail;
		this.instance = instance;
		this.nciks = nciks;
		this.aciks = aciks;
	}

	@Override
	public String toString() {
		return "Sub {id=" + id + ", adsh=" + adsh + ", cik=" + cik + ", name=" + name + ", sic=" + sic + ", countryba="
				+ countryba + ", stprba=" + stprba + ", cityba=" + cityba + ", zipba=" + zipba + ", bas1=" + bas1
				+ ", bas2=" + bas2 + ", baph=" + baph + ", countryma=" + countryma + ", stprma=" + stprma + ", cityma="
				+ cityma + ", zipma=" + zipma + ", mas1=" + mas1 + ", mas2=" + mas2 + ", countryinc=" + countryinc
				+ ", stprinc=" + stprinc + ", ein=" + ein + ", former=" + former + ", changed=" + changed + ", afs="
				+ afs + ", wksi=" + wksi + ", fye=" + fye + ", form=" + form + ", period=" + period + ", fy=" + fy
				+ ", fp=" + fp + ", filed=" + filed + ", accepted=" + accepted + ", prevrpt=" + prevrpt + ", detail="
				+ detail + ", instance=" + instance + ", nciks=" + nciks + ", aciks=" + aciks + "}";
	}
}
