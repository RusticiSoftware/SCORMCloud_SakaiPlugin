package org.sakaiproject.scormcloud.tool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.rusticisoftware.hostedengine.client.Configuration;
import com.rusticisoftware.hostedengine.client.ScormCloud;

public class FileUploadUtils {
	
	public static boolean parseFileUploadRequest(HttpServletRequest request, File outputFile, Map<String, String> params) throws Exception
	{
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		//upload.setSizeMax(Globals.MAX_UPLOAD_SIZE);
		
		FileItemIterator iter = upload.getItemIterator(request);
		while(iter.hasNext()){
			FileItemStream item = iter.next();
			InputStream stream = item.openStream();
			
			//If this item is a file
			if(!item.isFormField()){
			    String name = item.getName();
			    if(name == null){
			        throw new Exception("File upload did not have filename specified");
			    }
			    
                // Some browsers, including IE, return the full path so trim off everything but the file name
                name = getFileNameFromPath(name);
                 
				//Enforce required file extension, if present
				if(!name.toLowerCase().endsWith( ".zip" )){
					throw new Exception("File uploaded did not have required extension .zip");
				}
				
		        bufferedCopyStream(stream, new FileOutputStream(outputFile));
			}
			else {
				params.put(item.getFieldName(), Streams.asString(stream));
			}
		}
		return true;
	}
	
	public static String getFileNameFromPath (String name) {
		
		if (name.indexOf("\\") > -1)
			name = name.substring(name.lastIndexOf("\\")+1);
		if (name.indexOf("/") > -1)
			name = name.substring(name.lastIndexOf("/")+1);
		
		return name;
	}
	
	//Copy inStream to outStream using Java's built in buffering
	public static boolean bufferedCopyStream(InputStream inStream, OutputStream outStream) throws Exception {
		BufferedInputStream bis = new BufferedInputStream( inStream );
		BufferedOutputStream bos = new BufferedOutputStream ( outStream );
		while(true){
			int data = bis.read();
			if (data == -1){
				break;
			}
			bos.write(data);
		}
		bos.flush();
		bos.close();
		return true;
	}
}
