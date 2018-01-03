import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


/**
 *  This is mostly the same code from the AES lab task, but converted for DES.
 * encrypt() now takes in a byte array of the unencrypted bytes, and returns a byte array.
 * decrypt() now returns a byte array of the decrypted bytes.
 * To convert the encrypted byte array to a string, we use the method found in CryptoLib.
 
 * @author James Michael 788456
 * @author Scott Milne 794147
 */
public class DES {
	static Cipher cipher;
	
	public DES() throws Exception {
		cipher = Cipher.getInstance("DES");
	}

	/**
	 * Takes the plaintext as a byte array and a secret key and encrypts the text, returning the encrypted bytes.
	 * 
	 * @param plainTextByte The byte array that is to be encrypted
	 * @param secretKey The SecretKey that will be used during encryption
	 * @return encryptedByte The encrypted byte array
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] plainTextByte, SecretKey secretKey) throws Exception {
		
		//Initialise the cipher to be in encrypt mode, using the given key.
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		
		//Perform the encryption
		byte[] encryptedByte = cipher.doFinal(plainTextByte);
		
		return encryptedByte;
	}

	/**
	 * Takes the encrypted ciphertext and a secret key and decrypts the text, returning the decrypted bytes.
	 * 
	 * @param encryptedText The encrypted ciphertext that is to be decrypted
	 * @param secretKey The SecretKey that will be used to decrypt
	 * @return decryptedByte The decrypted byte array
	 * @throws Exception
	 */
	public byte[] decrypt(String encryptedText, SecretKey secretKey)
			throws Exception {
		//Get a new Base64 (ASCII) decoder and use it to convert ciphertext from a string into bytes
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] encryptedTextByte = decoder.decode(encryptedText);
		
		//Initialise the cipher to be in decryption mode, using the given key.
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		
		//Perform the decryption
		byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
		
		return decryptedByte;
	}
	
}