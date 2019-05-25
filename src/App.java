import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class App {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Arguments missing!");
            info();
            return;
        }
        FileReader file;
        FileReader file2;
        Quality quality = new Quality();
        EntityType et;
        EntityType ret;
        Map<String, Double> paramOutput = new HashMap<>();
        Map<EntityType, Cluster> detectOutput = new HashMap<>();
        int paramAlg = resolveParamAlg(args[1].toLowerCase());
        int detectAlg = resolveDetectionAlg(args[2].toLowerCase());

        if (paramAlg == 2 || detectAlg == 2) {
            System.out.println("Arguments mismatch!");
            info();
            return;
        }

        try {
            file = new FileReader(args[0]);
            file2 = new FileReader("resources/mydata.conll");
        } catch (IOException e) {
            System.out.println("File not found.");
            return;
        }

        ArrayList<Sentence> text = readText(file);
        ArrayList<Sentence> text2 = readText(file2);

        // param alg
        switch (paramAlg) {
            case 0:
                paramOutput = ParamAlgs.bow(text);

                for (Sentence s : text) {
                    s.setVector(s.createBowVector(paramOutput));
                    s.setRealType();
                }
                break;
            case 1:
                paramOutput = ParamAlgs.termFreqInvDocFreq(text);
                for (Sentence s : text) {
                    s.setVector(s.createTfidfVector(paramOutput));
                    s.setRealType();
                }
                break;
        }

        // detekcni alg
        switch (detectAlg) {
            case 0:
                detectOutput = DetectionAlgs.kMeans(text, paramOutput.size());
                break;
            case 1:
                detectOutput = DetectionAlgs.oneNN(text, text2, paramOutput, paramAlg);
                break;

        }

        for (EntityType ets : EntityType.values()) {
            for (int i = 0; i < detectOutput.get(ets).getSentences().size(); i++) {
                detectOutput.get(ets).getSentences().get(i).setEntityType(ets);
                et = detectOutput.get(ets).getSentences().get(i).getType();
                ret = detectOutput.get(ets).getSentences().get(i).getRealType();

                if (et != EntityType.NOT_ENTITY) {
                    if (ret != EntityType.NOT_ENTITY) {
                        if (et == ret)
                            quality.addTP();
                        else
                            quality.addTN();
                    } else {
                        quality.addFN();
                    }
                } else {
                    if (ret == EntityType.NOT_ENTITY)
                        quality.addFP();
                    else
                        quality.addTN();
                }
            }
        }

        try {
            saveData(detectOutput, args[1], args[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        quality.calculateQuality();
    }

    /**
     * Read, process and save file text
     *
     * @param file {FileReader} file with text
     * @return {ArrayList<Sentence>} File text
     */
    private static ArrayList<Sentence> readText(FileReader file) {
        ArrayList<Sentence> text = new ArrayList<>();
        BufferedReader reader;
        Sentence s;
        try {
            reader = new BufferedReader(file);
            String line = reader.readLine();
            String[] lineWords;
            s = new Sentence();

            while (line != null) {
                if (line.length() > 1) {
                    lineWords = line.split(" ");
                    s.addWord(lineWords[0]);
                    s.addRealEntityType(resolveWordEntity(lineWords[3]));
                } else {
                    s.setRealType();
                    text.add(s);
                    s = new Sentence();
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return text;
        }

        return text;
    }

    /**
     * Process EntityType from string
     *
     * @param entity {String} specifying entity type
     * @return {EntityType}
     */
    private static EntityType resolveWordEntity(String entity) {
        String[] attr = entity.split("-");

        if (attr.length > 1)
            for (EntityType et : EntityType.values())
                if (et.getShortcut().equals(Character.toString(attr[1].toCharArray()[0])))
                    return et;

        return EntityType.NOT_ENTITY;
    }

    /**
     * Resolve param algorithm from String
     *
     * @param alg algorithm
     * @return 0 bow alg, 1 tf-idf alg, 2 otherwise
     */
    private static int resolveParamAlg(String alg) {
        if (alg.equals("bow"))
            return 0;

        if (alg.equals("tfidf"))
            return 1;

        return 2;
    }

    /**
     * Resolve detection algorithm from String
     *
     * @param alg algorithm
     * @return 0 k-means, 1 One-NN, 2 otherwise
     */
    private static int resolveDetectionAlg(String alg) {
        if (alg.equals("kmeans"))
            return 0;

        if (alg.equals("onenn"))
            return 1;

        return 2;
    }

    /**
     * Save annotated data into a file
     *
     * @param annotatedData annotated data
     * @param paramAlg pamarm alg name
     * @param detectAlg detection alg name
     * @throws IOException
     */
    private static void saveData(Map<EntityType, Cluster> annotatedData, String paramAlg, String detectAlg) throws IOException {
        File f = new File("results-" + paramAlg + "-" + detectAlg + ".txt");
        FileWriter fw = new FileWriter(f.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("detect alg - real - sentence\n");
        for (EntityType et : EntityType.values()) {
            for (Sentence s : annotatedData.get(et).getSentences()) {
                bw.write(s.getType().toString() + " - " + s.getRealType() +
                        " - " + s.toString() + "\n");
            }
        }

        bw.close();
    }

    /**
     * print how to use this application
     */
    private static void info() {
        System.out.println("Usage:");
        System.out.println(" 1st param: Source file");
        System.out.println(" 2nd param: Parametrization algorithm");
        System.out.println(" 3rd param: Detection algorithm");
        System.out.println("Parametrization algorithms: bow, tfidf");
        System.out.println("Detection algorithms: kmeans, onenn");
        System.out.println("Usage example:");
        System.out.println(" resources/test.conll bow kmeans");
    }
}
