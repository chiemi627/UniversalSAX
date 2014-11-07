
public class HilbertOrder {
	/**
	 * Find the Hilbert order (=vertex index) for the given grid cell 
	 * coordinates.
	 * @param x cell column (from 0)
	 * @param y cell row (from 0)
	 * @param r resolution of Hilbert curve (grid will have Math.pow(2,r) 
	 * rows and cols)
	 * @return Hilbert order 
	 */
	public static int encode(int x, int y, int r) {

	    int mask = (1 << r) - 1;
	    int hodd = 0;
	    int heven = x ^ y;
	    int notx = ~x & mask;
	    int noty = ~y & mask;
	    int temp = notx ^ y;

	    int v0 = 0, v1 = 0;
	    for (int k = 1; k < r; k++) {
	        v1 = ((v1 & heven) | ((v0 ^ noty) & temp)) >> 1;
	        v0 = ((v0 & (v1 ^ notx)) | (~v0 & (v1 ^ noty))) >> 1;
	    }
	    hodd = (~v0 & (v1 ^ x)) | (v0 & (v1 ^ noty));

	    return interleaveBits(hodd, heven);
	}

	public static int[] decode(int h,int r){
		int[] tmp = deleaveBits(h);
		int heven = tmp[0];
		int hodd = tmp[1];
		
		int mask = (1<<r)-1;
		int v1 = 0;
		int v0 = 0;
		int temp1 = (~(heven | hodd)) & mask;
		int temp0 = (~(heven ^ hodd)) & mask;
		for(int k=1; k<r; k++){
			v1 = (v1^temp1) >> 1;
			v0 = (v0^temp0) >> 1;		
		}
		int[] result = new int[2];
		result[0] = (v0 & (~heven))^v1^hodd;
		result[1] = (v0 | heven)^v1^hodd;
		return result;
	}
	
	/**
	 * Interleave the bits from two input integer values
	 * @param odd integer holding bit values for odd bit positions
	 * @param even integer holding bit values for even bit positions
	 * @return the integer that results from interleaving the input bits
	 *
	 * @todo: I'm sure there's a more elegant way of doing this !
	 */
	private static int interleaveBits(int odd, int even) {
	    int val = 0;
	    int n = Math.max(Integer.highestOneBit(odd), Integer.highestOneBit(even));
	    if(n>31) n=31;

	    for (int i = 0; i < n; i++) {
	        int bitMask = 1 << i;
	        int a = (even & bitMask) > 0 ? (1 << (2*i)) : 0;
	        int b = (odd & bitMask) > 0 ? (1 << (2*i+1)) : 0;
	        val += a + b;
	    }

	    return val;
	}
	
	private static int[] deleaveBits(int val){
		int even=0,odd=0;
		int n = Integer.highestOneBit(val);
		if(n>31) n=31;
		
		for(int i=0;i*2<n;i++){
			if((val & (1<<(2*i))) > 0){
				even |= (1<<i);
			}
			if((val & (1<<(2*i+1))) > 0){
				odd |= (1<<i);
			}
		}
		int[] res = {even,odd};
		return res;
	}
	
	public static void main(String args[]){

		/*
		int r=2;
		for(int i=0;i<Math.pow((double)2, (double)2*r);i++){
			int[] v = HilbertOrder.decode(i,r);
			System.out.println("i="+i+" x="+v[0]+" y="+v[1]);
		}
	}*/
	
		int r=7;
		for(int i=0;i<Math.pow((double)2, (double)r);i++){
			for(int j=0;j<Math.pow((double)2, (double)r);j++){				
				int h = HilbertOrder.encode(i,j,r);
				System.out.println("x="+i+",y="+j+",h="+h);
			}
		}
	}
}
	
