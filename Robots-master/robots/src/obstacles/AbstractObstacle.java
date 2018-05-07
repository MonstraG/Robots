package obstacles;

import java.awt.*;
import java.util.ArrayList;
import gui.Line;

public abstract class AbstractObstacle {
    String type;
    Point position = new Point();
    int size = 0;
    int anchorDistance = 10;
    ArrayList<Point> verticies = new ArrayList<>();
    ArrayList<Point> anchorPoints = new ArrayList<>();
    ArrayList<Line> collisionPairs = new ArrayList<>();

    public String getType() { return type; }
    public ArrayList<Point> getVerticies() {return verticies; }
    public ArrayList<Point> getAnchors() { return anchorPoints; }
    public ArrayList<Line> getCollisionPairs() {return collisionPairs; }
    public int getSize() {return size;}
    public Point getPosition() {return position;}
}
