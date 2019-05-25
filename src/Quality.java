public class Quality {
	
	private double TP;
	private double TN;
	private double FP;
	private double FN;
	
	public Quality() {
		this.TP = 0;
		this.TN = 0;
		this.FP = 0;
		this.FN = 0;
	}
	
	public double getTN() {
		return TN;
	}

	public void setTN(double tN) {
		TN = tN;
	}

	public double getTP() {
		return TP;
	}
	public void setTP(double tP) {
		TP = tP;
	}
	public double getFP() {
		return FP;
	}
	public void setFP(double fP) {
		FP = fP;
	}
	public double getFN() {
		return FN;
	}
	public void setFN(double fN) {
		FN = fN;
	}

	public void addTP() {
		this.TP += 1.0;
	}

	public void addFP() {
		this.FP += 1.0;
	}

	public void addTN() {
		this.TN += 1.0;
	}

	public void addFN() {
		this.FP += 1.0;
	}
	
	private double precision() {
		return TP / (TP + FP);
	}

	public void calculateQuality() {
		double precision = Math.round((precision() * 1000.0)) / 1000.0;

		System.out.println("Precision: " + precision);
	}
}
