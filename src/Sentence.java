import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Sentence {
    /** Words representing sentence*/
    private ArrayList<String> words = new ArrayList<>();
    /** Real sentence entity types */
    private ArrayList<EntityType> realTypes = new ArrayList<>();
    /** Sentence entity type */
    private EntityType type = EntityType.NOT_ENTITY;
    /** Real sentence entity type */
    private EntityType realType = EntityType.NOT_ENTITY;
    /** Bow vector*/
    private double[] bowVector;
    /** Tf-idf vector*/
    private double[] tfidfVector;
    /** Vector */
    private double[] vector;

    /**
     * Add word to the end of a sentence
     *
     * @param word word
     */
    public void addWord(String word) {
        word = processWord(word);
        if (word.length() == 0)
            return;

        this.words.add(word);
    }

    /**
     * Make word lowercase and remove invalid chars
     *
     * @param w word
     * @return lowercase word
     */
    private String processWord(String w) {
        w = w.toLowerCase();

        w = w.replace("@", "");
        w = w.replace("#", "");
        w = w.replace(".", "");
        w = w.replace(":", "");
        w = w.replace("(", "");
        w = w.replace(")", "");
        w = w.replace("'", "");
        w = w.replace("„", "");
        w = w.replace("?", "");
        w = w.replace("!", "");
        w = w.replace(",", "");
        w = w.replace("*", "");
        w = w.replace("“", "");
        w = w.replace("~", "");
        w = w.replace("…", "");
        w = w.replace("\"","");
        w = w.replace("\\","");
        w = w.replace("/", "");
        w = w.replace("-", "");
        w = w.replace("&", "");
        w = w.replace("|", "");
        w = w.replace("+", "");
        w = w.replace("–", "");
        w = w.replace(">", "");
        w = w.replace("<", "");
        w = w.replace("‚", "");
        w = w.replace("%", "");
        w = w.replace("😂", "");
        w = w.replace("‚", "");
        w = w.replace("‘", "");
        w = w.replace("👏", "");
        w = w.replace("_", "");
        w = w.replace("🤔", "");
        w = w.replace("😝", "");
        w = w.replace("👀", "");
        w = w.replace("☺", "");
        w = w.replace("Ě", "ě");
        w = w.replace("Č", "š");
        w = w.replace("Ř", "ř");
        w = w.replace("Ž", "ž");
        w = w.replace("Ý", "ý");
        w = w.replace("Á", "á");
        w = w.replace("Í", "í");
        w = w.replace("É", "é");
        w = w.replace("Ť", "ť");
        w = w.replace("Ú", "ú");
        w = w.replace("Ů", "ů");
        w = w.replace("Ď", "ď");
        w = w.replace("Ó", "ó");
        w = w.replace("Ň", "ň");

        return w;
    }

    public void addRealEntityType(EntityType e) {
        if (!this.realTypes.contains(e))
            this.realTypes.add(e);
    }

    public void setEntityType(EntityType e) {
        this.type = e;
    }

    public void setRealType() {
        Map<EntityType, Integer> temp = new HashMap<>();
        Integer max = 0;

        for (EntityType et : realTypes) {
            if (temp.containsKey(et))
                temp.replace(et, temp.get(et) + 1);
            else
                temp.put(et, 1);
        }

        for (int i = 0; i < realTypes.size(); i++) {
            if (realTypes.get(i).equals(EntityType.NOT_ENTITY))
                realTypes.remove(realTypes.get(i));
        }

        for (EntityType et : realTypes) {
            if (max < temp.get(et)) {
                max = temp.get(et);
                this.realType = et;
            }
        }
    }

    public void createVectors(Map<String, Double> bow, Map<String, Double> tfids) {
        int wordCount = this.words.size();
        this.bowVector = createBowVector(bow);
        this.tfidfVector = createTfidfVector(tfids);
    }

    public double[] createTfidfVector(Map<String, Double> tfids) {
        double[] vector = new double[tfids.size()];
        double[] idf = new double[tfids.size()];
        int realTextSize = 0;
        int i = 0;
        for (Map.Entry<String, Double> entry : tfids.entrySet()){
            if (this.words.size() == 0)
                break;

            for (String w : this.words) {
                if (entry.getKey().equals(w) ) {
                    realTextSize++;
                    vector[i] += 1.0;
                    idf[i] = entry.getValue();
                }
            }
            i++;
        }

        for (i = 0; i < tfids.size(); i++)
            vector[i] = ParamAlgs.tfCompute((int)vector[i], realTextSize) * idf[i];

        return vector;
    }

    public double[] createBowVector(Map<String, Double> bow) {
        double[] vector = new double[bow.size()];
        int i = 0;
        for (Map.Entry<String, Double> entry : bow.entrySet()) {
            if (this.words.size() == 0)
                break;

            for (String w : this.words) {
                if (entry.getKey().equals(w))
                    vector[i] += 1.0;
            }
            i++;
        }
        
        return vector;
    }

    public void setVector(double[] vector) {
        this.vector = vector;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public EntityType getRealType() {
        return this.realType;
    }

    public EntityType getType() {
        return type;
    }

    public double[] getBowVector() {
        return bowVector;
    }

    public double[] getTfidfVector() {
        return tfidfVector;
    }

    public double[] getVector() {
        return vector;
    }

    @Override
    public String toString() {
        StringBuilder sent = new StringBuilder();
        int wordNum = words.size() - 1;
        for (int i = 0; i < wordNum; i++)
            sent.append(words.get(i)).append(" ");
        sent.append(words.get(wordNum));

        return sent.toString();
    }
}
