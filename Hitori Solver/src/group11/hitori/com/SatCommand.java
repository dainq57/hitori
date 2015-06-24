package group11.hitori.com;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;


public class SatCommand {

private static HashMap<String, String> infoSat;
	
	public SatCommand(){
		infoSat = new HashMap<String, String>();
	}
	
	public HashMap<String, String> getInfoSat() {
		return infoSat;
	}

	public void RunCommandSat() throws Exception {
        ProcessBuilder builder = new ProcessBuilder(
            "cmd.exe", "/c", "cd \"src\\filesat\" && (java -jar org.sat4j.core.jar Sat.cnf)");
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while((line = r.readLine()) != null) {
            
            String var = "c declared #vars";
            String constraint = "c #constraints";
            String clock = "time (in seconds)";
            if(line.indexOf(var) != -1){
            	infoSat.put("var", searchKey(line, var));
            }
            if(line.indexOf(constraint) != -1){
            	infoSat.put("clause", searchKey(line, constraint));
            }
            if(line.indexOf(clock) != -1){
            	/*System.out.println(line);*/
            	infoSat.put("time", searchKey(line, clock));
            }
        }
    }
	
    private String searchKey(String resource, String key){
    	String value ="";
    	String result = "";
    	if(resource.indexOf(key) != -1){
	    	for(int i=resource.length()-1; i>=0; i--){
	    		value += resource.charAt(i);
	    		if(resource.charAt(i) == 32){
	    			break;
	    		}
	    	}
	    	result += reverseString(value);
    	}
    	return result;
    }
    
    private String reverseString(String key){
    	String result="";
    	for(int i=key.length()-1; i>=0; i--)
    		result += key.charAt(i);
    	return result;
    }
    
    public void show(){
    	System.out.println(infoSat.get("var"));
    	System.out.println(infoSat.get("clause"));
    	System.out.println(infoSat.get("time"));
    }
}
