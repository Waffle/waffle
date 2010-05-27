package waffle.tomcat;

import java.util.Arrays;

/**
 * Rudimentary NTLM message utility.
 * @author dblock[at]dblock[dot]org
 */
abstract class NtlmMessage {
	
	// NTLM messages start with 0x4e544c4d53535000, NTLMSSP signature
	static final byte[] ntlmsspSignature = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50, 0x00 };

	public static boolean isNtlmMessage(byte[] message) {	
		if (message == null || message.length < ntlmsspSignature.length)
			return false;
		
		byte[] messageSignature = Arrays.copyOfRange(message, 0, ntlmsspSignature.length);
		return Arrays.equals(ntlmsspSignature, messageSignature);
	}
	
	/**
	 * Get NTLM message type.
	 * @param message
	 *  Assuming a valid NTLM message.
	 * @return
	 *  Message type.  
	 */
	public static int getMessageType(byte[] message) {
		return message[ntlmsspSignature.length];
	}
}
