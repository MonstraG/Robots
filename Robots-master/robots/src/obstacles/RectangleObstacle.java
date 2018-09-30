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
        anchorPoints.add(new Point(topleftCorner.x - anchorDistance, topleftCorner.y - anchorDistance));
        //down left
        anchorPoints.add(new Point(topleftCorner.x - anchorDistance, topleftCorner.y + size + anchorDistance));

        //up right
        anchorPoints.add(new Point(topleftCorner.x + size + anchorDistance, topleftCorner.y - anchorDistance));

        //down right
        anchorPoints.add(new Point(topleftCorner.x + size + anchorDistance, topleftCorner.y + size + anchorDistance));

        //collision points
        Point temp = anchorPoints.get(0);
        collisionPoints.add(new Point(temp.x + anchorDistance/2, temp.y + anchorDistance/2));

        temp = anchorPoints.get(1);
        collisionPoints.add(new Point(temp.x + anchorDistance/2, temp.y - anchorDistance/2));

        temp = anchorPoints.get(2);
        collisionPoints.add(new Point(temp.x - anchorDistance/2, temp.y + anchorDistance/2));

        temp = anchorPoints.get(3);
        collisionPoints.add(new Point(temp.x - anchorDistance/2, temp.y - anchorDistance/2));


        //collision pairs, adding diagonals
        Line line = new Line(collisionPoints.get(0), collisionPoints.get(2)); //up
        collisionPairs.add(line);
        line = new Line(collisionPoints.get(1), collisionPoints.get(3)); // down
        collisionPairs.add(line);
        line = new Line(collisionPoints.get(0), collisionPoints.get(1)); // left
        collisionPairs.add(line);
        line = new Line(collisionPoints.get(2), collisionPoints.get(3)); // right
        collisionPairs.add(line);
    }
}
