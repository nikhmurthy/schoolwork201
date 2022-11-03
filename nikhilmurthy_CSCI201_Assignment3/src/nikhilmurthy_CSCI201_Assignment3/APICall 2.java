package nikhilmurthy_CSCI201_Assignment3;

public class APICall {
	private double c;
	private double d;
	private double dp;
	private double h;
	private double l;
	private double o;
	private double pc;
	
	public APICall(double c, double d, double dp, double h, double l, double o, double pc) {
		this.c = c;
		this.d = d;
		this.dp = dp;
		this.h = h;
		this.l = l;
		this.o = o;
		this.pc = pc;
	}

	public double getC() {
		return c;
	}

	public void setC(double c) {
		this.c = c;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public double getDp() {
		return dp;
	}

	public void setDp(double dp) {
		this.dp = dp;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getL() {
		return l;
	}

	public void setL(double l) {
		this.l = l;
	}

	public double getO() {
		return o;
	}

	public void setO(double o) {
		this.o = o;
	}

	public double getPc() {
		return pc;
	}

	public void setPc(double pc) {
		this.pc = pc;
	}

	@Override
	public String toString() {
		return "APICall [price=" + c + ", change=" + d + ", percent change=" + dp + ", high=" + h + ", low=" + l + ", open=" + o + ", prevclose=" + pc
				+ "]\n";
	}
	
	
	
}
