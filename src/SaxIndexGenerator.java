import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 多次元空間データからUniversalSAXの索引を生成する
 * @author chiemi
 *
 */
public class SaxIndexGenerator {

	public int dimension;
	public int resolution;
	public int window;
	public int labels;

	ArrayList<double[]> list=new ArrayList<double[]>();
	ArrayList<LabelInfo> labelinfo = new ArrayList<LabelInfo>();
	ArrayList<Integer> hilbertList = new ArrayList<Integer>();
	int[][] lattice = null;


	public SaxIndexGenerator(int d,int r,int l,int w){
		this.dimension=d;
		this.resolution=r;
		this.labels=l;
		this.window=w;
	}

	public void resetData(){
		list.clear();
		hilbertList.clear();
	}

	/**
	 * CSVファイルを読み込んでlistに追加する。
	 * @param filename
	 */
	public void readTimeSeriesData(String filename){
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line="";
			while((line = br.readLine())!=null){
				String[] data = line.split(",");
				if(data.length<dimension){
					System.out.println("?");
				}
				double[] d = new double[dimension];
				for(int dim=0;dim<dimension;dim++){
					d[dim]=new Double(data[dim]).doubleValue();
				}
				this.list.add(d);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ラベル情報を読み込む
	 * @param filename ラベル情報が含まれているファイル名
	 */
	public void readLabelInfo(String filename){
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line="";
			char state = 's';
			while((line = br.readLine())!=null){
				if(state=='s'){
					if(line.equals(""))continue;
					if(line.startsWith("# Regions")) state='g';
					continue;
				}
				else if(state=='g'){
					if(line.equals(""))continue;
					if(line.charAt(0)==' ')continue;
					if(line.startsWith("# ")) break;
					String[] data = line.split(",");
					LabelInfo info = new LabelInfo();
					info.region[0]=new Integer(data[1]).intValue();
					info.region[1]=new Integer(data[2]).intValue();
					info.alphabet=data[0];
					this.labelinfo.add(info);
				}

			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * データを次元ごとに正規化する
	 * @param tno
	 * @return 正規化された値
	 */
	public double[][] getNormalizationData(){
		double[][] result = new double[list.size()][dimension];
		for(int i=0;i<dimension;i++){
			ArrayList<Float> data = new ArrayList<Float>();
			for(int j=0;j<this.list.size();j++){
				data.add((float) this.list.get(j)[i]);
			}
			//次元ごとに正規化
			normalization(data);
			for(int j=0;j<list.size();j++){
				result[j][i]=data.get(j);
			}
		}
		return result;
	}

	public void outputNormalizedData(String tno){
		double[][] result = this.getNormalizationData();
		try {
			FileWriter outFile = new FileWriter("normalizeddata/data"+tno+".csv");
			for(int i=0;i<result.length;i++){
				for(int j=0;j<result[i].length;j++){
					outFile.write(""+result[i][j]);
					if(j==result[i].length-1)outFile.write("\n");
					else outFile.write(",");
				}
			}
			outFile.close();
		}catch(Exception e){e.printStackTrace();}

	}

	public void generateLatticeValues(){
		int newlength = (int) Math.ceil((double)list.size()/(double)window);
		this.lattice = new int[newlength][dimension];
		for(int i=0;i<dimension;i++){
			ArrayList<Float> data = new ArrayList<Float>();
			for(int j=0;j<list.size();j++){
				data.add((float) list.get(j)[i]);
			}
			//次元ごとに正規化
			normalization(data);

			//Windowごとに平均化
			ArrayList<Float> roughenlist=averaging(data,window);
			for(int j=0;j<roughenlist.size();j++){
				float v = roughenlist.get(j);
				if(v>4)v=4;
				if(v<-4)v=4;
				lattice[j][i]=(int)(Math.floor(Math.pow(2, this.resolution)*(v+4)/8));
			}
		}
	}

	public void trans2HilbertValues(){

		//正規化して平均化する→lattice配列にデータが入る
		generateLatticeValues();

		//ヒルベルト値に変換
		for(int i=0;i<lattice.length;i++){
			int v;
			if(this.dimension == 3){
				int a = Hilbert.encode3Dmain(lattice[i][0],lattice[i][1],lattice[i][2],resolution);
				v = a;
			}
			else{
				int b = Hilbert.encode2Dmain(lattice[i][0],lattice[i][1],resolution);
				v = b;
			}

			hilbertList.add(v);
		}

	}

	public void transMultiHilbertValues(){

		//正規化して平均化する→lattice配列にデータが入る
		generateLatticeValues();
		Hcode multiDim = new Hcode();

		//ヒルベルト値に変換(現段階では二次元のみ対応）
		for(int i=0;i<lattice.length;i++){

			multiDim.hcode[0] = i;
			//multiDim.hcode[1] = j;

			//int v = HilbertOrderMulti.H_encode(lattice[i][0],lattice[i][1],resolution);
			//hilbertList.add(v);
		}

	}

	public String makeSAXString(){
		String str="";
		for(int i=0;i<this.hilbertList.size();i++){
			int v = this.hilbertList.get(i);
			for(int j=0;j<this.labelinfo.size();j++){
				LabelInfo info = labelinfo.get(j);
				if(info.region[0]<=v && v<info.region[1]){
					str += info.alphabet;
					break;
				}
			}
		}
		return str;
	}

	public String makeSAXString_for_MDSAX(int dim){
		String str="";
		for(int i=0;i<this.lattice.length;i++){
			int v = this.lattice[i][dim];
			for(int j=0;j<this.labelinfo.size();j++){
				LabelInfo info = labelinfo.get(j);
				if(info.region[0]<=v && v<info.region[1]){
					str += info.alphabet;
					break;
				}
			}
		}
		return str;
	}


	static void normalization(ArrayList<Float> list){
		int length=list.size();
		//平均値を求める
		Float sum=new Float(0),avg,stdsum=new Float(0);
		Float stddev;
		for(int i=0;i<length;i++){
			sum+=list.get(i);
		}
		avg=sum/length;
		//標準偏差を求める
		for(int i=0;i<length;i++){
			stdsum+=(list.get(i)-avg)*(list.get(i)-avg);
		}
		stddev=new Float(Math.sqrt(stdsum/length));
		//正規化
		for(int i=0;i<length;i++){
			list.set(i, (list.get(i)-avg)/stddev);
		}
	}


	static ArrayList<Float> averaging(ArrayList<Float> list,int interval){
		int i=0,j,num=0;
		float sum;
		ArrayList<Float> hist=new ArrayList();
		while(i<list.size()){
			sum=0;
			for(j=0;j<interval;j++){
				sum+=list.get(i);
				i++;
				if(i==list.size()){
					num=j+1;
					break;
				}
			}
			if(j==interval){num=j;}
			float avg=sum/num;
			hist.add(avg);
		}
		return hist;
	}


	static String RunlengthCompression(String src){
		String dest="";
		//char prev=src.charAt(0);
		String prev = String.valueOf(src.charAt(0))+ String.valueOf(src.charAt(1));


		int length=1;
		for(int i=2;i<src.length();i=i+2){
			//char c=src.charAt(i);
			String c = String.valueOf(src.charAt(i))+String.valueOf(src.charAt(i+1));

			if(prev.equals(c)){
				length++;
			}else{
				if(length==1)
					dest+=prev;
				else
					dest+=""+length+prev;
				length=1;
				prev=c;
			}
		}
		if(length==1)
			dest+=prev;
		else
			dest+=""+length+prev;
		return dest;
	}

	public static String extractMatchString(String regex, String target) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			throw new IllegalStateException("No match found.");
		}
	}


	public static void generate_MDSAX(int dimension,int resolution,int labels,int window){
		String outfile = "result_MDSAX"+dimension+"_"+resolution+"_"+labels+"_.txt";

		SaxIndexGenerator sig = new SaxIndexGenerator(dimension,resolution,labels,window);
		//割り当てる文字の情報を読み込む(1次元の情報）
		sig.readLabelInfo("LabelInfo_1_"+resolution+"_"+labels+"_.txt");

		try {
			FileWriter outFile = new FileWriter(outfile);

		//String path = "C:\\filelist";
	    //File dir = new File("C:/Users/Ayaka Onishi/Downloads/pleiades-e3.7-java-jre_20110924/workspace/UniversalSax");

			File dir = new File("testcsvdata");
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				String tno = extractMatchString("test(.*).csv",files[i].getName());
//			for(int i=0;i<3;i++){
				//File file = files[i];
				//時系列データを読み込む
				sig.readTimeSeriesData(files[i].getAbsolutePath());
				//時系列データを格子データに変換する
				sig.generateLatticeValues();
				//SAXの生成
				for(int d=0;d<dimension;d++){
					String saxindex = sig.makeSAXString_for_MDSAX(d);
					outFile.write(""+tno+","+d+","+saxindex+"\n");
					//ランレングス圧縮
					//String compindex=RunlengthCompression(saxindex);
					//outFile.write(compindex+"\n");
					//System.out.println(compindex);
				}
				sig.resetData();
			}
			outFile.close();
		}catch(Exception e){e.printStackTrace(); System.exit(-1);}

	}

	public static void generate_UniversalSAX(int dimension,int resolution,int labels,int window){
		String outfile = "result_USAX_"+dimension+"_"+resolution+"_"+labels+"_.csv";

		SaxIndexGenerator sig = new SaxIndexGenerator(dimension,resolution,labels,window);
		//割り当てる文字の情報を読み込む
		sig.readLabelInfo("LabelInfo_"+dimension+"_"+resolution+"_"+labels+"_.txt");

		try {
			FileWriter outFile = new FileWriter(outfile);

			File dir = new File("testcsvdata");
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				String tno = extractMatchString("test(.*).csv",files[i].getName());

				//ちゃんと読みこめているかカウントテスト
				//BufferedReader br;
				//br = new BufferedReader(new FileReader(files[i].getAbsolutePath()));
				//String line="";
				//int d = 0;
				//while((line = br.readLine())!=null){
				//	d++;
				//}
				//System.out.println(i+"回目："+d);

//			for(int i=0;i<3;i++){
				//File file = files[i];
				//時系列データを読み込む
				sig.readTimeSeriesData(files[i].getAbsolutePath());
				//(比較実験のため）正規化後データを保存する
				sig.outputNormalizedData(tno);
				//時系列データをヒルベルトコードに変換する
				sig.trans2HilbertValues();
				//SAXの生成
				String saxindex = sig.makeSAXString();
				System.out.println(saxindex);
				outFile.write(""+tno+","+saxindex+"\n");
				//ランレングス圧縮
				//String compindex=RunlengthCompression(saxindex);
				//System.out.println(compindex);
				//outFile.write(""+tno+","+compindex+"\n");

				//Listのリセット
				sig.resetData();
			}
			outFile.close();
		}catch(Exception e){e.printStackTrace(); System.exit(-1);}

	}


	public static void main(String[] args) {

		//
		int dimension = 3;
		int resolution = 9; //多次元空間の格子の解像度。格子の数は各次元 2^resolution 個になる。　
		int labels = 811; //割り当てる符号の数
		int window = 5; //平均化するウインドウ幅

		if(args.length==0){
			System.err.println("ERROR: Input filename");
			System.exit(-1);
		}

		//UniversalSAXで符号化する場合はこちらを使う
		SaxIndexGenerator.generate_UniversalSAX(dimension, resolution, labels, window);
		//MDSAXで符号化する場合はこちらを使う
		//SaxIndexGenerator.generate_MDSAX(dimension, resolution, labels, window);

	}
}
