/**
 * 
 */
package com.vfi.demo.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vfi.demo.rest.entity.Num;
import com.vfi.demo.rest.entity.Pre;
import com.vfi.demo.rest.entity.Sub;
import com.vfi.demo.rest.entity.Tag;

/**
 * @author athi
 *
 */
public class Util {
	String TAB = "	";
	
	public List<Sub> processSubFile(String inputFilePath) {
	    List<Sub> inputList = new ArrayList<Sub>();
	    try{
	      File inputF = new File(inputFilePath);
	      InputStream inputFS = new FileInputStream(inputF);
	      BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
	      // skip the header of the file
	      inputList = br.lines().skip(1).map(mapToSub).collect(Collectors.toList());
	      br.close();
	    } catch (FileNotFoundException e) {
	    	System.out.println("FileNotFoundException : " + e);
	    } catch (IOException e) {
	    	System.out.println("IOException : " + e);
	    }
	    return inputList ;
	}
	
	private Function<String, Sub> mapToSub = (line -> {
		  String[] p = line.split(TAB);
		  Sub item = new Sub(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7],
				  p[8], p[9], p[10], p[11], p[12], p[13], p[14], p[15],
				  p[16], p[17], p[18], p[19], p[20], p[21], p[22], p[23],
				  p[24], p[25], p[26], p[27], p[28], p[29], p[30], p[31],
				  p[32], p[33], p[34], p[33]);
		  return item;
	});
	
	public List<Tag> processTagFile(String inputFilePath) {
	    List<Tag> inputList = new ArrayList<Tag>();
	    try{
	      File inputF = new File(inputFilePath);
	      InputStream inputFS = new FileInputStream(inputF);
	      BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
	      // skip the header of the file
	      inputList = br.lines().skip(1).map(mapToTag).collect(Collectors.toList());
	      br.close();
	    } catch (FileNotFoundException e) {
	    	System.out.println("FileNotFoundException : " + e);
	    } catch (IOException e) {
	    	System.out.println("IOException : " + e);
	    }
	    return inputList ;
	}
	
	private Function<String, Tag> mapToTag = (line -> {
		  String[] p = line.split(TAB);
		  Tag item = new Tag(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8]);
		  return item;
	});
	
	public List<Num> processNumFile(String inputFilePath) {
	    List<Num> inputList = new ArrayList<Num>();
	    try{
	      File inputF = new File(inputFilePath);
	      InputStream inputFS = new FileInputStream(inputF);
	      BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
	      // skip the header of the file
	      inputList = br.lines().skip(1).map(mapToNum).collect(Collectors.toList());
	      br.close();
	    } catch (FileNotFoundException e) {
	    	System.out.println("FileNotFoundException : " + e);
	    } catch (IOException e) {
	    	System.out.println("IOException : " + e);
	    }
	    return inputList ;
	}
	
	private Function<String, Num> mapToNum = (line -> {
		  String[] p = line.split(TAB);
		  Num item = new Num(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
		  return item;
	});
	
	public List<Pre> processPreFile(String inputFilePath) {
	    List<Pre> inputList = new ArrayList<Pre>();
	    try{
	      File inputF = new File(inputFilePath);
	      InputStream inputFS = new FileInputStream(inputF);
	      BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
	      // skip the header of the file
	      inputList = br.lines().skip(1).map(mapToPre).collect(Collectors.toList());
	      br.close();
	    } catch (FileNotFoundException e) {
	    	System.out.println("FileNotFoundException : " + e);
	    } catch (IOException e) {
	    	System.out.println("IOException : " + e);
	    }
	    return inputList ;
	}
	
	private Function<String, Pre> mapToPre = (line -> {
		  String[] p = line.split(TAB);
		  Pre item = new Pre(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9]);
		  return item;
	});
}
