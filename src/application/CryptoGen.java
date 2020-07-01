package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;//maybe remove these after
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Michael
 *
 */

@SuppressWarnings("unused")
public class CryptoGen {
	private static final String ALGO_TYPE = "AES";
	private static final String TRANSFORM_TYPE = "AES";
	
	public static void encrypt(String key, File inFile, File outFile)
            throws GenException {
        runCipher(Cipher.ENCRYPT_MODE, key, inFile, outFile);
    }
 
    public static void decrypt(String key, File inFile, File outFile)
            throws GenException {
        runCipher(Cipher.DECRYPT_MODE, key, inFile, outFile);
    }
    
    private static void runCipher(int mode, String key, File inFile, File outFile) throws GenException {

    	try {
    	Key sKey = new SecretKeySpec(key.getBytes(), ALGO_TYPE); //specify "UTF-8" ?
    	Cipher cipher = Cipher.getInstance(TRANSFORM_TYPE);
    	cipher.init(mode, sKey);//check other types of init, maybe one with Initial vector for CBC?
    	
    	FileInputStream inStream = new FileInputStream(inFile);
    	byte[] plainBytes = new byte[(int) inFile.length()];
    	inStream.read(plainBytes);
    	
    	byte[] cipherBytes = cipher.doFinal(plainBytes);
    	FileOutputStream outStream = new FileOutputStream(outFile);
    	outStream.write(cipherBytes);
    	
    	inStream.close();
    	outStream.close();
    	}catch(NoSuchAlgorithmException | NoSuchPaddingException
    			| InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException ex){
    		throw new GenException("ERROR - File Encryption/Decryption", ex);
    	}
    }
    
    
    public static String encryptString(String key, String input)
            throws GenException {
        return runStringCipher(Cipher.ENCRYPT_MODE, key, input);
    }
 
    public static String decryptString(String key, String input)
            throws GenException {
        return runStringCipher(Cipher.DECRYPT_MODE, key, input);
    }
    private static String runStringCipher(int mode, String key, String input) throws GenException{
    	
    	try {
    	Key k = new SecretKeySpec(key.getBytes(), ALGO_TYPE);
    	Cipher c = Cipher.getInstance(TRANSFORM_TYPE);
    	c.init(mode, k);
    	
    	byte[] inputBytes = input.getBytes();//turns text into bytes to encrypt
    	byte[] outputBytes = c.doFinal(inputBytes);
    	String output = new String(outputBytes, StandardCharsets.UTF_8);
    	return output;
    	
    	}catch(NoSuchAlgorithmException | NoSuchPaddingException| InvalidKeyException | 
    			IllegalBlockSizeException | BadPaddingException ex) { //use other exceptions later
    	//	NoSuchAlgorithmException | NoSuchPaddingException| InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException ex){
    		throw new GenException("ERROR - runStringCipher", ex);
    	}
    	//return "";
    }
    public static String stringGen(int length) {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789"; //maybe not 1-9
		SecureRandom rand = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		while(sb.length()<length) {
			int i = (int) (rand.nextFloat() * chars.length()); //rand.nextFloat()
			sb.append(chars.charAt(i));
		}
		return sb.toString();
	}

    
    
}
