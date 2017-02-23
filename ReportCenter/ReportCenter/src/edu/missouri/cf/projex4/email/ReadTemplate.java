package edu.missouri.cf.projex4.email;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

@SuppressWarnings("serial")
public class ReadTemplate implements Serializable{
	
	@SuppressWarnings("resource")
	public String readTemplate(String path){
		String template = null;
		try{
			File file = new File(path);
			FileInputStream fls = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fls,"UTF-8");
			StringBuffer sb = new StringBuffer();
			BufferedReader in = new BufferedReader(isr);
			while((template = in.readLine()) != null) {
				sb.append(template);
			}
			template = sb.toString();
		} catch(Exception e) {
			System.err.print("error! read html template has issue");
			e.printStackTrace();
		}
		return template;
	}

}
