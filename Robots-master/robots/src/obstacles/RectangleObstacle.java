package obstacles;

import gui.Line;

import java.awt.*;


public class RectangleObstacle extends AbstractObstacle {
    public RectangleObstacle(Point p) {
        type = "square";
        position = p;
        size = 30;
        anchorDistance = 10;

        Point topleftCorner = new Point();
        topleftCorner.setLocation(p.x - size/2, p.y - size/2);

        verticies.add(topleftCorner); //up left
        verticies.add(new Point(topleftCorner.x + size, topleftCorner.y)); //down left
        verticies.add(new Point(topleftCorner.x, topleftCorner.y + size)); // up right
        verticies.add(new Point(topleftCorner.x + size, topleftCorner.y + size)); //down right

        //anchors
        //up left
        anchorPoints.add(new Point(topleftCorner.x - anchorDistance, topleftCorner.y));
        anchorPoints.add(new Point(topleftCorner.x, topleftCorner.y - anchorDistance));

        //down left
        anchorPoints.add(new Point(topleftCorner.x - anchorDistance, topleftCorner.y + size));
        anchorPoints.add(new Point(topleftCorner.x, topleftCorner.y + size + anchorDistance));

        //up right
        anchorPoints.add(new Point(topleftCorner.x + size, topleftCorner.y - anchorDistance));
        anchorPoints.add(new Point(topleftCorner.x + size + anchorDistance, topleftCorner.y));

        //down right
        anchorPoints.add(new Point(topleftCorner.x + size + anchorDistance, topleftCorner.y + size));
        anchorPoints.add(new Point(topleftCorner.x + size, topleftCorner.y + size + anchorDistance));

        //collision pairs, adding diagonals
        Line line = new Line(verticies.get(0), verticies.get(3));
        collisionPairs.add(line);
        line = new Line(verticies.get(1), verticies.get(2));
        collisionPairs.add(line);

    }
}
