import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParamAlgs {
    public static Map<String, Double> bow(ArrayList<Sentence> sentences) {
        Map<String, Double> bow = new HashMap<>();
        Double count;

        for (Sentence s : sentences) {
            for (String w : s.getWords()) {
                count = bow.get(w);

                if (w.length() == 0)
                    continue;

                if (count == null)
                    bow.put(w, 1.0);
                else
                    bow.put(w, count + 1);
            }
        }

        return bow;
    }

    public static Map<String, Double> termFreqInvDocFreq(ArrayList<Sentence> sentences) {
        Map<String, Word> temp = tfIdfFindWords(sentences);
        Map<String, Double> words = new HashMap<>();
        double idf;

        for (Map.Entry<String, Word> entry : temp.entrySet()) {
            idf = idfCompute(sentences.size(), entry.getValue().getDocCount());
            words.put(entry.getKey(), idf);
        }

        return words;
    }

    private static Map<String, Word> tfIdfFindWords(ArrayList<Sentence> sentences) {
        Map<String, Word> map = new HashMap<>();
        Word temp;
        boolean newLine;
        for (Sentence s : sentences) {
            newLine = true;
            for (String w : s.getWords()) {
                temp = map.get(w);

                if (w.length() == 0)
                    continue;

                if (temp == null) {
                    map.put(w, new Word(w));
                    newLine = false;
                } else {
                    Word word = map.get(w);
                    word.setCount(1);

                    if (newLine) {
                        word.addDocCount();
                        newLine = false;
                    }

                    map.put(w, word);
                }
            }
        }
        return map;
    }

    private static double idfCompute(int docSize, int docCount) {
        return Math.log(docSize / (double)docCount);
    }

    /**
     * return term frequency
     * @param count word frequency
     * @param sizeOfDictionary dictionary size
     * @return tf value
     */
    public static double tfCompute(int count, int sizeOfDictionary) {
        return(double)count / (double)sizeOfDictionary;
    }
}
