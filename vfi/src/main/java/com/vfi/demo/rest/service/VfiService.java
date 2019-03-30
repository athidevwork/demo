/**
 * 
 */
package com.vfi.demo.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vfi.demo.rest.dao.NumRepository;
import com.vfi.demo.rest.dao.PreRepository;
import com.vfi.demo.rest.dao.SubRepository;
import com.vfi.demo.rest.dao.TagRepository;
import com.vfi.demo.rest.entity.Num;
import com.vfi.demo.rest.entity.Pre;
import com.vfi.demo.rest.entity.Sub;
import com.vfi.demo.rest.entity.Tag;

/**
 * @author athi
 *
 */
@Service
public class VfiService {
	@Autowired
	SubRepository subRepo;

	@Autowired
	TagRepository tagRepo;
	
	@Autowired
	NumRepository numRepo;
	
	@Autowired
	PreRepository preRepo;
	
	public Sub getSub(String adsh) {
		return subRepo.findByAdsh(adsh);
	}

	public void saveSub(List<Sub> sub) {
		sub.forEach(item -> {
			System.out.println("Saving Sub " + item);
			subRepo.save(item);
			});
	}

	public void saveTag(List<Tag> tag) {
		tag.forEach(item -> {
			System.out.println("Saving Tag " + item);
			tagRepo.save(item);
			});
	}

	public void saveNum(List<Num> num) {
		num.forEach(item -> {
			System.out.println("Saving Num " + item);
			numRepo.save(item);
			});
	}

	public void savePre(List<Pre> pre) {
		pre.forEach(item -> {
			System.out.println("Saving Pre " + item);
			preRepo.save(item);
			});
	}
}
