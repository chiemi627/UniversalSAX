import java.util.ArrayList;

/**
 * �q���x���g�Ȑ���̋�Ԃł���킳�ꂽ��������ԏ�̓�̗̈�̋��������߂邽�߂̃N���X
 * @author Chiemi Watanabe
 * 
 */
public class MDSax {

	public int resolution;
	public int dimension;
	public double interval;
	
	public MDSax(int r,int d,double i){
		this.resolution = r;
		this.dimension = d;
		this.interval = i;
	}
	

	/**
	 * �q���x���g�Ȑ���̋�Ԃŕ\���ꂽ2�̑�������ԏ�̗̈�̍ŏ����������߂�
	 * @param region1 �q���x���g�Ȑ���̋�Ԃł���킳�ꂽ�̈�
	 * @param region2 �q���x���g�Ȑ���̋�Ԃł���킳�ꂽ�̈�
	 * @return �ŏ�����
	 */
	public double calcdist(int[] region1,int[] region2){
		ArrayList<int[]> boxes1 = this.putBoxes(region1[0], region1[1]);
		ArrayList<int[]> boxes2 = this.putBoxes(region2[0], region2[1]);
		if(boxes1==null || boxes2==null){
			System.out.println("error");
		}
		return calcdist_boxes(boxes1,boxes2);
	}

	/**
	 * �q���x���g�Ȑ���̋�Ԃŕ\���ꂽ��������ԏ�̗̈�𑽎�����ԏ�̋�`�̏W���ɕ�������
	 * @param v1 �q���x���g�Ȑ���̋�Ԃ̍ŏ��l
	 * @param v2 �q���x���g�Ȑ���̋�Ԃ̍ő�l
	 * @return ��`�̏W��
	 */
	public ArrayList<int[]> putBoxes(int v1,int v2){
		int i=1,j;
		v2++;
		int diff = v2-v1;
		int keta=2*dimension;
		
		if(diff<=0) return null;
		int prev=v1;
		ArrayList<int[]> BoxList = new ArrayList<int[]>();
			
		while(diff>=(1<<(dimension*i))){
			int next = ((v1>>(dimension*i))+1)<<(dimension*i);
			if(next>prev){
				int tmp=prev;
				while(prev<next){
					tmp=prev+(1<<(dimension*(i-1)));
					int[] nbox={prev,tmp-1};
					BoxList.add(nbox);
					prev=tmp;
				}
				prev=next;
			}
			i++;
		}
		
		j=i-1;
		while(j>=0){
			int next = ((v2>>(dimension*j)))<<(dimension*j);
			if(next>prev){			
				int tmp=prev;
				while(prev<next){
					tmp=prev+(1<<(dimension*j));
					int[] nbox={prev,tmp-1};
					BoxList.add(nbox);
					prev=tmp;
				}
				prev=next;
			}
			j--;
		}
		return BoxList;
	}	
	
	
	/**
	 * �q���x���g�Ȑ���̋�Ԃł���킳�ꂽ��̋�`�̍ŏ����������߂�B
	 * ���̋�Ԃ�putBoxes���\�b�h�ŋ��߂�ꂽ��`�łȂ���΂Ȃ�Ȃ��B
	 * @param b1 ��̗̈�̃q���x���g�l�i[0]���ŏ��l�C[1]���ő�l)
	 * @param b2 ������̗̈�̃q���x���g�l�i[0]���ŏ��l�C[1]���ő�l)
	 * @return ����
	 */
	public double calcdist_hilbert(int[] b1,int[] b2){
		int[] b1_min = HilbertOrder.decode(b1[0],resolution);
		int[] b1_max = HilbertOrder.decode(b1[1],resolution);
		int[] b2_min = HilbertOrder.decode(b2[0],resolution);
		int[] b2_max = HilbertOrder.decode(b2[1],resolution);

		return calcdist_euclid(b1_min,b1_max,b2_min,b2_max);
	}

	/**
	 * ��`�ɕ��������̈�Ԃ̍ŏ����������߂�
	 * @param BoxList_R1
	 * @param BoxList_R2
	 * @return�@����
	 */
	public double calcdist_boxes(ArrayList<int[]> BoxList_R1,ArrayList<int[]> BoxList_R2){
		double dist=-1;
		for(int i=0;i<BoxList_R1.size();i++){
			for(int j=0;j<BoxList_R2.size();j++){
				double dcand = calcdist_hilbert(BoxList_R1.get(i),BoxList_R2.get(j));
				if(dist<0) dist = dcand;
				else if(dcand<dist) dist = dcand;
			}	
		}
		return dist;
	}	

	/**
	 * ��������ԏ�̓�̋�`�̊Ԃ̋��������߂�
	 * @param b1_min ��������ԏ��1�ڂ̋�`�̍ŏ��l
	 * @param b1_max�@��������ԏ��1�ڂ̋�`�̍ő�l
	 * @param b2_min�@��������ԏ��2�ڂ̋�`�̍ŏ��l
	 * @param b2_max�@��������ԏ��2�ڂ̋�`�̍ő�l
	 * @return
	 */
	public double calcdist_euclid(int[] b1_min,int[] b1_max,int[] b2_min,int[] b2_max){
		int dim = b1_min.length;
		double dist_square = 0;
		for(int i=0;i<dim;i++){
			if(b1_max[i]<b2_min[i]){
				dist_square += (b2_min[i]*(interval)-(b1_max[i]+1)*(interval));
			}else if(b2_max[i]<b1_min[i]){
				dist_square += (b1_min[i]*(interval)-(b2_max[i]+1)*(interval));
			}
		}
		return Math.sqrt((double)dist_square);
	}	
}
