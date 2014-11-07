/**
 * Class Hilbert
 *
 *  This class includes a encoding function which transforms a vector
 *  to the value on the Hilbert space-filling curve, and it includes
 *  a decoding function from a Hilbert value to the corresponding vector value.
 *  This program is implemented according to the Butz's Algorithm[1].
 *
 *  [1] A.R.Butz: Alternative Algorithm for Hilbert's Space-Filling Curve,
 *       IEEE Trans. Comp., April, 1971, pp 424-426.
 *
 *  LIMITATION:
 *    Dimension x Resolution should be less than 32.
 *
 * @author chiemi watanabe
 *
 */

public class Hilbert {

	int resolution;
	int dimension;


	public Hilbert(int r,int d){
		this.resolution=r;
		this.dimension=d;
	}

	public int[] calcP(int hcode){
		int[] p = new int[this.resolution];
		int mask = (1<<dimension)-1;
		for(int i=resolution-1;i>=0;i--){
			p[i]=hcode & mask;
			hcode=hcode>>dimension;
		}
		return p;
	}

	public int calcP2(int sigma){
		int mask = 1<<(this.dimension-1);
		int p=0;
		for(int i=0;i<this.dimension;i++){
			p = p | (((sigma & mask ) ^ (p>>>1)) & mask );
			mask = mask>>>1;
		}
		return p;
	}

	public int calcJ(int p){
		int last = (0x01 & p);
		for(int i=1;i<this.dimension;i++){
			if(((p>>>i) & 0x01)!=last){
				return dimension-i;
			}
		}
		return this.dimension;
	}



	public int calcSigma(int p){
		int sigma = ( (p>>>1) ^ p );

		return sigma;
	}

	public int calcTau(int p,int j){

		int c = ((~p)&0x01);
		int mask = ~(0x01);
		int cmp = c | (p & mask);

		int cnt = Integer.bitCount(cmp);
		if(cnt%2==0){
			return cmp;
		}else{
			c = ( ( ( (~cmp) >>> (this.dimension-j)) & 0x01 ) << (this.dimension-j));
			mask = ~( 0x01 << (this.dimension-j) );
			cmp = c | (cmp & mask);
			return cmp;
		}
	}

	public int rotate_right(int sigma,int shift){
		shift = shift % this.dimension;
		if(shift==0) return sigma;

		int mask = (1<<shift)-1;
		int rotated = ((sigma & mask) << (this.dimension - shift));
		return (sigma>>>shift) | rotated;
	}

	public int rotate_left(int sigma,int shift){
		int dim_mask = (1<<this.dimension)-1;
		shift = shift % this.dimension;
		if(shift==0) return sigma;

		//int mask = ((1<<this.dimension)-1) ^ ((1<<shift)-1);
		int mask = ((1<<shift)-1)<<(this.dimension -shift);
		int rotated = ((sigma & mask) >> (this.dimension - shift));
		return ((sigma<<shift)& dim_mask) | rotated;
	}

	public void setBits(int[] a, int alpha,boolean shift){
		for(int i=a.length-1;i>=0;i--){
			a[i]=(a[i] | (alpha & 0x01));
			alpha = alpha>>1;
			if(shift)
				a[i]=(a[i]<<1);
		}
	}

	public int[] decode(int value){
		int[] p = this.calcP(value);
		int[] a = new int[dimension];
		int shift = 0;
		int t_prev=0,w_prev=0;
		for(int i=0;i<p.length;i++){
			int j = this.calcJ(p[i]);
			p[i] = this.calcSigma(p[i]);
			int t = this.calcTau(p[i], j);
			int Sigmanyoro = this.rotate_right(p[i], shift);
			t = this.rotate_right(t, shift);
			shift += (j-1);
			int w = t_prev ^ w_prev;
			int alpha = w ^ Sigmanyoro;
			if(i<p.length-1){
				this.setBits(a, alpha,true);
			}else{
				this.setBits(a, alpha, false);
			}
			t_prev=t;
			w_prev=w;
		}
		return a;
	}

	public int encode(int[] value){
		int hcode = 0;
		int w=0;
		int t_prev=0,w_prev=0;
		int shift=0;
		int[] alpha = new int[this.resolution];
		for(int i=0;i<value.length;i++){
			if(i<value.length-1)
				this.setBits(alpha, value[i], true);
			else
				this.setBits(alpha, value[i], false);
		}

		for(int i=0;i<alpha.length;i++){
			w = t_prev ^ w_prev;
			int sigma2 = alpha[i]^w;
			int sigma = this.rotate_left(sigma2, shift);
			int p = this.calcP2(sigma);
			int j = this.calcJ(p);
			int t = this.calcTau(sigma, j);
			t = this.rotate_right(t, shift);
			shift = shift + (j-1);
			t_prev = t;
			w_prev = w;
			hcode = (hcode << this.dimension ) | p;
		}
		return hcode;
	}


	public static String getBitStream(int x){
		String str="";
		for(int i=0;i<32;i++){
			str=String.valueOf(x&0x01)+str;
			x=x>>>1;
			if(i%8==0) str=str+" ";
		}
		return str;
	}


	//3次元データ用ヒルベルトエンコード関数へのメイン関数
	public static int encode3Dmain(int x, int y, int z, int r){

		Hilbert h = new Hilbert(r,3);

		int[] pos = {x,y,z};

		int hvalue = h.encode(pos);

		return hvalue;

	}

	//2次元データ用ヒルベルトエンコード関数へのメイン関数
	public static int encode2Dmain(int x, int y, int r){

		Hilbert h = new Hilbert(r,2);

		int[] pos = {x,y};

		int hvalue = h.encode(pos);

		return hvalue;

	}


	/**
	 * @param args
	 */

	public static void main(String[] args) {
		/*
		// This example is cited from the Butz's paper.
		int hvalue = 0x0988B8;
		Hilbert h = new Hilbert(4,5);
		int[] ret = h.decode(hvalue);
		for(int i=0;i<ret.length;i++){
			System.out.println("["+i+"]="+h.getBitString(ret[i]));
		}
		hvalue = h.encode(ret);
		System.out.println(h.getBitString(hvalue));
		*/

		//引数1つ目解像度、2つ目次元
		Hilbert h = new Hilbert(3,3);

		for(int i=0;i<Math.pow(8, 3);i++){
			int[] pos=h.decode(i);
			System.out.println("ENCODE: "+i+"->("+pos[0]+","+pos[1]+","+pos[2]+")");
			int hvalue = h.encode(pos);
			System.out.println("DECODE: ("+pos[0]+","+pos[1]+","+pos[2]+")->"+hvalue);
		}

	}

}
