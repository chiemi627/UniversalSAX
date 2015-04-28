import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;


public class TightnessOfLowerBoundCalucular {

	/**
	 * @param args
	 */



	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		//すんごい途中

		String str1 = "distance_orig_sort";
		String str2 = "distance_USAX_sort";

		DB db1 = DBMaker.newFileDB(new File("origDB")).writeAheadLogDisable().make();
		DB db2 = DBMaker.newFileDB(new File("USAXDB")).writeAheadLogDisable().make();

		Map<String,Double> distanceListMap1 = db1.getTreeMap("origMap");
		Map<String,Double> distanceListMap2 = db2.getTreeMap("USAXMap");

		ArrayList<String> alpha = new ArrayList<String>();


		try {

			BufferedReader br1 = new BufferedReader(new FileReader(str1 +".csv"));
			//BufferedReader br2 = new BufferedReader(new FileReader(str2 +".csv"));


			String line;
			line = br1.readLine();
			int lno=0;
			while((line = br1.readLine())!=null){
				//距離情報を取り出す
				String[] str = line.split(",");
				//distanceList.put(alpha.get(lno)+""+alpha.get(lno), new Double(0));
				distanceListMap1.put(alpha.get(lno)+""+alpha.get(lno), new Double(0));

				for(int j=lno+1;j<alpha.size();j++){
					String key=""+alpha.get(j)+alpha.get(lno);
					//distanceList.put(key, new Double(str[j-lno-1]));
					distanceListMap1.put(key, new Double(str[j-lno-1]));

					key = ""+alpha.get(lno)+alpha.get(j);
					//distanceList.put(key, new Double(str[j-lno-1]));
					distanceListMap1.put(key, new Double(str[j-lno-1]));
				}
				lno++;
			}


		//distanceList.put(""+alpha.get(alpha.size()-1)+alpha.get(alpha.size()-1),new Double(0));
		distanceListMap1.put(""+alpha.get(alpha.size()-1)+alpha.get(alpha.size()-1),new Double(0));


		}catch(Exception e){e.printStackTrace();System.exit(-1);}

	}

}
