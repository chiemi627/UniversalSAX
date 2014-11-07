
public class Hcode {


	static int DIM = 2;

    public int hcode[] = new int[DIM];

    Hcode() {
            for (int i = 0; i < hcode.length; i++) {
                    hcode[i] = 0;
            }
    }

    Hcode(Hcode h) {
            for (int i = 0; i < hcode.length; i++) {
                    hcode[i] = h.hcode[i];
            }
    }
}
