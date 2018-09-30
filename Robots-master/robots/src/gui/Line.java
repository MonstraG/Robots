package gui;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Line extends Line2D{
    private Point p1;
    private Point p2;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
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
