package ar.com.hjg.pngj;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

/**
 * A few static utility methods related with PngReader/PngWriter that read/writes to files.
 * <p>
 * This is not essential to the PNGJ library, and the writer will not work in sandboxed 
 * environments (eg. Google App Engine)
 */
public class FileHelper {

	public static InputStream openFileForReading(File file) {
		InputStream isx = null;
		if (file == null || !file.exists() || !file.canRead())
			throw new PngjInputException("Cannot open file for reading (" + file + ")");
		try {
			isx = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new PngjInputException("Error opening file for reading (" + file + ") : " + e.getMessage());
		}
		return isx;
	}

	/***
	 * WARNING: This method will throw exception if run in a sandboxed environment (as Google App Engine) that does not
	 * permit to use Java class java.io.FileOutputStream To be sure that this just throw error in that case, but that
	 * the class is usable, we use reflection.
	 * 
	 * @param file
	 * @param allowOverwrite
	 * @return outputStream (should be of type FileOutputStream)
	 */
	public static OutputStream openFileForWriting(File file, boolean allowOverwrite) {
		if (file.exists() && !allowOverwrite)
			throw new PngjOutputException("File already exists (" + file + ") and overwrite=false");
		OutputStream os = null;
		Constructor<?> constructorOs = null;
		try {
			constructorOs = Class.forName("java.io.FileOutputStream").getConstructor(File.class);
		} catch (Exception e) {
			throw new PngjOutputException("Error opening file for write. "
					+ "Perhaps running in a sandboxed environment? If so, you can't use this method", e);
		}
		try {
			// osx = new FileOutputStream(file);
			os = (OutputStream) constructorOs.newInstance(file);
		} catch (Exception e) {
			throw new PngjOutputException("error opening " + file + " for write. "
					+ "Check that you have permission to write and that this is not a sandboxed environment", e);
		}
		return os;
	}

	public static PngWriter createPngWriter(File file, ImageInfo imgInfo, boolean allowOverwrite) {
		return new PngWriter(openFileForWriting(file, allowOverwrite), imgInfo, file.getName());
	}

	public static PngReader createPngReader(File file) {
		return new PngReader(openFileForReading(file), file.getName());
	}

}
