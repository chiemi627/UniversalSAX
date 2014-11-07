
public class BitArray {

	byte[] array = null;
	
	public BitArray(int length){
		array = new byte[length];
	}
	
	public BitArray(BitArray tmp){
		array = new byte[tmp.array.length];
		for(int i=0;i<tmp.array.length;i++){
			this.array[i]=tmp.array[i];
		}
	}
	
	public BitArray copy(BitArray tmp){
		return new BitArray(tmp);
	}
	
	public BitArray shiftL(int num){
		BitArray result = new BitArray(this.array.length);
		if(num>=this.array.length*8)
			return result;
		if(num==0) return new BitArray(this);
			
		int ano = (int) (Math.floor(num/8));
		int bno = num%8;
		
		if(ano>0){
			for(int i=this.array.length-1;i-ano>=0;i--){
				result.array[i-ano]=this.array[i];
			}
			for(int i=this.array.length-ano;i<this.array.length-1;i++)
				result.array[i]=0;
		}
		
		for(int i=0;i<this.array.length-1;i++){
			result.array[i]=(byte) ((result.array[i]<<bno)|(result.array[i+1]>>(bno+1)));
		}
		result.array[this.array.length-1]=(byte) (result.array[this.array.length-1]<<bno);
		
		return result;
	}
	
	public BitArray shiftR(int num){
		BitArray result = new BitArray(this.array.length);
		if(num>=this.array.length*8)
			return result;
		if(num==0) return this;
			
		int ano = (int) (Math.floor(num/8));
		int bno = num%8;
		
		if(ano>0){
			for(int i=0;i<ano;i++)
				result.array[i]=0;
			for(int i=0;(i+ano)<this.array.length;i++){ //i+ano<length
				result.array[i+ano]=this.array[i];
			}
		}
		for(int i=this.array.length-1;i>0;i--){
			result.array[i]=(byte) ((result.array[i]>>bno)|((result.array[i-1]&((1<<bno)-1))<<(8-bno)));
			result.array[i-1]=(byte) (result.array[i-1]>>bno);
		}
		return result;		
	}
	
	public static BitArray and(BitArray a,BitArray b){
		if(a.array.length!=b.array.length) return null;
		BitArray result = new BitArray(a.array.length);
		for(int i=0;i<a.array.length;i++){
			result.array[i] = (byte) (a.array[i] & b.array[i]);
		}
		return result;
	}
	
	public static int and(BitArray a,int b){
		int result;
		int a_int = 0;
		
		for(int i=0;i<4;i++){
			a_int |= a.array[a.array.length-1-i]<<(i*8);
		}
		return a_int & b;
	}
	
	public static BitArray or(BitArray a,BitArray b){
		if(a.array.length!=b.array.length) return null;
		BitArray result = new BitArray(a.array.length);
		for(int i=0;i<a.array.length;i++){
			result.array[i] = (byte) (a.array[i] | b.array[i]);
		}
		return result;
	}
	
	public static BitArray or(BitArray a,int b){
		BitArray result = new BitArray(a.array.length);
		for(int i=0;i<a.array.length-1;i++){
			if(i<4){
				result.array[a.array.length-1-i]=(byte) (a.array[a.array.length-1-i]|(( b&( ((1<<8)-1)<<(i*8) ) )>>(i*8)));
			}else{
				result.array[a.array.length-1-i]=a.array[a.array.length-1-i];
			}
		}
		return result;
	}
	
	public void setIntValue(int b){
		for(int i=0;i<this.array.length-1;i++){
			if(i<4)
				this.array[this.array.length-1-i]=(byte) (this.array[this.array.length-1-i]|(( b&( ((1<<8)-1)<<(i*8) ) )>>(i*8)));
			else
				this.array[this.array.length-1-i]=0;
		}		
	}
	
	public static BitArray xor(BitArray a,BitArray b){
		if(a.array.length!=b.array.length) return null;
		BitArray result = new BitArray(a.array.length);
		for(int i=0;i<a.array.length;i++){
			result.array[i] = (byte) (a.array[i] ^ b.array[i]);
		}
		return result;
	}
	
	public static int xor(BitArray a,int b){
		int a_int = 0;		
		for(int i=0;i<4;i++){
			a_int |= a.array[a.array.length-1-i]<<(i*8);
		}
		return a_int ^ b;
	}
	
	public int compareTo(BitArray a){
		if(a.array.length!=this.array.length){
			System.err.println("please compare to the bitarray which has the same length bit array");
			System.exit(-1);
		}
		for(int i=0;i<a.array.length;i++){
			if(this.array[i]!=a.array[i])
				return this.array[i]-a.array[i];
		}
		return 0;
	}
	
	public int compareTo(int a){
		for(int i=0;i<this.array.length-4;i++){
			if(this.array[i]>0) return 1;
		}
		int a_int = 0;		
		for(int i=0;i<4;i++){
			a_int |= this.array[this.array.length-1-i]<<(i*8);
		}
		return a_int - a;
		
	}
	
	public boolean isOdd(){
		if((this.array[this.array.length-1]&1)==1)
			return true;
		else
			return false;
	}
	
	public String toString(){
		String str="";
		for(int i=0;i<this.array.length;i++){
			for(int j=0;j<8;j++)
				str+=""+((this.array[i]>>(7-j))&1);
			str+=" ";
		}
		return str;
	}
		
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BitArray t1=new BitArray(4);
		t1.setIntValue(5);
		System.out.println(t1.toString());
		
		BitArray t2 = t1.shiftL(25);
		System.out.println(t2.toString());		
		
		BitArray t3 = t2.shiftR(8);
		System.out.println(t3.toString());		
		
		BitArray t4 = BitArray.or(t3, t2);
		System.out.println(t4.toString());
		
		BitArray t5 = BitArray.and(t4, t2);
		System.out.println(t5.toString());		
		
		System.out.println(t5.compareTo(t4));
		System.out.println(t1.isOdd());
		System.out.println(t2.isOdd());
		
		BitArray t6 = BitArray.xor(t4, t5);
		System.out.println(t6.toString());		
		
	}

}
