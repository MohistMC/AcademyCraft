package com.mohistmc.academy.utils;

import static org.lwjgl.opengl.GL11.*;

public class Tessellator {

    public static final Tessellator instance = new Tessellator();

    private double _dx, _dy, _dz;

    public void startDrawing(int mode) {
        glBegin(mode);
    }

    public void startDrawingQuads() {
        glBegin(GL_QUADS);
    }

    public void draw() {
        glEnd();
        setTranslation(0, 0, 0);
    }

    public void setTranslation(double dx, double dy, double dz) {
        _dx = dx;
        _dy = dy;
        _dz = dz;
    }

    public void addVertex(double x, double y, double z) {
        glVertex3d(x, y, z);
    }

    public void addVertexWithUV(double x, double y, double z, double u, double v) {
        glTexCoord2d(u, v);
        glVertex3d(x + _dx, y + _dy, z + _dz);
    }

    public void setColorOpaque_F(float r, float g, float b) {
        glColor3f(r, g, b);
    }

    public void setNormal(double x, double y, double z) {
        glNormal3d(x, y, z);
    }

    private Tessellator() {
    }

}
