import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

/*
 *  Class: xSaxLabeler
 */
public class uSaxLabeler {


	int[] array = null;
	public int dimension;
	public int resolution;
	public final static int elements = 1000000;
	int[] histogram = new int[100];

	static String[] symbolicCode = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","0","1","2","3","4","5","6","7","8","9","+","/"};

	public uSaxLabeler(int d,int r){
		this.dimension=d;
		this.resolution=r;
		this.array = new int[elements];
	}

	public void generateValues() throws UnSupportedDimensionException{
		Random rnd = new Random();
		for(int i=0;i<elements;i++){
			int[] value = new int[dimension];
			for(int d=0;d<dimension;){
				double x=rnd.nextGaussian();
				if(x>3 || x<-3)continue;
				x = Math.pow(2, this.resolution)*(x+3)/6;
				value[d] = (int) Math.floor(x);
				d++;
			}

			if(dimension==3)
				array[i] = Hilbert.encode3Dmain(value[0], value[1],value[2], this.resolution);

			else if(dimension==2)
				//array[i] = Hilbert.encode2Dmain(value[0], value[1], this.resolution);
				array[i] = HilbertOrder.encode(value[0], value[1], this.resolution);
			
			else if(dimension==1){
				array[i] = value[0];				
			}

			else{
				throw new UnSupportedDimensionException();
			}

			int backet = (int) ((array[i]/Math.pow(2,this.resolution*dimension))*100);
			histogram[backet]++;
		}
	}

	public ArrayList<int[]> makeLabels(int nofLabels){
		ArrayList<int[]> regions= new ArrayList<int[]>();
		Arrays.sort(array);
		int interval = (int) (elements/nofLabels);
		int prev=0;
		for(int i=0;i<nofLabels;i++){
			int next = array[i*interval+1];
			if(i==nofLabels-1)
				next=(int) (Math.pow(2, resolution*dimension))-1;

			regions.add(new int[]{prev,next});
			prev=next;
		}

		return regions;
	}

	public ArrayList<int[]> devideLabels(ArrayList<int[]> input,int dim, int bres){
		ArrayList<int[]> output = new ArrayList<int[]>();
		Stack<int[]> stack = new Stack<int[]>();
		for(int i=input.size()-1;i>=0;i--)
			stack.push(input.get(i));
		while(stack.size()>0){
			int[] region = (int[])stack.pop();
			if(region[1]-region[0]<(1<<dim*bres)){
				output.add(region);
				continue;
			}
			int start = region[0];
			int end = region[1];
			int i=1;
			while(true){
				//区分候補を探す
				int cand = ((start>>(dim*bres))+i)<<(dim*bres);
				//区分候補が終点を超えたら終了
				if(cand>end){output.add(region); break;}
				//ブロック解像度の半分の大きさ
				int half = (1<<(dim*bres-1));
				if(cand-start>half){
					if(end-cand>half){
						output.add(new int[]{start,cand});
						stack.push(new int[]{cand,end});
						break;
					}
					else{
						output.add(region);
						break;
					}
				}
				i++;
			}
		}
		return output;
	}

	public double[][] calcDist(ArrayList<int[]> regions,int nofLabels){
		MDSax mdsax = new MDSax(resolution,dimension,8/Math.pow(2, resolution));
		double distarray[][] = new double[nofLabels][nofLabels];

		for(int i=0;i<nofLabels;i++){
			for(int j=i+1;j<nofLabels;j++){ //j>i
				int[] region1 = regions.get(i);
				int[] region2 = regions.get(j);
				double dist = mdsax.calcdist(region1, region2);
				distarray[i][j]=dist;
			}
		}
		return distarray;
	}

	public void printHistogram(){
		for(int i=0;i<this.histogram.length;i++){
			System.out.println(this.histogram[i]);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int dimension=3;
		int resolution=9;
		int labels=2048;
		int block_resolution=6;
		
		if(args.length==4){
			dimension = new Integer(args[0]);
			resolution = new Integer(args[1]);
			labels = new Integer(args[2]);
			block_resolution = new Integer(args[3]);
		}

		long start = System.currentTimeMillis();

		// TODO Auto-generated method stub
		uSaxLabeler lblr = new uSaxLabeler(dimension,resolution);
		try {
			lblr.generateValues();
		} catch (UnSupportedDimensionException e1) {
			// TODO Auto-generated catch block
			System.out.println("Current version only supports less than 2nd dimension.");
			e1.printStackTrace();
			return;
		}
		ArrayList<int[]> tmp = lblr.makeLabels(labels);
		ArrayList<int[]> regions = lblr.devideLabels(tmp, dimension, block_resolution);
		labels = regions.size();
		double[][] dist = lblr.calcDist(regions,labels);

		long stop = System.currentTimeMillis();

		String filename = "LabelInfo_"+dimension+"_"+resolution+"_"+labels+"_.txt";

		if(dimension == 3){
		try {
			FileWriter outFile = new FileWriter(filename);

			outFile.write("\n# Parameters \n\n");
			outFile.write("dimension,"+dimension+"\n");
			outFile.write("resolution,"+resolution+"\n");
			outFile.write("labels,"+labels+"\n");

			outFile.write("\n# Regions \n\n");

			for(int i=0;i<labels;i++){
				int x = i/64;
				int y = i%64;
				String a = symbolicCode[x]+symbolicCode[y];
				outFile.write(a+","+regions.get(i)[0]+","+regions.get(i)[1]+"\n");
			}
			outFile.write("\n# Distance \n\n");
			for(int i=0;i<labels;i++){
				for(int j=i+1;j<labels;j++){ //j>i
					if(j==labels-1)
						outFile.write(dist[i][j]+"\n");
					else
						outFile.write(dist[i][j]+",");
				}
			}


			outFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		else if(dimension == 4){
			try {
				FileWriter outFile = new FileWriter(filename);

				outFile.write("\n# Parameters \n\n");
				outFile.write("dimension,"+dimension+"\n");
				outFile.write("resolution,"+resolution+"\n");
				outFile.write("labels,"+labels+"\n");

				outFile.write("\n# Regions \n\n");

				for(int i=0;i<labels;i++){
					//char a = (char) ('A'+i);

					int x = i/64;
					int y = i%64;

					String a = symbolicCode[x]+symbolicCode[y];

					outFile.write(a+","+regions.get(i)[0]+","+regions.get(i)[1]+"\n");
				}
				outFile.write("\n# Distance \n\n");
				for(int i=0;i<labels;i++){
					for(int j=i+1;j<labels;j++){ //j>i
						if(j==labels-1)
							outFile.write(dist[i][j]+"\n");
						else
							outFile.write(dist[i][j]+",");
					}
				}


				outFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else{
			try {
				FileWriter outFile = new FileWriter(filename);

				outFile.write("\n# Parameters \n\n");
				outFile.write("dimension,"+dimension+"\n");
				outFile.write("resolution,"+resolution+"\n");
				outFile.write("labels,"+labels+"\n");

				outFile.write("\n# Regions \n\n");

				for(int i=0;i<labels;i++){
					char a = (char) ('A'+i);

					outFile.write(a+","+regions.get(i)[0]+","+regions.get(i)[1]+"\n");
				}
				outFile.write("\n# Distance \n\n");
				for(int i=0;i<labels;i++){
					for(int j=i+1;j<labels;j++){ //j>i
						if(j==labels-1)
							outFile.write(dist[i][j]+"\n");
						else
							outFile.write(dist[i][j]+",");
					}
				}


				outFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		lblr.printHistogram();

		System.out.println("距離表作成にかかった時間は"+ (stop-start) +"ミリ秒です。\n");
	}

}
