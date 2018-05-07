package gui;

import java.awt.*;

public class Line {
    Point p1;
    Point p2;
    double lenght;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.lenght = RobotMovement.distance(p1.x, p1.y, p2.x, p2.y);
    }

    public void setP1(Point p) {this.p1 = p;}
    public void setP2(Point p) {this.p2 = p;}

    public Point getP1() {return p1;}
    public Point getP2() {return p2;}

    public boolean intersect(Line l) {
        boolean result = false;
        //make y = ax + b
        double a1 = (this.p2.y - this.p1.y) / (this.p2.x - this.p1.x);
        double b1 = this.p1.y - a1 * this.p1.x;
        double a2 = (l.p2.y - l.p1.y) / (l.p2.x - l.p1.x);
        double b2 = l.p1.y - a2 * l.p1.x;
        double x0 = -(b1 - b2) / (a1 - a2); //intersection
        if (((Math.min(this.p1.x, this.p2.x) < x0) && (x0 < Math.max(this.p1.x, this.p2.x))) && //lies in first line
        ((Math.min(l.p1.x, l.p2.x) < x0) && (x0 < Math.max(l.p1.x, l.p2.x)))) //lies in second line
            result = true;
        return result;
    }
}
