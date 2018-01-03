import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import javax.crypto.SecretKey;



/**
 * This class is used to represent Bob and how he carries out his tasks.
 * 
 * @author James Michael 788456
 * @author Scott Milne 794147
 */
public class Bob {
	private ArrayList<String> readPuzzles = new ArrayList<String>();
	Random random = new Random();
	DES des = new DES();
	private byte[] crackedPuzzle = new byte[26];
	private String messageFromAlice;
	
	/**
	 * Constructs a Bob object.
	 * On construction, Bob reads the encrypted puzzles from the text file,
	 * chooses a puzzle at random and tries to crack it.
	 * 
	 * @throws Exception
	 */
	public Bob() throws Exception {
		// Read each of the puzzles from the text file.
		System.out.println("\nBob: Reading puzzles from file...");
		readPuzzlesFromFile();
		
		// Choose a puzzle at random.
		System.out.println("Bob: Choosing a puzzle at random...");
		String chosenPuzzle = chooseRandomPuzzle();
		System.out.println("Chosen puzzle: " + chosenPuzzle);
		
		// Crack the chosen puzzle.
		System.out.println("\nBob: Cracking puzzle...");
		crackPuzzle(chosenPuzzle);
	}
	
	/**
	 * Reads each puzzle line by line from the text file and adds it to an ArrayList.
	 * 
	 * @throws FileNotFoundException
	 */
	public void readPuzzlesFromFile() throws FileNotFoundException {
		// Use a FileReader to read the file, and a Scanner to find the puzzles within.
		FileReader fIn = new FileReader("alicepuzzles3.txt");
		Scanner inFile = new Scanner(fIn);
		
		// Add all the encrypted puzzles to the readPuzzles ArrayList.
		while (inFile.hasNextLine()) {
			String encryptedPuzzle = inFile.nextLine();
			readPuzzles.add(encryptedPuzzle);
		}	
	}
	
	/**
	 * Chooses an encrypted puzzle at random
	 * 
	 * @return randomPuzzle The puzzle chosen at random.
	 */
	public String chooseRandomPuzzle() {
		// Generate a random number between 0 and the size of the readPuzzles ArrayList (1024).
		int randomNum = random.nextInt(readPuzzles.size());
		
		/* Use the random number as the index for the ArrayList, and return the puzzle at that
		 * index.
		 */
		String randomPuzzle = readPuzzles.get(randomNum);
		return randomPuzzle;
	}
	
	/**
	 * Uses the puzzle chosen at random and tries to crack it by brute force - using all
	 * possible 2^16 puzzle keys.
	 *
	 * @param puzzle The puzzle chosen at random.
	 * @throws Exception
	 */
	public void crackPuzzle(String puzzle) throws Exception {
		byte[] crackedPuzzle = new byte[26];
		int i=0;
		
		/* For all 2^16 possible puzzle key values, try all possible keys to
		 * decrypt the chosen puzzle.
		 */
		for (i=0;i<65536;i++) {
			// Convert the value of i to a byte array, and create the SecretKey.
			byte[] baKey = CryptoLib.smallIntToByteArray(i);
			baKey = Arrays.copyOf(baKey, 8);
			SecretKey key = CryptoLib.createKey(baKey);
			
			
			try {
				// Store the decrypted bytes.
				byte[] decrypted = des.decrypt(puzzle, key);
				
				/* Take the first 16 bytes of the decrypted puzzle, and compare them
				 * to a 16 byte array of only 0s.
				 * If the puzzle begins with 16 0 bytes then it has been decrypted successfully.
				 */
				byte[] test = Arrays.copyOf(decrypted, 16);
				byte[] zeros = new byte[16];
				if (Arrays.equals(test, zeros)) {
					// Store the decrypted puzzle.
					crackedPuzzle = decrypted;
				}
				
			} catch(BadPaddingException e) {
			}
			
		}
		
		// Print and set the decrypted puzzle.
		System.out.print("Cracked: ");
		for (byte b : crackedPuzzle) {
			System.out.print(b);
		}
		System.out.println();
		
		setCrackedPuzzle(crackedPuzzle);
	}
	
	/**
	 * Gets the cracked puzzle.
	 * 
	 * @return crackedPuzzle An array of decrypted bytes.
	 */
	public byte[] getCrackedPuzzle() {
		return crackedPuzzle;
	}
	
	/**
	 * Gets the puzzle number which Bob will send to Alice.
	 * 
	 * @return The puzzle number.
	 */
	public int getPuzzleNumber() {
		System.out.println("\nBob: Extracting puzzle number...");
		
		/* Get the puzzle and copy bytes 17 and 18 (the puzzle number) to a 
		 * new byte array.
		 */
		byte[] puzzle = getCrackedPuzzle();
		byte[] puzzleNum = Arrays.copyOfRange(puzzle, 16, 18);
		
		// Convert the byte array to a number and return it.
		return CryptoLib.byteArrayToSmallInt(puzzleNum);
	}
	
	/**
	 * Gets the message sent by Alice.
	 * 
	 * @return messageFromAlice The message from Alice.
	 */
	public String getMessageFromAlice() {
		return messageFromAlice;
	}
	
	/**
	 * Sets the cracked puzzle once successfully found using brute force.
	 * 
	 * @param crackedPuzzle The decrypted puzzle.
	 */
	public void setCrackedPuzzle(byte[] crackedPuzzle) {
		this.crackedPuzzle = crackedPuzzle;
	}
	
	/**
	 * Decrypts the message received from Alice, using the shared key.
	 * 
	 * @param message The message sent by Alice.
	 * @param sharedKey The key used between Alice and Bob.
	 * @throws Exception
	 */
	public void setMessageFromAlice(String message, SecretKey sharedKey) throws Exception {
		this.messageFromAlice = CryptoLib.byteArrayToString(des.decrypt(message, sharedKey));
	}
	
}
