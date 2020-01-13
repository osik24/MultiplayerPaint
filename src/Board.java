import java.awt.*;
import java.util.ArrayList;

class Board {
    ArrayList<ArrayList<Integer>> xArray = new ArrayList<>();
    ArrayList<ArrayList<Integer>> yArray = new ArrayList<>();
    ArrayList<Color> colors = new ArrayList<>();
    ArrayList<Integer> strokes = new ArrayList<>();

    void addNewLine(String str) {
        String[] splitStr = str.split(" ");

        while (Integer.parseInt(splitStr[0]) >= xArray.size()) {
            colors.add(null);
            strokes.add(3);
            xArray.add(new ArrayList<>());
            yArray.add(new ArrayList<>());
        }
        if (xArray.get(Integer.parseInt(splitStr[0])).size() == 0) {
            colors.set(Integer.parseInt(splitStr[0]), new Color(Integer.parseInt(splitStr[3])));
            strokes.set(Integer.parseInt(splitStr[0]), Integer.parseInt(splitStr[5]));
        }
        xArray.get(Integer.parseInt(splitStr[0])).add(Integer.parseInt(splitStr[1]));
        yArray.get(Integer.parseInt(splitStr[0])).add(Integer.parseInt(splitStr[2]));
    }
}
