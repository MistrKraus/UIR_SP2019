import java.util.ArrayList;

public class Cluster {
    /** center of all sentences */
    private double[] center;
    /** Size of center vector */
    private int vectorSize;
    /** List of sentences in cluster */
    private ArrayList<Sentence> sentences = new ArrayList<>();

    public Cluster(int vectorSize) {
        this.vectorSize = vectorSize;
        center = new double[vectorSize];
    }

//    public void computeCenter(int paramAlg) {
    public void computeCenter() {
        double[] newCenter = new double[vectorSize];
        double top;
        double bottom = sentences.size();

        if (sentences.size() > 0) {
            for (int i = 0; i < vectorSize; i++) {
                top = 0.0;
                for (Sentence s : sentences) {
                    top += s.getVector()[i];
                }
                newCenter[i] = top / bottom;
            }
        }
        setCenter(newCenter);
    }

    public void setCenter(double[] center) {
        this.center = center;
    }

    public double[] getCenter() {
        return center;
    }

    public ArrayList<Sentence> getSentences() {
        return sentences;
    }
}
