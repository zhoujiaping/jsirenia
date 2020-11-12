package org.jsirenia.util;

import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 */
public class ZipUtils {

    public static void zip(InputStream input, OutputStream output, String entryName) {
        try (ZipOutputStream zos = new ZipOutputStream(output)){
            zos.putNextEntry(new ZipEntry(entryName));
            StreamUtils.copy(input,zos);
            zos.closeEntry();
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        }
    }

    public static void unZip(InputStream input, Callback.Callback20e<String,ZipInputStream> callback){
	    try(ZipInputStream zis = new ZipInputStream(input)){
            ZipEntry entry = zis.getNextEntry();
            callback.apply(entry.getName(),zis);
	        //StreamUtils.copy(zis,output);
	        zis.closeEntry();
	        //return entry.getName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
