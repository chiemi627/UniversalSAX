import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class universalSaxVisualizer  extends JPanel{

	ArrayList<LabelInfo> labelinfo = new ArrayList<LabelInfo>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    JFrame frame = new JFrame();

	    universalSaxVisualizer app = new universalSaxVisualizer();
	    frame.getContentPane().add(app);

	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setBounds(0, 0, 620, 620);
	    frame.setTitle("universalSaxVisualizer");
	    frame.setVisible(true);
	}

	  public void paintComponent(Graphics g){

		  int dimension = 2;
		  int resolution = 8;
		  int labels = 65;

		  this.readLabelInfo("LabelInfo_"+dimension+"_"+resolution+"_"+labels+"_.txt");

		  Graphics2D g2 = (Graphics2D)g;
		  g2.setColor(Color.black);
		  g2.draw(new Line2D.Double(50, 50, 550, 50));
		  g2.draw(new Line2D.Double(550, 50, 550, 550));
		  g2.draw(new Line2D.Double(550, 550, 50, 550));
		  g2.draw(new Line2D.Double(50, 550, 50, 50));
		    //g.fillRect(40, 20, 200, 120);
		    //g.setColor(Color.blue);
		    //g.drawString("Hello Java2D", 10, 50);

		  /*
		  for(int i=0;i<this.labelinfo.size();i++){
			  int[] region = this.labelinfo.get(i).region;
			  g.setColor(c)
		  }
		  */
		  double interval = (double)(500/(Math.pow(2, resolution)+1));

		  int hvalue=0;
		  for(int rno=0;rno<labels;rno++){
			  int[] region = this.labelinfo.get(rno).region;
//			  int h = ((int)(Math.floor(360/labels))*rno*2)%360;
			  int h = hvalue % 360;
			  int s = 255;
			  int v = 255;
			  int[] rgb = HSVtoRGB(h,s,v);
			  g.setColor(new Color(rgb[0],rgb[1],rgb[2]));
			  if(rno==labels-1)region[1]=region[1]+1;
			  for(int i=region[0];i<region[1];i++){
				  int[] value = HilbertOrder.decode(i,resolution);
				  double x = value[0]*interval+(0.5*interval)+50;
				  double y = value[1]*interval+(0.5*interval)+50;
				  g.fillRect((int)x, (int)y, (int)interval, (int)interval);
			  }
			  hvalue+=130;
		  }

		  /*
		  g.setColor(Color.gray);

		  double px=-1,py=-1;
		  for(int i=0;i<Math.pow((double)2, (double)2*resolution);i++){
				int[] v = HilbertOrder.decode(i,resolution);
				double x = (double)v[0]*interval+interval+50;
				double y = (double)v[1]*interval+interval+50;
				if(px>0 && py>0){
					  g2.draw(new Line2D.Double(px, py, x, y));
				}
				px=x; py=y;
		  }
		  */



	  }

	    public int[] HSVtoRGB(int h, int s, int v){
	        float f;
	        int i, p, q, t;
	        int[] rgb = new int[3];

	        i = (int)Math.floor(h / 60.0f) % 6;
	        f = (float)(h / 60.0f) - (float)Math.floor(h / 60.0f);
	        p = (int)Math.round(v * (1.0f - (s / 255.0f)));
	        q = (int)Math.round(v * (1.0f - (s / 255.0f) * f));
	        t = (int)Math.round(v * (1.0f - (s / 255.0f) * (1.0f - f)));

	        switch(i){
	            case 0 : rgb[0] = v; rgb[1] = t; rgb[2] = p; break;
	            case 1 : rgb[0] = q; rgb[1] = v; rgb[2] = p; break;
	            case 2 : rgb[0] = p; rgb[1] = v; rgb[2] = t; break;
	            case 3 : rgb[0] = p; rgb[1] = q; rgb[2] = v; break;
	            case 4 : rgb[0] = t; rgb[1] = p; rgb[2] = v; break;
	            case 5 : rgb[0] = v; rgb[1] = p; rgb[2] = q; break;
	        }

	        return rgb;
	    }

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

}
