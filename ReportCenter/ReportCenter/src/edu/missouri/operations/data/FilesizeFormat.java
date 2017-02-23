package edu.missouri.operations.data;

public class FilesizeFormat {

	private boolean si;
	
	public FilesizeFormat(boolean siFormat) {
		this.si = siFormat;
	}
	
	/**
	 * Found on stackoverflow to format file size. Looks like windows uses si = false.
	 * @param bytes
	 * @param si
	 * @return
	 */
	public String format(long bytes) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
}
