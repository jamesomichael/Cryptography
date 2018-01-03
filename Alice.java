import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import javax.crypto.SecretKey;

// James Michael 788456
// Scott Milne 794147

/**
 * This class is used to represent Alice and how she carries out her tasks.
 * 
 * @author James Michael 788456
 * @author Scott Milne 794147
 */
public class Alice {
	Random rnd = new Random();
	DES des = new DES();
	private ArrayList<byte[]> puzzles = new ArrayList<byte[]>();
	private ArrayList<String> encryptedPuzzles = new ArrayList<String>();
	private ArrayList<Integer> puzzleKeyNums = new ArrayList<Integer>();
	private int sharedPuzNum;
	private String encryptedMessage;
	
	/**
	 * Constructs an Alice object.
	 * On construction, Alice generates 1024 puzzles and writes them to a file.
	 * 
	 * @throws Exception
	 */
	public Alice() throws Exception {
		// Populate an ArrayList with numbers up to 2^16 to be used for the puzzle keys.
		for (int i=0; i<65536; i++) {
			puzzleKeyNums.add(i);
		}
		// Shuffle the ArrayList to ensure the order of numbers is random.
		Collections.shuffle(puzzleKeyNums);	
		
		// Generate the puzzles and write them to a file.
		System.out.println("Alice: Generating puzzles...");
		generatePuzzles();
		System.out.println("Alice: Writing puzzles to file...");
		writePuzzlesToFile();		
	}
	
	/**
	 * Generate 1024 puzzles.
	 * The puzzles are each made of up 16 0 bytes, 2 puzzle number bytes, and 8 puzzle key bytes.
	 * The puzzles are generated in three parts and then joined using a ByteArrayOutputStream.
	 * 
	 * @throws Exception
	 */
	public void generatePuzzles() throws Exception {
		// Create a byte array of size 16, automatically padded with 0s.
		byte[] leadingZeros = new byte[16];
		
		// Generate 1024 puzzles.
		int i=0;
		for (i=0; i<1024; i++) {
			// Generate a unique puzzle number (2 byte array) using the value of i. 
			byte[] puzzleNumber = CryptoLib.smallIntToByteArray(i);
			
			/* Generate a puzzle key using the value of i as the index of the shuffled ArrayList.
			 * Then pad the remaining six bytes with 0s.
			 */
			byte[] puzzleKey = CryptoLib.smallIntToByteArray(puzzleKeyNums.get(i));
			puzzleKey = Arrays.copyOf(puzzleKey, 8);
			
			// Create a SecretKey object using the puzzle key.
			SecretKey key = CryptoLib.createKey(puzzleKey);
			
			/* Define a ByteArrayOutputStream, used to connect the three separate byte arrays
			 * into one.
			 */
			ByteArrayOutputStream baOut = new ByteArrayOutputStream();
			baOut.write(leadingZeros);
			baOut.write(puzzleNumber);
			baOut.write(puzzleKey);
			byte[] puzzle = baOut.toByteArray();
			
			// Add the plaintext puzzle and the encrypts puzzle to their respective ArrayLists.
			puzzles.add(puzzle);
			String encryptedPuz = CryptoLib.byteArrayToString(des.encrypt(puzzle, key));
			encryptedPuzzles.add(encryptedPuz);
			
		}	
		
	}
	
	
	/**
	 * Writes all of the generated puzzles (encrypted) to a text file. Each puzzle is
	 * written to a new line.
	 * 
	 * @throws IOException
	 */
	public void writePuzzlesToFile() throws IOException {
		// Create a text file and a construct a PrintWriter object.
		File file = new File("alicepuzzles3.txt");
		//FileWriter fWriter = new FileWriter(file);
		//FileOutputStream fOut = new FileOutputStream(file);
		PrintWriter writer = new PrintWriter(file);
		
		// Write each of the encrypted puzzles to the text file.
		for (String s : encryptedPuzzles) {
			writer.write(s + "\n");	
		}
		//fWriter.close();
		
		// Close the PrintWriter.
		writer.close();
	}
	
	/**
	 * Lookup the puzzle key using the puzzle number provided by Bob.
	 * 
	 * @return The unique SecretKey associated with the relevant puzzle number. 
	 * @throws Exception
	 */
	public SecretKey lookupPuzzleKey() throws Exception {
		/* Get the puzzle number provided by Bob, and return the puzzle.
		 * (With our implementation, we can simply lookup the puzzle at the 'puzzleNum'
		 * index in the puzzles ArrayList.)
		 */
		int puzzleNum = getSharedPuzzleNumber();
		byte[] puzzle = puzzles.get(puzzleNum);
		
		// To extract the puzzle key, get the final 8 bytes of the puzzle.
		byte[] extractedKey = Arrays.copyOfRange(puzzle, 18, 26);
		System.out.println("Puzzle key: " + CryptoLib.byteArrayToSmallInt(extractedKey));
		
		// Create a DES key using the 8 byte puzzle key.
		return CryptoLib.createKey(extractedKey);
	}
	
	/**
	 * Writes and encrypts a message for Bob to receive, using the key found in lookupPuzzleKey().
	 * 
	 * @param key The shared key used between Alice and Bob.
	 * @throws Exception
	 */
	public void setMessageForBob(SecretKey key) throws Exception {
		String message = "HELLOBOB";
		byte[] baMessage = new byte[message.getBytes().length];
		
		// Convert the message to a byte array and encrypt it using the key.
		baMessage = CryptoLib.stringToByteArray(message);
		this.encryptedMessage = CryptoLib.byteArrayToString(des.encrypt(baMessage, key));
	}
	
	/**
	 * Gets Alice's encrypted message for Bob.
	 * 
	 * @return encryptedMessage The encrypted message.
	 */
	public String getMessageForBob() {
		return encryptedMessage;
	}
	
	/**
	 * Gets the puzzle number sent by Bob.
	 * This will be used when looking up the associated key.
	 * 
	 * @return sharedPuzNum The shared puzzle number.
	 */
	public int getSharedPuzzleNumber() {
		return sharedPuzNum;
	}
	
	/**
	 * Sets the puzzle number sent by Bob.
	 * 
	 * @param sharedPuzNum The shared puzzle number.
	 */
	public void setSharedPuzzleNumber(int sharedPuzNum) {
		this.sharedPuzNum = sharedPuzNum;
	}
	
	
	
}
