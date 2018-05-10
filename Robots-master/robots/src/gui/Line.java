package gui;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Line extends Line2D{
    Point p1;
    Point p2;
    double length;
    double diffX;
    double diffY;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.length = RobotMovement.distance(p1.x, p1.y, p2.x, p2.y);
        this.diffX = Math.abs(p1.x - p2.x);
        this.diffY = Math.abs(p1.y - p2.y);
    }

    @Override
    public double getX1() {
        return p1.x;
    }

    @Override
    public double getY1() {
        return p1.y;
    }

    @Override
    public Point2D getP1() {
        return p1;
    }

    @Override
    public double getX2() {
        return p2.x;
    }

    @Override
    public double getY2() {
        return p2.y;
    }

    @Override
    public Point2D getP2() {
        return p2;
    }

    @Override
    public void setLine(double v, double v1, double v2, double v3) {
        Point p = new Point();
        p.setLocation(v, v1);
        this.p1 = p;
        p.setLocation(v2, v3);
        this.p2 = p;
        this.length = RobotMovement.distance(p1.x, p1.y, p2.x, p2.y);
        this.diffX = Math.abs(p1.x - p2.x);
        this.diffY = Math.abs(p1.y - p2.y);
    }


    @Override
    public Rectangle2D getBounds2D() {
        return null;
    }

    @Override
    public boolean intersectsLine(Line2D line2D) {
        return super.intersectsLine(line2D);
    }
}
