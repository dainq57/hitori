package group11.hitori.com;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class WriteFile {

	private static FileWriter fw;
	private static String Url = "src\\filesat\\Sat.cnf";
	
	public WriteFile(){
		
		File file = new File(Url);
		try {
			if(file.exists()){
				file.delete();
			}
			fw = new FileWriter(Url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void write(String line) throws IOException {
		
		try {
	           			
			fw.write(line);
	            
	    } catch (IOException exc) {
	        System.out.println("Error1 !");
	        return;
	    }
	}
	
	public void closeFile(){
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
