import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HangmanPainter {
    public static void draw(GraphicsContext gc, int health) {
        gc.clearRect(0, 0, 1024, 320);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4.5);

        // ÇÖP ADAM İÇİN SABİT KOORDİNATLAR
        double ropeX = 375; 
        double ropeY = 10;

        if (health <= 5) gc.strokeOval(ropeX - 22, ropeY, 44, 44);              // Kafa
        if (health <= 4) gc.strokeLine(ropeX, ropeY + 44, ropeX, ropeY + 130);   // Gövde
        if (health <= 3) gc.strokeLine(ropeX, ropeY + 60, ropeX - 35, ropeY + 95);  // Sol Kol
        if (health <= 2) gc.strokeLine(ropeX, ropeY + 60, ropeX + 35, ropeY + 95);  // Sağ Kol
        if (health <= 1) gc.strokeLine(ropeX, ropeY + 130, ropeX - 30, ropeY + 195); // Sol Bacak
        if (health <= 0) gc.strokeLine(ropeX, ropeY + 130, ropeX + 30, ropeY + 195); // Sağ Bacak
    }
}