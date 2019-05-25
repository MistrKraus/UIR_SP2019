import java.util.*;

public class DetectionAlgs {
    /**
     * sorts data into classes
     *
     * @param sentences data
     * @param wordCount number of words in data
     * @return data sorted into classes
     */
    public static Map<EntityType, Cluster> kMeans(ArrayList<Sentence> sentences, int wordCount) {
        Map<EntityType, Cluster> annotatingSentences = new HashMap<>();
        ArrayList<Integer> sentenceId = new ArrayList<>();
        ArrayList<Double> distances = new ArrayList<>();
        EntityType[] types = EntityType.values();
        Random r = new Random();
        boolean flag = true;
        int[] sizes = new int[EntityType.values().length];
        int sentencesCount = sentences.size();
        int num;
        int entityTypeId;
        Sentence tmp;
        
        for (EntityType et : EntityType.values()) {
            annotatingSentences.put(et, new Cluster(wordCount));

            num = r.nextInt(sentencesCount);
            while (sentenceId.contains(num))
                num = r.nextInt(sentencesCount);
            sentenceId.add(num);

            annotatingSentences.get(et).getSentences().add(sentences.get(num));
            annotatingSentences.get(et).computeCenter();

            sentences.remove(num);
            sentencesCount--;
        }

        for (Sentence s : sentences) {
            for (EntityType et : EntityType.values()) {
                distances.add(euclideanDistance(s.getVector(),
                        annotatingSentences.get(et).getCenter()));
            }
            entityTypeId = distances.indexOf(getMin(distances));
            annotatingSentences.get(EntityType.values()[entityTypeId]).getSentences().add(s);
            annotatingSentences.get(EntityType.values()[entityTypeId]).computeCenter();

            distances.clear();
        }

        int x = 0;
        while(flag) {
            for (int i = 0; i < sizes.length; i++)
                sizes[i] = annotatingSentences.get(types[i]).getSentences().size();

            for (EntityType et : EntityType.values()) {
                ArrayList<Sentence> tmpList = annotatingSentences.get(et).getSentences();
                for (int i = 0; i < tmpList.size(); i++) {
                    tmp = tmpList.get(i);
                    for (EntityType ett : EntityType.values()) {
                        distances.add(euclideanDistance(tmp.getVector(),
                                        annotatingSentences.get(ett).getCenter()));
                    }
                    entityTypeId = distances.indexOf(getMin(distances));

                    annotatingSentences.get(EntityType.values()[entityTypeId]).getSentences().add(tmp);
                    annotatingSentences.get(EntityType.values()[entityTypeId]).computeCenter();
                    annotatingSentences.get(et).getSentences().remove(tmp);
                    annotatingSentences.get(et).computeCenter();
                    distances.clear();
                }
            }

            flag = false;
            for(int i = 0; i < sizes.length; i++) {
                if (sizes[i] != annotatingSentences.get(types[i]).getSentences().size()) {
                    flag = true;
                    break;
                }
            }
            if (x++ > 100)
                flag = false;
        }

        return annotatingSentences;
    }

    /**
     * Sort data into classes
     *
     * @param sentences data
     * @param controlSentences teacher data
     * @param paramOut processed data
     * @param paramAlg param lgs
     * @return data sorted into classes
     */
    public static Map<EntityType, Cluster> oneNN(ArrayList<Sentence> sentences, ArrayList<Sentence> controlSentences,
                                             Map<String, Double> paramOut, int paramAlg) {
        Map<EntityType, Cluster> annotatingSentences = new HashMap<>();
        ArrayList<Double> distances = new ArrayList<>();
        int entityTypeId;

        for(EntityType et : EntityType.values()) {
            annotatingSentences.put(et, new Cluster(paramOut.size()));
        }

        for(Sentence s : controlSentences) {
            switch (paramAlg) {
                case 0:
                    s.setVector(s.createBowVector(paramOut));
                    break;
                case 1:
                    s.setVector(s.createTfidfVector(paramOut));
                    break;
            }
            s.setRealType();
            annotatingSentences.get(s.getRealType()).getSentences().add(s);
        }

        for(EntityType et : EntityType.values())
            annotatingSentences.get(et).computeCenter();

        for(Sentence s : sentences) {
            switch (paramAlg) {
                case 0:
                    s.setVector(s.createBowVector(paramOut));
                    break;
                case 1:
                    s.setVector(s.createTfidfVector(paramOut));
                    break;
            }

            for(EntityType et: EntityType.values()) {
                distances.add(euclideanDistance(s.getVector(),
                        annotatingSentences.get(et).getCenter()));
            }
            entityTypeId  = distances.indexOf(getMin(distances));
            annotatingSentences.get(EntityType.values()[entityTypeId]).getSentences().add(s);
            annotatingSentences.get(EntityType.values()[entityTypeId]).computeCenter();

            distances.clear();
        }

        for(int i = 0; i < controlSentences.size(); i++) {
            Sentence s = controlSentences.get(i);
            for(int h = 0; h < annotatingSentences.get(s.getRealType()).getSentences().size(); h++) {
                if(s.toString().equals(annotatingSentences.get(s.getRealType()).getSentences().get(h).toString())) {
                    annotatingSentences.get(s.getRealType()).getSentences().remove(h);
                    break;
                }
            }
        }

        return annotatingSentences;
    }

    /**
     * Calculat euclidean distance between two vectors
     *
     * @param vec1 vector one
     * @param vec2 vector two
     * @return euclidean distance
     */
    private static double euclideanDistance(double[] vec1, double[] vec2) {
        double distance = 0.0;

        for(int i = 0; i < vec1.length; i++)
            distance += Math.pow((vec1[i] - vec2[i]), 2);

        distance = Math.sqrt(distance);
        distance /= vec1.length;
        return distance;
    }

    /**
     * finds min from arraylist
     *
     * @param list
     * @return min
     */
    private static double getMin(ArrayList<Double> list) {
        double min = Double.MAX_VALUE;
        double number;

        for (Double aList : list) {
            number = aList;
            if (min > number)
                min = number;
        }

        return min;
    }
}
