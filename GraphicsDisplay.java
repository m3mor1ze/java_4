import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {
    private final BasicStroke graphicsStroke;
    private final BasicStroke axisStroke;
    private final BasicStroke markerStroke;
    private final Font axisFont;

    private Double[][] graphicsData;
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double scale;
    private boolean isRotated = false;

    private Double[][] graphicsData2;
    private boolean showAxis2 = true;
    private boolean showMarkers2 = true;
    private double minX2;
    private double maxX2;
    private double minY2;
    private double maxY2;
    private double scale2;
    private boolean isRotated2 = false;

    private boolean displaySecond = false;

    public GraphicsDisplay() {
        setBackground(Color.WHITE);
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f);
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    public void setRotation(boolean ch) {
        isRotated = ch;
        repaint();
    }

    public void setRotation2(boolean ch) {
        isRotated2 = ch;
        repaint();
    }

    public void showGraphics(Double[][] graphicsData, Double[][] graphicsData2) {
        if (graphicsData != null) {
            this.graphicsData = graphicsData;
        }
        if (graphicsData2 != null) {
            this.graphicsData2 = graphicsData2;
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (graphicsData == null || graphicsData.length == 0)
            return;

        if (graphicsData == null || graphicsData.length == 0)
            return;

        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;

        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
            }
        }
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
        scale = Math.min(scaleX, scaleY);

        if (scale == scaleX) {
            double yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale == scaleY) {
            double xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;
            maxX += xIncrement;
            minX -= xIncrement;
        }

        Graphics2D canvas = (Graphics2D) g;
        AffineTransform initialTransform = canvas.getTransform();

        if (isRotated) {
            AffineTransform rotateTransform = new AffineTransform();
            rotateTransform.rotate(-Math.PI / 2, getSize().getWidth() / 2, getSize().getHeight() / 2);
            canvas.setTransform(rotateTransform);
        }
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (showAxis)
            paintAxis(canvas);

        paintGraphics(canvas, false);

        if (showMarkers)
            paintMarkers(canvas);

        canvas.setTransform(initialTransform);
        if (displaySecond) {
            if (graphicsData2 == null || graphicsData2.length == 0)
                return;

            if (graphicsData2 == null || graphicsData2.length == 0)
                return;

            minX2 = graphicsData2[0][0];
            maxX2 = graphicsData2[graphicsData2.length - 1][0];
            minY2 = graphicsData2[0][1];
            maxY2 = minY2;

            for (int i = 1; i < graphicsData2.length; i++) {
                if (graphicsData2[i][1] < minY2) {
                    minY2 = graphicsData2[i][1];
                }
                if (graphicsData2[i][1] > maxY2) {
                    maxY2 = graphicsData2[i][1];
                }
            }

            scaleX = getSize().getWidth() / (maxX2 - minX2);
            scaleY = getSize().getHeight() / (maxY2 - minY2);
            scale2 = Math.min(scaleX, scaleY);

            if (scale2 == scaleX) {
                double yIncrement = (getSize().getHeight() / scale2 - (maxY2 - minY2)) / 2;
                maxY2 += yIncrement;
                minY2 -= yIncrement;
            }
            if (scale2 == scaleY) {
                double xIncrement = (getSize().getWidth() / scale2 - (maxX2 - minX2)) / 2;
                maxX2 += xIncrement;
                minX2 -= xIncrement;
            }

            if (isRotated2) {
                AffineTransform rotateTransform = new AffineTransform();
                rotateTransform.rotate(-Math.PI / 2, getSize().getWidth() / 2, getSize().getHeight() / 2);
                canvas.setTransform(rotateTransform);
            }

            if (showAxis2)
                paintAxis2(canvas);

            paintGraphics(canvas, true);

            if (showMarkers2)
                paintMarkers2(canvas);

        }
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    public void paintGraphics(Graphics2D canvas, boolean ch) {
        GeneralPath graphics = new GeneralPath();
        if (!ch) {
            canvas.setStroke(graphicsStroke);
            canvas.setColor(Color.RED);
            for (int i = 0; i < graphicsData.length; i++) {
                Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
                if (i > 0) {
                    graphics.lineTo(point.getX(), point.getY());
                } else {
                    graphics.moveTo(point.getX(), point.getY());
                }
            }
            canvas.draw(graphics);
        }
        if (ch) {
            BasicStroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10, 10, 10, 10, 10, 10, 5, 10, 5, 10, 5, 10}, 0);
            canvas.setStroke(dashed);

            canvas.setColor(Color.CYAN);
            for (int i = 0; i < graphicsData2.length; i++) {
                Point2D.Double point = xyToPoint(graphicsData2[i][0], graphicsData2[i][1]);
                if (i > 0) {
                    graphics.lineTo(point.getX(), point.getY());
                } else {
                    graphics.moveTo(point.getX(), point.getY());
                }
            }
            canvas.draw(graphics);
        }
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    protected void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);
        for (Double[] point : graphicsData) {
            double x = point[0];
            double y = point[1];
            Point2D.Double center = xyToPoint(x, y);
            int integerPart = (int) y;
            int sumOfDigits = 0;
            while (integerPart != 0) {
                sumOfDigits += integerPart % 10;
                integerPart /= 10;
            }

            if (sumOfDigits < 10) {
                canvas.setColor(Color.green);
            } else {
                canvas.setColor(Color.BLACK);
            }

            Ellipse2D.Double marker = new Ellipse2D.Double(center.getX() - 5.5, center.getY() - 5.5, 11, 11);
            canvas.draw(marker);
            canvas.drawLine((int) center.getX(), (int) center.getY() - 5, (int) center.getX(), (int) center.getY() + 5);
            canvas.drawLine((int) center.getX() - 5, (int) center.getY(), (int) center.getX() + 5, (int) center.getY());
        }
    }

    protected void paintAxis(Graphics2D canvas) {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);
        FontRenderContext context = canvas.getFontRenderContext();

        if (minX <= 0.0 && maxX >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float) labelPos.getX() + 10, (float) (labelPos.getY() - bounds.getY()));
        }

        if (minY <= 0.0 && maxY >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float) (labelPos.getX() - bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));
        }
    }

    public void setShowAxis2(boolean showAxis2) {
        this.showAxis2 = showAxis2;
        repaint();
    }

    public void setShowMarkers2(boolean showMarkers2) {
        this.showMarkers2 = showMarkers2;
        repaint();
    }

    protected void paintMarkers2(Graphics2D canvas) {
        canvas.setStroke(markerStroke);
        for (Double[] point : graphicsData2) {
            double x = point[0];
            double y = point[1];
            Point2D.Double center = xyToPoint(x, y);
            int integerPart = (int) y;
            int sumOfDigits = 0;
            while (integerPart != 0) {
                sumOfDigits += integerPart % 10;
                integerPart /= 10;
            }
            if (sumOfDigits < 10) {
                canvas.setColor(Color.green);
            } else {
                canvas.setColor(Color.BLACK);
            }
            Ellipse2D.Double marker = new Ellipse2D.Double(center.getX() - 5.5, center.getY() - 5.5, 11, 11);
            canvas.draw(marker);
            canvas.drawLine((int) center.getX(), (int) center.getY() - 5, (int) center.getX(), (int) center.getY() + 5);
            canvas.drawLine((int) center.getX() - 5, (int) center.getY(), (int) center.getX() + 5, (int) center.getY());
        }
    }

    protected void paintAxis2(Graphics2D canvas) {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.green);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);
        FontRenderContext context = canvas.getFontRenderContext();
        if (minX2 <= 0.0 && maxX2 >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY2), xyToPoint(0, minY2)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY2);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("y2", context);
            Point2D.Double labelPos = xyToPoint(0, maxY2);
            canvas.drawString("y2", (float) labelPos.getX() + 10, (float) (labelPos.getY() - bounds.getY()));
        }

        if (minY2 <= 0.0 && maxY2 >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(minX2, 0), xyToPoint(maxX2, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX2, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("x2", context);
            Point2D.Double labelPos = xyToPoint(maxX2, 0);
            canvas.drawString("x2", (float) (labelPos.getX() - bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));
        }
    }

    public void setDisplaySecond(boolean ch) {
        this.displaySecond = ch;
        repaint();
    }

    protected Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scale, deltaY * scale);
    }
}