import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Finder {

	private static MessageDigest md;

	static {
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("cannot initialize SHA-512 hash function " + e);
		}
	}

	public static void find(Map<String, List<String>> duplicateList, File directory, boolean type) throws Exception {
		String hash;
		for (File object : directory.listFiles()) {
			if (object.isDirectory())
				find(duplicateList, object, type);
			else {
				hash = type ? hashSave(object) : hashQuick(object);
				List<String> copyList = duplicateList.get(hash);
				if (copyList == null) {
					copyList = new LinkedList<String>();
					duplicateList.put(hash, copyList);
				}
				copyList.add(object.getAbsolutePath());
			}
		}
	}

	private static String hashQuick(File inputFile) throws Exception {			// COMPROMISE MEMORY, FASTER HASH CALCULATION
		FileInputStream fin = new FileInputStream(inputFile);
		byte data[] = new byte[(int) inputFile.length()];
		fin.read(data);
		fin.close();
		String hash = new BigInteger(1, md.digest(data)).toString(16);
		return hash;

	}
	
	private static String hashSave(File inputFile) throws Exception {			// SAVE MEMORY, SLOWER HASH CALCULATION
		RandomAccessFile file = new RandomAccessFile(inputFile, "r");
		int buffSize = 16384;
		byte[] buffer = new byte[buffSize];
		long read = 0;
		long offset = file.length();
		int unitsize;
		while (read < offset) {
			unitsize = (int) (((offset - read) >= buffSize) ? buffSize : (offset - read));
			file.read(buffer, 0, unitsize);
			md.update(buffer, 0, unitsize);
			read += unitsize;
		}
		file.close();
		String hash = new BigInteger(1, md.digest()).toString(16);
		return hash;
	}

}