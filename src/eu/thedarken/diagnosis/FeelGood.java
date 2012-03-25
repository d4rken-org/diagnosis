package eu.thedarken.diagnosis;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;

public class FeelGood {

	public static boolean testClassesDotDex(ContextWrapper cwr, String cmp_value) throws IOException {
		long crc = Long.parseLong(cmp_value);

		ZipFile zf = new ZipFile(cwr.getPackageCodePath());
		ZipEntry ze = zf.getEntry("classes.dex");

		if (ze.getCrc() != crc) {
			// dex has been modified
			return true;
		} else {
			// dex not tampered with
			return false;
		}
	}
	public static boolean isSignatureOfficial(Context c, String pkgname) throws NameNotFoundException {
		Signature[] sigs = c.getPackageManager().getPackageInfo(pkgname, PackageManager.GET_SIGNATURES).signatures;
		for (Signature sig : sigs) {
			if(sig.hashCode() == 1710960080) {
		    	return true;
		    }
		}
		return false;
	}
	
	public static boolean switchero(boolean b) {
		return !b;
	}
}