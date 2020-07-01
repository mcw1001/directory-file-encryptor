package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Asks user for a key, or generates one
 * then iterates through file and (recursively) through subdirectories of the current directory
 * either making an encrypted copy of each file or replacing the file with an encryption
 * 
 * optional file renaming and name encryption?
 * @author Michael
 *
 */
@SuppressWarnings("unused")
public class FileCruncher {

//		System.out.println(p.getAbsolutePath());
//		System.out.println(p.getParent()+"\\"+p.getName()); //USEFUL FOR RECONSTRUCTION or namechange//*/

	
	//special functions to make use of private defined filters for decrypt specifically
	// done with encrypt just to be consistent
//	public static void doEncrypt(String key, String PATH,Boolean keepOld) {
//		File dir[] = new File(PATH).listFiles();
//		searchEncrypt(key,dir,keepOld);
//	}
//	public static void doDecrypt(String key, String PATH,Boolean keepOld) {
//		File dir2[] = new File(PATH).listFiles(new encFilter());//reuse dir? or make new one
//		searchDecrypt(key,dir2,keepOld);
//	}
	
	
	//maybe assume a directory will be given, have a catch for single file?
	//TODO look into portability, file name scheming etc
//	public static void searchEncrypt(String key, File[] fileList, Boolean keepOld) {// throws GenException {
	public static void searchEncrypt(String key, ArrayList<File> fileList, Boolean keepOld) {// throws GenException {
//		System.out.println("ONE");
		ArrayList<File> unpackedFiles = unpackFileList(fileList);
		for(File file: unpackedFiles) {
//			System.out.println("TWO");
			File fileTemp;
			try {
				String name;
				if(keepOld) {
					fileTemp = new File(file.getAbsolutePath()+".enc");
				}else { //maybe not new? maybe just same file?
					fileTemp = file;
				}
//				System.out.println("Encrypting "+file.getName() +" into " +fileTemp.getName());
				CryptoGen.encrypt(key, file, fileTemp);
				
			} catch (GenException e) {
				e.printStackTrace();
			}
		}
		//System.out.println();
		return;
	}
//	if(encryptNames) {
//	//TODO test for empty or near-empty filename and other issues
//	String name = fileTemp.getName();
//	int ind = name.lastIndexOf('.');
//	//if(ind>0 && ind<name.length()-2) {}
//	name = name.substring(0,ind);
//	name = CryptoGen.encryptString(key, name);
//	Boolean check = fileTemp.renameTo(new File(fileTemp.getParent()+"\\"+name));//
//	if(!check) {
//		throw new GenException("File renaming failed", new Exception());
//	}
//}

	//, File[] fileList
	//TODO take out recursion area
	public static void searchDecrypt(String key, ArrayList<File> fileList,Boolean keepOld) {// throws GenException {
//		System.out.println("ONE");
		ArrayList<File> tempAL;
		for(File file: fileList) {
//			System.out.println("TWO");
			String path = file.getAbsolutePath();//simplifies calls to path (such as path.substring(... path.length()..) etc
			if(file.isDirectory()) {
				if(keepOld) {
					tempAL = new ArrayList<File> (Arrays.asList(file.listFiles(new encFilter())));
					searchDecrypt(key, tempAL,keepOld);
				}else {//if not keepOld, then all files are encrypted; all files can be decrypted
					tempAL = new ArrayList<File> (Arrays.asList(file.listFiles()));
					searchDecrypt(key, tempAL,keepOld);
				}
				
			}else {

//				System.out.println("three");
				//removes '.enc' replaces with '.dec' --> should be optional to do everything on one file 
				File fileTemp;
				if(keepOld) {
					fileTemp = new File(path.substring(0, path.length()-4) + ".dec");//or -5
					//File fileTemp = new File(path.substring(0, path.length()-4));//or -5
				}else {
					//fileTemp = new File(path);
					fileTemp = file;
				}
				//System.out.println("Decrypting "+file.getName() +" into " +fileTemp.getName());
				try {
					CryptoGen.decrypt(key, file, fileTemp);
					if(keepOld) {file.delete();} //deletes the old encrypted file (.enc) for cleanup

				} catch (GenException e) {
					e.printStackTrace();
				}
			}
		}
		//System.out.println();
		return;
	}
//	public static void nameScambleAux(File[] fileList, File nameFile) {
//		
//	}
	/**
	 * recursively finds all files in subdirectories and adds them to one list
	 * @param fileList - array of files, may contain directory
	 */
	//file[] fileList
	public static ArrayList<File> unpackFileList(ArrayList<File> fileList) {
		ArrayList<File> newFileList = new ArrayList<File>();
		ArrayList<File> tempAL;
		for(File file:fileList) {
			if(file.isDirectory()) {
				//newFileList.add(file);//TODO THIS CHANGE HERE!!!
				tempAL = new ArrayList<File> (Arrays.asList(file.listFiles())); //doing this inside the next line with a cast causes casting error, maybe this form instead of (ArrayList<File>)
				newFileList.addAll(unpackFileList(tempAL));
			}else {
				newFileList.add(file);
				
			}
		}
		return newFileList;
	}
	
	public static ArrayList<File> unpackFolderList(File dir){
		ArrayList<File> newFolderList = new ArrayList<File>();
		//newFolderList.add(dir);//TODO test this if rest works
		for(File file: dir.listFiles()) {
			if(file.isDirectory()) {
				//newFolderList.add(file);
				newFolderList.addAll(unpackFolderList(file));
			}else {
				newFolderList.add(file);
			}
		}
		return newFolderList;
	}
	/**
	 * @param fileList - list of files and/or directories to scramble
	 * @param dir - the directory file that fileList is derived from
	 * @param nameFile - file dedicated to holding the information of the unscrambled files (path and file name + scrambled equivalent)
	 * 
	 * @return newFiles - File[] of files with scrambled names, and the nameFile
	 */
//	public static ArrayList<File> nameScramble(File[] fileList, File nameFile) {// throws IOException{//, File nameFile) {
	public static ArrayList<File> nameScramble(ArrayList<File> fileList, File nameFile) {// throws IOException{//, File nameFile) {
		ArrayList<File> unpackedFiles = unpackFileList(fileList);//TODO test with unpackFolderList
		ArrayList<File> newFiles = new ArrayList<File>();
//		Integer ind = 0;
		
		String name;
		String scrambleName;
		String combined;
		FileWriter fWriter;
		File fileTemp;
		Boolean check;
		try {
			fWriter = new FileWriter(nameFile);
			
			for(File file:unpackedFiles) {
				//take current file, add its path, name, and scrambled name to file with two seperators ( ":", and ";" )
				name = file.getName();
				scrambleName = CryptoGen.stringGen(10); //TODO check if extensions important here
				fileTemp = new File(file.getParent()+"\\"+scrambleName);
				combined = fileTemp.getAbsolutePath()+"?"+name+";";
				check = file.renameTo(fileTemp);
//				if(!check) { //TODO temp removed to see if !check non-critical
//					fWriter.close();//close before throw to stop resourceleak
//					throw new IOException("renameTo failed "+file.getName()+" to "+fileTemp.getName());
//				}
				fWriter.write(combined);
				
				newFiles.add(fileTemp);
			}
			newFiles.add(nameFile);
			
			fWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**NOTE: the resulting namefile can be read directly, opening a file using the path string */
		return newFiles;
	}
	/**
	 * TODO ARRAYLIST OUTPUT IDEAL, (hard to set size with no knowledge beforehand)
	 * @param nameFile
	 */
	public static List<File> nameUnscramble(File nameFile) {//throws genexception?
		//TODO use buffered reader, create File from first token, then second token, then renameTo
		List<File> newFiles = new ArrayList<File>();
		String[] tokens = fileToTokens(nameFile);
		String[] tempTok;
		File scrambledFile;
		File niceFile;
		String name;
		Boolean check;
		try {
			for(String str:tokens) {
				if(str!="") {
					tempTok = str.split("\\?");
					scrambledFile = new File(tempTok[0]);
					name = tempTok[1];
					niceFile = new File(scrambledFile.getParent()+"\\"+name);
					check = scrambledFile.renameTo(niceFile);
					if(!check) {
							throw new IOException("renameTo failed "+scrambledFile.getName()+" to "+niceFile.getName());
					}
					newFiles.add(niceFile);
				}
			}
			//TODO check this area for throws and other file deletion errors
			if(!nameFile.delete()) {
				throw new IOException("file "+nameFile.getName() +" cannot be deleted");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newFiles;
	}
	public static void main(String[] args) {
		String p = "A:\\Users\\Michael\\Desktop\\program current\\EncryptionTester";
		File dir = new File(p);
		File[] fList = dir.listFiles();
		File nameFile = new File(dir.getAbsolutePath()+"\\"+"Xx99A&Btg.txt");
//		ArrayList<File> af = unpackFileList(fList);
		ArrayList<File> af = unpackFolderList(dir);
		printFiles(af);
		

	}
	/**Similar to listFiles + fileFilter, this function is used to grab all .enc variants of a list of files after encryption (with KEEPOLD selected)
	 * 
	 * @param fileList
	 * @return encList
	 */
	public static ArrayList<File> getEncs(ArrayList<File> fileList){
		ArrayList<File> encList = new ArrayList<File>();
		File temp;
		for(File file: fileList) {
			temp = new File(file.getAbsolutePath()+".enc");
			encList.add(temp);
		}
		return encList;
	}
	public static String[] fileToTokens(File inFile) {
		String result="";
		String tempS;
		String[] tokens = {""};
		try {
			FileReader fr = new FileReader(inFile);
			BufferedReader br = new BufferedReader(fr);
			StringBuilder sb = new StringBuilder();
			
			while((tempS = br.readLine())!=null) {
				sb.append(tempS);
			}
			br.close();
			
			result = sb.toString();
			tokens = result.split(";");
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		return tokens;
	}
	//just to test
	public static void printFiles(ArrayList<File> newFileList) {
		for(File file: newFileList) {
			System.out.println(file.getName());
		}
	}
	public static void printFilesArray(File[] files) {
		for(File file: files) {
			System.out.println(file.getName());
		}
	}
	
	
}

/*
 * encFilter is a file filter that allows files that are directories or files ending in ".enc"
 * 	ONLY APPLICABLE if the user has selected to keep the old files after encryption, otherwise there won't be any 
 * 	.enc files or .dec
 */
class encFilter implements FileFilter{
	@Override
	public boolean accept(File file) {
		 if(file.isDirectory()) {
			 return true;
		 }
		 String name = file.getName();
		 return name.endsWith(".enc");
	 }
}
