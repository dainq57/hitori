package group11.hitori.com;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Input {

	private static int[][] input;
	private Random rand = new Random();
	private static int size;
	private static int choose=0;
	private static int choose_before;
	private static String Url;

	@SuppressWarnings("static-access")
	public Input(int size) {
		this.size = size;
		Url = "src\\map\\" + size + "x" + size + ".sat";
	}

	public int[][] getInput() {
		return input;
	}

	public int[][] readFile() {
		input = new int[size][size];
		boolean status = false;
		try {
			FileReader fr = new FileReader(Url);
			BufferedReader br = new BufferedReader(fr);

			String line;
			int i = 0, j = 0;
			line = br.readLine();
			int numberMatrix = Integer.parseInt(line);
			if(choose == 0 || numberMatrix == 1){
				choose = rand.nextInt(numberMatrix) + 1;
			}else{
				choose_before = choose;
				choose = rand.nextInt(numberMatrix) + 1;
				 while(choose == choose_before){
					 choose = rand.nextInt(numberMatrix) + 1;
				 }
			}
			
			String start = "<" + choose + ">";
			String end = start.replace("<", "</");
			
			while ((line = br.readLine()) != null && i < size) {
				line = line.trim();
				if(line.equals(start) == true && status == false){
					status = true;
				}else if(status == true){
					if(line.equals(end) == false){

						String[] number = line.split(" ");
						for (int k = 0; k < number.length; k++) {
								input[i][j] = Integer.parseInt(number[k]);
								j++;
							}
						j = 0;
						i++;
					}else{
						status = false;
						break;
					}
				}

			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.out.println("Not Found!");
		}
		return input;
	}

}
