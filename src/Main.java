import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

        int kn = Integer.parseInt(args[0]);
        String pathtrain = args[1];
        String pathtest = args[2];
        int numofnumbers = 0;

        FileReader fr = new FileReader(pathtrain);
        BufferedReader br = new BufferedReader(fr);

        HashMap<String, ArrayList<ArrayList<Float>>> map = new HashMap<>();

        while (br.ready())
        {
            String[] line = br.readLine().split(",");
            ArrayList<Float> list = new ArrayList<Float>();
            for(int i=0;i<line.length-1;i++) {
                list.add(Float.parseFloat(line[i]));
            }
            map.computeIfAbsent(line[line.length-1], k -> new ArrayList<>()).add(list);
        }

        for (var entry : map.entrySet()) {
            String key = entry.getKey();
            ArrayList<ArrayList<Float>> value = entry.getValue();
            System.out.println("Key: " + key + ", Value: " + value);
        }

        FileReader fr2 = new FileReader(pathtest);
        BufferedReader br2 = new BufferedReader(fr2);

        int all=0;
        int hit=0;

        while (br2.ready())
        {
            String[] line = br2.readLine().split(",");
            all++;
            numofnumbers = line.length-1; // to jest potrzebne potem do wpisywania wlasnych punktow
            ArrayList<Float> list = new ArrayList<Float>();
            for(int i=0;i<line.length-1;i++) {
                list.add(Float.parseFloat(line[i]));
            }
            String key = line[line.length-1];

            String mostFrequent = classifyPoint(map, kn, list);

            if(key.equals(mostFrequent)) {
                hit++;
            }

        }
        System.out.println("accuracy: "+hit/(all*1.0)*100 + "% for knn = "+kn);

        Scanner scanner = new Scanner(System.in);

        while(true)
        {
            System.out.println("what do you want to do next?");
            System.out.println("1. classify new point");
            System.out.println("any other number. exit");
            System.out.print("provide a number: ");
            int liczba = scanner.nextInt();
            if(liczba==1) {
                ArrayList<Float> newpoint = new ArrayList<>();
                System.out.println("enter knn: ");
                int knn = scanner.nextInt();
                System.out.println("enter new point: ");
                for(int i=0;i<numofnumbers;i++) {
                    float temp = scanner.nextFloat();
                    newpoint.add(temp);
                }
                System.out.println(classifyPoint(map, knn, newpoint));
            }
            else
                break;
        }

    }

    public static float notquitedistance(ArrayList<Float> a, ArrayList<Float> b) {
        float dist=0;
        for(int i=0;i<a.size();i++) {
            dist+=Math.abs(a.get(i)-b.get(i));
        }
        return dist;
    }
    // to nie robi potegi i pierwiastka ale do porównywania starczy

    public static String classifyPoint(Map<String, ArrayList<ArrayList<Float>>> map, int knn, ArrayList<Float> target) {
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<ArrayList<Float>> value = new ArrayList<>();

        for (int i = 0; i < knn; i++) {  //robie sobie inicjacje knn najblizszych jako "" (9999,9999...)
            keys.add("");
            value.add(new ArrayList<Float>());
            for (int j = 0; j < target.size(); j++) {
                value.get(i).add(9999f);
            }
        }

        for (var entry : map.entrySet()) {  //ide po kolei po wejściach do mapy (klucz: lista list współrzędnych)
            String name = entry.getKey();
            ArrayList<ArrayList<Float>> vectors = entry.getValue();
            for (int z = 0; z < vectors.size(); z++) {  //ide po kolei po liśćie list współrzędnych
                ArrayList<Float> vector = vectors.get(z);  //wyciagam współrzędne jednego punktu
                float maxdist = -1;
                int maxindex = -1;
                for (int j = 0; j < value.size(); j++) {  //znajduje najwiekszą odległość z knn najbliższych
                    float dist = Main.notquitedistance(target, value.get(j));
                    if (dist > maxdist) {
                        maxdist = dist;
                        maxindex = j;
                    }
                }

                if (maxdist > Main.notquitedistance(vector, target)) { //jeżeli odległość pomiędzy nowym punktem i aktualnie sprawdznaym z mapy
                    keys.set(maxindex, name);                          //jest mniejsza niż najwieksza z tych knn to podmieniam
                    value.set(maxindex, vector);
                }
            }

            /*System.out.println("--------------");
            System.out.println("point: ");
            System.out.println(target);
            System.out.println("closests: ");
            for(int k=0;k<keys.size();k++) {
                System.out.println(keys.get(k));
                System.out.println(value.get(k));
            }*/

        }

        Map<String, Integer> frequency = new HashMap<>();
        for (String word : keys) {
            if (frequency.containsKey(word)) {
                frequency.put(word, frequency.get(word) + 1);
            } else {
                frequency.put(word, 1);
            }
        }

        int maxCount = 0;
        String mostFrequent = "";

        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequent = entry.getKey();
            }
        }

        //System.out.println("classification: "+mostFrequent);

        return mostFrequent;
    }

}
