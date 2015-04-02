package weka.classifiers.timeseries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 * This class read every @fileType under @directorName
 */
public class InputProject {
	public List<File> files;
	public List<String> files_names;

	public InputProject() {
		files_names = new ArrayList<String>();
	}

	public List<File> readDirectory(String directoryName, String[] fileType)
			throws IOException {
		File dir = new File(directoryName);
		String[] extensions = fileType;
		System.out.println("Reading all csv files in " + dir.getCanonicalPath()
				+ " including those in subdirectories");
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions,
				true);
		for (File file : files) {
			System.out.println("file: " + file.getName());
		}
		return files;
	}
	
	public void deleteFolder(String dir) {
		  File delfolder=new File(dir); 
		  File oldFile[] = delfolder.listFiles();
		  try 
		  { 
		     for (int i = 0; i < oldFile.length; i++)
		     {
		        if(oldFile[i].isDirectory())
		        {
		           deleteFolder(dir+oldFile[i].getName()+"//"); //递归清空子文件夹
		        }
		        oldFile[i].delete();
		     }
		  } 
		  catch (Exception e) 
		  { 
		    System.out.println("清空文件夹操作出错!"); 
		    e.printStackTrace(); 
		  }
		}
		 
	
}