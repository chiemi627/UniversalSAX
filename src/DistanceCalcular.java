import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mapdb.*;

public class DistanceCalcular {

	//mapDBの作成
	DB db = DBMaker.newFileDB(new File("distanceListDB")).writeAheadLogDisable().make();
	ArrayList<String> dataname=new ArrayList<String>();

	public static double comp2TimeSeries_default(double[][] a,double[][] b){
		double res=0;
		double[][] tmp;
		if(a.length>b.length){tmp = a; b = a; a = tmp;} //aの方が短くなるようにする
		for(int i=0;i<a.length;i++)
			for(int j=0;j<a[0].length;j++){
				res += ((a[i][j]-b[i][j])*(a[i][j]-b[i][j]));
			}
		return res;
	}

	public static double compTimeWarpingDistance_orig(ArrayList<double[]> a,ArrayList<double[]> b){
		int i=0,j=0;
		double dist=0;
		while(i<a.size()-1 && j<b.size()-1){
			double dmin;
			int flg=1;

			if(i==a.size()-1 && j<b.size()-1){
				dist+=DistanceCalcular.getDistOrig(a,b,i,j+1);
				j++;
				continue;
			}
			else if(i<a.size()-1 && j==b.size()-1){
				dist+=DistanceCalcular.getDistOrig(a,b,i+1,j);
				i++;
				continue;
			}

			double d1 = DistanceCalcular.getDistOrig(a,b,i+1,j+1);
			dmin = d1;
			double d2 = DistanceCalcular.getDistOrig(a,b,i+1,j);
			if(dmin>d2) {dmin=d2; flg=2;}
			double d3 = DistanceCalcular.getDistOrig(a,b,i,j+1);
			if(dmin>d3) {dmin=d3; flg=3;}
			dist+=dmin;
			if(flg==1){i++;j++;}
			else if(flg==2){i++;}
			else if(flg==3){j++;}
		}
		return dist;
	}

	//TODO
	public static double compTimeWarpingDistanceDB_byUSAX(String a,String b,Map<String,Double> distanceListDB){
		int i=0,j=0;
		double dist=0;
		while(i<a.length()-1 && j<b.length()-1){
			double dmin;
			int flg=1;

			if(i==a.length()-1 && j<b.length()-1){
				dist+=DistanceCalcular.getDistUSAX_DB(a,b,i,j+1,distanceListDB);
				j=j+2;
				continue;
			}
			else if(i<a.length()-1 && j==b.length()-1){
				dist+=DistanceCalcular.getDistUSAX_DB(a,b,i+1,j,distanceListDB);
				i=i+2;
				continue;
			}

			double d1 = DistanceCalcular.getDistUSAX_DB(a,b,i+2,j+2,distanceListDB);
			dmin = d1;
			double d2 = DistanceCalcular.getDistUSAX_DB(a,b,i+2,j,distanceListDB);
			if(dmin>d2) {dmin=d2; flg=2;}
			double d3 = DistanceCalcular.getDistUSAX_DB(a,b,i,j+2,distanceListDB);
			if(dmin>d3) {dmin=d3; flg=3;}
			dist+=dmin;
			if(flg==1){i=i+2;j=j+2;}
			else if(flg==2){i=i+2;}
			else if(flg==3){j=j+2;}
		}
		return dist;
	}

	public static double compTimeWarpingDistance_byUSAX(String a,String b,HashMap<String,Double> distanceList){
		int i=0,j=0;
		double dist=0;
		while(i<a.length()-1 && j<b.length()-1){
			double dmin;
			int flg=1;

			if(i==a.length()-1 && j<b.length()-1){
				dist+=DistanceCalcular.getDistUSAX(a,b,i,j+1,distanceList);
				j=j+2;
				continue;
			}
			else if(i<a.length()-1 && j==b.length()-1){
				dist+=DistanceCalcular.getDistUSAX(a,b,i+1,j,distanceList);
				i=i+2;
				continue;
			}

			double d1 = DistanceCalcular.getDistUSAX(a,b,i+2,j+2,distanceList);
			dmin = d1;
			double d2 = DistanceCalcular.getDistUSAX(a,b,i+2,j,distanceList);
			if(dmin>d2) {dmin=d2; flg=2;}
			double d3 = DistanceCalcular.getDistUSAX(a,b,i,j+2,distanceList);
			if(dmin>d3) {dmin=d3; flg=3;}
			dist+=dmin;
			if(flg==1){i=i+2;j=j+2;}
			else if(flg==2){i=i+2;}
			else if(flg==3){j=j+2;}
		}
		return dist;
	}

	public static double compTimeWarpingDistance_byMdSAX(String[] a,String[] b,HashMap<String,Double> distanceList){
		int i=0,j=0;
		double dist=0;
		while(i<a[0].length()-1 && j<b[0].length()-1){
			double dmin;
			int flg=1;

			if(i==a[0].length()-1 && j<b[0].length()-1){
				dist+=DistanceCalcular.getDistMdSAX(a,b,i,j+1,distanceList);
				j++;
				continue;
			}
			else if(i<a[0].length()-1 && j==b[0].length()-1){
				dist+=DistanceCalcular.getDistMdSAX(a,b,i+1,j,distanceList);
				i++;
				continue;
			}

			double d1 = DistanceCalcular.getDistMdSAX(a,b,i+1,j+1,distanceList);
			dmin = d1;
			double d2 = DistanceCalcular.getDistMdSAX(a,b,i+1,j,distanceList);
			if(dmin>d2) {dmin=d2; flg=2;}
			double d3 = DistanceCalcular.getDistMdSAX(a,b,i,j+1,distanceList);
			if(dmin>d3) {dmin=d3; flg=3;}
			dist+=dmin;
			if(flg==1){i++;j++;}
			else if(flg==2){i++;}
			else if(flg==3){j++;}
		}
		return dist;
	}

	//TODO
	public static double getDistUSAX_DB(String a,String b,int i,int j,Map<String,Double> distanceListDB){
		if(i==0 && j==0) return 0;
		else if(i==0 || j==0) return 100000;
		else{
			String x = String.valueOf(a.charAt(i)) + String.valueOf(a.charAt(i+1));
			String y = String.valueOf(b.charAt(i)) + String.valueOf(b.charAt(i+1));
			return distanceListDB.get(""+x+y);
		}

	}

	public static double getDistUSAX(String a,String b,int i,int j,HashMap<String,Double> distanceList){
		if(i==0 && j==0) return 0;
		else if(i==0 || j==0) return 100000;
		else{
			String x = String.valueOf(a.charAt(i)) + String.valueOf(a.charAt(i+1));
			String y = String.valueOf(b.charAt(i)) + String.valueOf(b.charAt(i+1));
			return distanceList.get(""+x+y);
		}

	}

	public static double getDistMdSAX(String[] a,String[] b,int i,int j,HashMap<String,Double> distanceList){
		if(i==0 && j==0) return 0;
		else if(i==0 || j==0) return 100000;
		else{
			double dist=0;
			for(int d=0;d<a.length;d++){
				String key = ""+a[d].charAt(i)+b[d].charAt(j);
				//System.out.println("i="+i+" j="+j+" d="+d+" key="+key);
				double dst = distanceList.get(key);
				dist+= Math.pow(dst,2);
			}
			return Math.sqrt(dist);
		}
		//else return distanceList.get(""+a.charAt(i)+b.charAt(j));
	}

	public static double getDistOrig(ArrayList<double[]> a,ArrayList<double[]> b,int i,int j){
		double dist=0;
		for(int d=0;d<a.get(0).length;d++){
			dist += Math.pow((a.get(i)[d]-b.get(j)[d]),2);
		}
		return Math.sqrt(dist);
	}

	// TODO
	public Map<String,Double> readDistFileDB(String filename){
		//HashMap<String,Double> distanceList = new HashMap<String,Double>();
		Map<String,Double> distanceListMap = db.getTreeMap("distanceListMap");

		ArrayList<String> alpha = new ArrayList<String>();
		BufferedReader br;
		char state='i';
		try {
			String line;
			br = new BufferedReader(new FileReader(filename));
			int lno=0;
			while((line = br.readLine())!=null){
				if(line.startsWith("# Regions")){
					state='r';
					continue;
				}
				else if(line.startsWith("# Distance")){
					state='d';
					continue;
				}
				else if(line.equals("")){continue;}
				else{
					if(state=='i')continue;
					if(state=='d'){//距離情報を取り出す
						String[] str = line.split(",");
						//distanceList.put(alpha.get(lno)+""+alpha.get(lno), new Double(0));
						distanceListMap.put(alpha.get(lno)+""+alpha.get(lno), new Double(0));

						for(int j=lno+1;j<alpha.size();j++){
							String key=""+alpha.get(j)+alpha.get(lno);
							//distanceList.put(key, new Double(str[j-lno-1]));
							distanceListMap.put(key, new Double(str[j-lno-1]));

							key = ""+alpha.get(lno)+alpha.get(j);
							//distanceList.put(key, new Double(str[j-lno-1]));
							distanceListMap.put(key, new Double(str[j-lno-1]));
						}
						lno++;
					}
					else if(state=='r'){//アルファベット情報を取り出す
						String[] str = line.split(",");
						alpha.add(str[0]);
					}
				}
				//distanceList.put(""+alpha.get(alpha.size()-1)+alpha.get(alpha.size()-1),new Double(0));
				distanceListMap.put(""+alpha.get(alpha.size()-1)+alpha.get(alpha.size()-1),new Double(0));

			}
		}catch(Exception e){e.printStackTrace();System.exit(-1);}

		//return distanceList;
		return distanceListMap;
	}


	public HashMap<String,Double> readDistFile(String filename){
		HashMap<String,Double> distanceList = new HashMap<String,Double>();
		ArrayList<String> alpha = new ArrayList<String>();
		BufferedReader br;
		char state='i';
		try {
			String line;
			br = new BufferedReader(new FileReader(filename));
			int lno=0;
			while((line = br.readLine())!=null){
				if(line.startsWith("# Regions")){
					state='r';
					continue;
				}
				else if(line.startsWith("# Distance")){
					state='d';
					continue;
				}
				else if(line.equals("")){continue;}
				else{
					if(state=='i')continue;
					if(state=='d'){//距離情報を取り出す
						String[] str = line.split(",");
						distanceList.put(alpha.get(lno)+""+alpha.get(lno), new Double(0));
						for(int j=lno+1;j<alpha.size();j++){
							String key=""+alpha.get(j)+alpha.get(lno);
							distanceList.put(key, new Double(str[j-lno-1]));
							key = ""+alpha.get(lno)+alpha.get(j);
							distanceList.put(key, new Double(str[j-lno-1]));
						}
						lno++;
					}
					else if(state=='r'){//アルファベット情報を取り出す
						String[] str = line.split(",");
						alpha.add(str[0]);
					}
				}
				distanceList.put(""+alpha.get(alpha.size()-1)+alpha.get(alpha.size()-1),new Double(0));

			}
		}catch(Exception e){e.printStackTrace();System.exit(-1);}

		return distanceList;
	}


	public String extractMatchString(String regex, String target) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			throw new IllegalStateException("No match found.");
		}
	}

	public double[][] calcAllDistance_orig(String path){

		//まずは全部のデータを読む
		File dir = new File(path);
		File[] files = dir.listFiles();
		HashMap<String,ArrayList<double[]>> data = new HashMap<String,ArrayList<double[]>>();
		double[][] distance = new double[files.length][files.length];
		for (int i = 0; i < files.length; i++) {
			String tno = extractMatchString("principal_4dim_(.*).csv",files[i].getName());
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(files[i].getAbsolutePath()));
				String line="";
				ArrayList<double[]> dlist = new ArrayList<double[]>();
				while((line = br.readLine())!=null){
					String[] str = line.split(",");
					/*//3次元に限定バージョン
					double[] d = new double[3];
					d[0]=new Double(str[0]).doubleValue();
					d[1]=new Double(str[1]).doubleValue();
					d[2]=new Double(str[2]).doubleValue();
					dlist.add(d);
					*/

					//多次元対応ばーじょん
					double[] d = new double[str.length];
					for(int j=0;j<str.length; j++){
						d[j] = new Double(str[j]).doubleValue();
					}
					dlist.add(d);


				}
				br.close();
				data.put(tno, dlist);
			}catch(Exception e){e.printStackTrace();System.exit(-1);}
		}
		//距離を求める
		Object[] keys = data.keySet().toArray();
		for(int i=0;i<keys.length;i++){
			this.dataname.add((String)keys[i]);
			distance[i][i]=0;
			for(int j=i+1;j<keys.length;j++){
				distance[i][j]=DistanceCalcular.compTimeWarpingDistance_orig(data.get((String)keys[i]), data.get((String)keys[j]));
				distance[j][i]=distance[i][j];
			}
		}

		return distance;
	}

	public double[][] calcAllDistanceDB(String infile, Map<String, Double> distanceListDB){
		ArrayList<String> data = new ArrayList<String>();

		//データファイルを読み込む
		BufferedReader br;
		try {
			String line;
			br = new BufferedReader(new FileReader(infile));
			while((line = br.readLine())!=null){
				String[] seq = line.split(",");
				this.dataname.add(seq[0]);
				data.add(seq[1]);
			}
			br.close();
		}catch(Exception e){e.printStackTrace();}
		double[][] result = new double[data.size()][data.size()];
		for(int i=0;i<data.size();i++){
			result[i][i]=0;
			for(int j=i+1;j<data.size();j++){
				//System.out.println("i="+i+",j="+j);

				//double dist = DistanceCalcular.comp2TimeSeries_bySAX(data.get(i), data.get(j), distanceList);
				double dist = compTimeWarpingDistanceDB_byUSAX(data.get(i),data.get(j),distanceListDB);
				result[i][j]=dist;
				result[j][i]=dist;
			}
		}
		System.out.println("data.size="+data.size());
		return result;
	}

	public double[][] calcAllDistance(String infile, HashMap<String, Double> distanceList){
		ArrayList<String> data = new ArrayList<String>();

		//データファイルを読み込む
		BufferedReader br;
		try {
			String line;
			br = new BufferedReader(new FileReader(infile));
			while((line = br.readLine())!=null){
				String[] seq = line.split(",");
				this.dataname.add(seq[0]);
				data.add(seq[1]);
			}
			br.close();
		}catch(Exception e){e.printStackTrace();}
		double[][] result = new double[data.size()][data.size()];
		for(int i=0;i<data.size();i++){
			result[i][i]=0;
			for(int j=i+1;j<data.size();j++){
				//System.out.println("i="+i+",j="+j);
				//double dist = DistanceCalcular.comp2TimeSeries_bySAX(data.get(i), data.get(j), distanceList);
				double dist = compTimeWarpingDistance_byUSAX(data.get(i),data.get(j),distanceList);
				result[i][j]=dist;
				result[j][i]=dist;
			}
		}
		System.out.println("data.size="+data.size());
		return result;
	}

	public double[][] calcAllDistance_byMdSAX(String infile, HashMap<String, Double> distanceList){
		ArrayList<String[]> data = new ArrayList<String[]>();

		//データファイルを読み込む
		BufferedReader br;
		try {
			String line;
			br = new BufferedReader(new FileReader(infile));
			ArrayList<String> d = null;
			while((line = br.readLine())!=null){
				String[] seq = line.split(",");
				if(seq[1].equals("0")){
					this.dataname.add(seq[0]);
					if(d==null){
						d=new ArrayList<String>();
						d.add(seq[2]);
					}else{
						//前のデータをdataに追加する
						Object[] obj = d.toArray();
						String[] str = new String[obj.length];
						for(int i=0;i<str.length;i++)
							str[i]=(String)obj[i];
						data.add(str);
						//次のためにクリアする
						d.clear();
						//1次元目の値を追加する
						d.add(seq[2]);
					}
				}
				else{
					d.add(seq[2]);
				}
			}
			//最後のデータをdataに追加する
			Object[] obj = d.toArray();
			String[] str = new String[obj.length];
			for(int i=0;i<str.length;i++)
				str[i]=(String)obj[i];
			data.add(str);
			br.close();
		}catch(Exception e){e.printStackTrace();}
		double[][] result = new double[data.size()][data.size()];
		for(int i=0;i<data.size();i++){
			result[i][i]=0;
			for(int j=i+1;j<data.size();j++){
				//System.out.println("***d("+i+","+j+")");
				double dist = compTimeWarpingDistance_byMdSAX(data.get(i),data.get(j),distanceList);
				result[i][j]=dist;
				result[j][i]=dist;
			}
		}
		System.out.println("data.size="+data.size());
		return result;
	}

	public void do_main_by_orig(String path){
		double[][] dist = calcAllDistance_orig(path);
		try {
			FileWriter outFile = new FileWriter("distance_orig.txt");

			outFile.write(" ,");
			for(int i=0;i<dist.length;i++){
				outFile.write("principal_4dim_"+dataname.get(i));
				if(i==dist.length-1)outFile.write("\n");
				else outFile.write(",");
			}
			for(int i=0;i<dist.length;i++){
				outFile.write("data"+dataname.get(i)+",");
				for(int j=0;j<dist.length;j++){
					//System.out.println("["+i+","+j+"]="+dist[i][j]);
					outFile.write(""+dist[i][j]);
					if(j<dist.length-1)outFile.write(",");
					else outFile.write("\n");
				}
			}
			outFile.close();
		}catch(Exception e){e.printStackTrace(); System.exit(-1);}
	}



	public void do_main_by_mdsax(int dimension,int resolution,int labels){
		HashMap<String,Double> distanceList = readDistFile("LabelInfo_1_"+resolution+"_"+labels+"_.txt");
		double[][] dist = calcAllDistance_byMdSAX("result_MDSAX_"+dimension+"_"+resolution+"_"+labels+"_small.csv",distanceList);
		try {
			FileWriter outFile = new FileWriter("distance_MDSAX_"+dimension+"_"+resolution+"_"+labels+"_small.txt");

			outFile.write(" ,");
			for(int i=0;i<dist.length;i++){
				outFile.write("data"+dataname.get(i));
				if(i==dist.length-1)outFile.write("\n");
				else outFile.write(",");
			}
			for(int i=0;i<dist.length;i++){
				outFile.write("data"+dataname.get(i)+",");
				for(int j=0;j<dist.length;j++){
					//System.out.println("["+i+","+j+"]="+dist[i][j]);
					outFile.write(""+dist[i][j]);
					if(j<dist.length-1)outFile.write(",");
					else outFile.write("\n");
				}
			}
			outFile.close();
		}catch(Exception e){e.printStackTrace(); System.exit(-1);}
	}

	// TODO
	public void do_main_by_usax(int dimension,int resolution,int labels){


		//mapDB使います
		Map<String,Double> distanceListMap = db.getTreeMap("distanceListMap");

		//HashMap<String,Double> distanceList = readDistFile("LabelInfo_"+dimension+"_"+resolution+"_"+labels+"_.txt");
		distanceListMap = readDistFileDB("LabelInfo_"+dimension+"_"+resolution+"_"+labels+"_.txt");


		//double[][] dist = calcAllDistance("result_USAX_"+dimension+"_"+resolution+"_"+labels+"_.csv",distanceList);
		double[][] dist = calcAllDistanceDB("result_USAX_"+dimension+"_"+resolution+"_"+labels+"_.csv",distanceListMap);
		try {
			FileWriter outFile = new FileWriter("distance_USAX_"+dimension+"_"+resolution+"_"+labels+"_.csv");

			outFile.write(" ,");
			for(int i=0;i<dist.length;i++){
				outFile.write("data"+dataname.get(i));
				if(i==dist.length-1)outFile.write("\n");
				else outFile.write(",");
			}
			for(int i=0;i<dist.length;i++){
				outFile.write("data"+dataname.get(i)+",");
				for(int j=0;j<dist.length;j++){
					//System.out.println("["+i+","+j+"]="+dist[i][j]);
					outFile.write(""+dist[i][j]);
					if(j<dist.length-1)outFile.write(",");
					else outFile.write("\n");
				}
			}
			outFile.close();
		}catch(Exception e){e.printStackTrace(); System.exit(-1);}


	}





	public double[][] calcAllDistance_by_1d_ver2(String infile, HashMap<String, Double> distanceList){
		ArrayList<String> data = new ArrayList<String>();

		//データファイルを読み込む
		BufferedReader br;
		try {
			String line;
			br = new BufferedReader(new FileReader(infile));
			while((line = br.readLine())!=null){
				String[] seq = line.split(",");
				this.dataname.add(seq[0]);
				data.add(seq[1]);
			}
			br.close();
		}catch(Exception e){e.printStackTrace();}
		double[][] result = new double[data.size()][data.size()];
		for(int i=0;i<data.size();i++){
			result[i][i]=0;
			for(int j=i+1;j<data.size();j++){
				//System.out.println("i="+i+",j="+j);

				String[] saxstr1 = {data.get(i)};
				String[] saxstr2 = {data.get(j)};

				double dist = compTimeWarpingDistance_by1d(saxstr1,saxstr2,distanceList);
				result[i][j]=dist;
				result[j][i]=dist;
			}
		}
		System.out.println("data.size="+data.size());
		return result;
	}






	//重心距離法とかで作成した文字列同士の比較をするために距離を測る関数
	public static double getDist1d(String[] a,String[] b,int i,int j,HashMap<String,Double> distanceList){
		if(i==0 && j==0) return 0;
		else if(i==0 || j==0) return 100000;
		else{
			double dist=0;
			for(int d=0;d<a.length;d++){
				String key = ""+a[d].charAt(i)+b[d].charAt(j);
				//System.out.println("i="+i+" j="+j+" d="+d+" key="+key);
				double dst = distanceList.get(key);
				dist+= dst;
			}
			return dist;
		}
		//else return distanceList.get(""+a.charAt(i)+b.charAt(j));
	}


	public void do_main_by_1d_ver2(int dimension,int resolution,int labels){
		HashMap<String,Double> distanceList = readDistFile("LabelInfo_1_"+resolution+"_"+labels+"_.txt");

		//距離を測りたい文字列のcsvファイル名を指定
		double[][] dist = calcAllDistance_by_1d_ver2("SaxIndex_Gravity_.csv",distanceList);

		try {
			FileWriter outFile = new FileWriter("distance_id_Gravity_"+dimension+"_"+resolution+"_"+labels+"_.csv");

			outFile.write(" ,");
			for(int i=0;i<dist.length;i++){
				outFile.write("data"+dataname.get(i));
				if(i==dist.length-1)outFile.write("\n");
				else outFile.write(",");
			}
			for(int i=0;i<dist.length;i++){
				outFile.write("data"+dataname.get(i)+",");
				for(int j=0;j<dist.length;j++){
					//System.out.println("["+i+","+j+"]="+dist[i][j]);
					outFile.write(""+dist[i][j]);
					if(j<dist.length-1)outFile.write(",");
					else outFile.write("\n");
				}
			}
			outFile.close();
		}catch(Exception e){e.printStackTrace(); System.exit(-1);}
	}



	//重心距離法とかで作成した文字列同士の比較をするためのタイムワーピング処理
	public static double compTimeWarpingDistance_by1d(String[] a,String[] b,HashMap<String,Double> distanceList){
		int i=0,j=0;
		double dist=0;
		while(i<a[0].length()-1 && j<b[0].length()-1){
			double dmin;
			int flg=1;

			if(i==a[0].length()-1 && j<b[0].length()-1){
				dist+=DistanceCalcular.getDist1d(a,b,i,j+1,distanceList);
				j++;
				continue;
			}
			else if(i<a[0].length()-1 && j==b[0].length()-1){
				dist+=DistanceCalcular.getDist1d(a,b,i+1,j,distanceList);
				i++;
				continue;
			}

			double d1 = DistanceCalcular.getDist1d(a,b,i+1,j+1,distanceList);
			dmin = d1;
			double d2 = DistanceCalcular.getDist1d(a,b,i+1,j,distanceList);
			if(dmin>d2) {dmin=d2; flg=2;}
			double d3 = DistanceCalcular.getDist1d(a,b,i,j+1,distanceList);
			if(dmin>d3) {dmin=d3; flg=3;}
			dist+=dmin;
			if(flg==1){i++;j++;}
			else if(flg==2){i++;}
			else if(flg==3){j++;}
		}
		return dist;
	}



	//重心距離法とかで作成した文字列同士の比較をするためのメイン関数
	public void do_main_by_1d(int dimension,int resolution,int labels){
		HashMap<String,Double> distanceList = readDistFile("LabelInfo_"+dimension+"_"+resolution+"_"+labels+"_.txt");
		String str1 = null;
		String str2 = null;

		double dist;


		BufferedReader br;
		//比較したい文字列1つ目を読みこむ
		try {
			String line;
            br = new BufferedReader(new FileReader("SaxIndex_Principal_data0_3_interval5_1.txt"));
            while ((line = br.readLine()) != null) {
                System.out.print("読み込んだ文字列1つ目："+line + "\n");
                str1 = line;

            }
            br.close();
        } catch (IOException e) {
            System.out.println(e);
        }

		//比較したい文字列2つ目を読み込む
		try {
			String line;
			br = new BufferedReader(new FileReader("SaxIndex_Principal_data0_3_interval5_3.txt"));

            while ((line = br.readLine()) != null) {
                System.out.print("読み込んだ文字列2つ目："+line + "\n");
                str2 = line;

            }
            br.close();
        } catch (IOException e) {
            System.out.println(e);
        }

		//互換性のために配列に入れます
		String[] saxstr1 = {str1};
		String[] saxstr2 = {str2};

		dist = DistanceCalcular.compTimeWarpingDistance_by1d(saxstr1, saxstr2,distanceList);

		System.out.print("距離測定結果："+dist + "\n");

		try {
			FileWriter outFile = new FileWriter("distance_SaxIndex_Gravity_interval5.txt");

			outFile.write(""+dist);

			outFile.close();
		}catch(Exception e){e.printStackTrace(); System.exit(-1);}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int dimension=4;
		int resolution=9;
		int labels=2175;
		DistanceCalcular dcalc = new DistanceCalcular();
		//Universal SAXを使う場合
		//dcalc.do_main_by_usax(dimension,resolution,labels);
		//MultiDimensional SAXを使う場合
		//dcalc.do_main_by_mdsax(dimension,resolution,labels);
		//普通に距離を測る場合
		dcalc.do_main_by_orig("testcsvdata");
		//重心距離法や主成分分析使用したSAX文字列の距離を測る場合（2つの文字列を入れて距離を測る）
		//dcalc.do_main_by_1d(dimension,resolution,labels);
		//重心距離法や主成分分析使用したSAX文字列の距離を測る場合(csv形式のファイルを読み込んで全組合せを測る)
		//dcalc.do_main_by_1d_ver2(dimension,resolution,labels);



	}



}