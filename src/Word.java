public class Word {
    /** word */
    private String word;
    /** Number of ppearance in text */
    private int count;
    private int docCount;

    public Word(String word) {
        this.word = word;
        this.count = 1;
        this.docCount = 1;
    }

    public void addDocCount() {
        this.docCount++;
    }

    public String getWord() {
        return word;
    }


    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public int getDocCount() {
        return docCount;
    }

    @Override
    public String toString() {
        return this.word;
    }
}
