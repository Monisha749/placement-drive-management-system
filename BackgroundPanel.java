import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {

    private final Image backgroundImage;
    private final double zoom;

    public BackgroundPanel(String fileName) {
        this(fileName, 1.0);
    }

    public BackgroundPanel(String fileName, double zoom) {
        backgroundImage = new ImageIcon(fileName).getImage();
        this.zoom = zoom < 1.0 ? 1.0 : zoom;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelW = getWidth();
        int panelH = getHeight();

        int drawW = (int) Math.round(panelW * zoom);
        int drawH = (int) Math.round(panelH * zoom);
        int x = (panelW - drawW) / 2;
        int y = (panelH - drawH) / 2;

        g.drawImage(backgroundImage, x, y, drawW, drawH, this);
    }
}