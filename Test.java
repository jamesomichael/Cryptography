import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.SecretKey;


/**
 *  This class demonstrates the program working as a whole.
 * It constructs Alice and Bob as objects and demonstrates what they
 * are doing at each step.
 * It then shows how they exchange keys and messages.
 * @author James Michael 788456
 * @author Scott Milne 794147
 */
public class Test {
	public static void main(String[] args) throws Exception {
		DES des = new DES();

		/* Construct Alice and Bob objects.
		 * On construction, Alice generates puzzles and writes them to a file.
		 * Bob reads the puzzles from the file, picks a puzzle at random and cracks it.
		 */
		Alice alice = new Alice();
		Bob bob = new Bob();
		
		// Get the puzzle number from Bob's cracked puzzle and send it to Alice.
		int crackedPuzzleNumber = bob.getPuzzleNumber();
		System.out.println("Puzzle number: " + crackedPuzzleNumber);
		System.out.println("\nBob: Sending puzzle number to Alice...");
		alice.setSharedPuzzleNumber(crackedPuzzleNumber);
		
		// Alice looks up the associated puzzle key using the number sent by Bob.
		System.out.println("\nAlice: Looking up key using puzzle number...");
		SecretKey sharedKey = alice.lookupPuzzleKey();
		
		// Alice writes a message for Bob and encrypts using the key from the cracked puzzle.
		System.out.println("\nAlice: Writing & encrypting message for Bob...");
		alice.setMessageForBob(sharedKey);
		String messageForBob = alice.getMessageForBob();
		System.out.println("Encrypted message: " + messageForBob);
		
		// Bob decrypts Alice's message.
		System.out.println("\nBob: Decrypting message from Alice...");
		bob.setMessageFromAlice(messageForBob, sharedKey);
		String messageFromAlice = bob.getMessageFromAlice();
		System.out.println("Decrypted message: " + messageFromAlice);
				
	}
}
