package nm.itunes.findTunesWithNoFile;

public class FindTunesWithNoFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		doTest();
	}

	public static void doTest() {
		TunesXML tunesXML = new TunesXML();
		tunesXML.init();
		tunesXML.findTunesWithNoFile();
	}
}
