/**
 * 
 */
package com.vfi.demo.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vfi.demo.rest.entity.Num;
import com.vfi.demo.rest.entity.Pre;
import com.vfi.demo.rest.entity.Sub;
import com.vfi.demo.rest.entity.Tag;
import com.vfi.demo.rest.service.VfiService;


/**
 * @author athi
 *
 */
@RestController
@RequestMapping("/api/vfi")
public class VfiController {
	@Autowired
	VfiService service;
	
	Logger l = LoggerFactory.getLogger("VfiController");
	
	@GetMapping("/data")
	public String addDomData() {		
		String subFile = "src/main/resources/data/sub.txt";
		String tagFile = "src/main/resources/data/tag.txt";
		String numFile = "src/main/resources/data/num.txt";
		String preFile = "src/main/resources/data/pre.txt";
		
		Util util = new Util();
		List<Sub> sub = util.processSubFile(subFile);
		List<Tag> tag = util.processTagFile(tagFile);
		List<Num> num = util.processNumFile(numFile);
		List<Pre> pre = util.processPreFile(preFile);
		
		StringBuilder sb = new StringBuilder();
		sb.append(sub).append("\n").append(tag).append("\n").append(num).append("\n").append(pre);
		
		System.out.println(sub.toString());
		System.out.println(tag.toString());
		System.out.println(num.toString());
		System.out.println(pre.toString());
		
		/*sub.forEach(item -> {
			item.setNum(num);
			item.setPre(pre);
		});*/
		
		//populate sub
		service.saveSub(sub);
		service.saveTag(tag);
		service.saveNum(num);
		service.savePre(pre);
		
		//return "Added dom json data";
		return "Added data to database.\n" + sb.toString();
	}
	
	@GetMapping(path = "/{adsh}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public Sub getWeatherByAdsh(@PathVariable(name = "adsh") String adsh) {
		return service.getSub(adsh);
	}
}
