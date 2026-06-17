import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

// Oyun içi mesaj pencerelerini (Pop-up) yöneten yardımcı sınıf
public class GameDialogs {
    
    // "Nasıl Oynanır" penceresini oluşturan ve ekrana ekleyen metot
    public static void showHowToPlay(StackPane root) {
        VBox overlay = createOverlay(); // Arka planı karartır
        VBox box = createMessageBox("NASIL OYNANIR?");
        
        Label txt = new Label("Harf veya kelime tahmin ederek çöp adamı kurtarın!\n6 canınız var.");
        txt.setStyle("-fx-text-fill: white;");
        
        Button btn = new Button("ANLADIM");
        // Butona basıldığında sadece bu popup katmanını kaldırır
        btn.setOnAction(e -> root.getChildren().remove(overlay));
        
        box.getChildren().addAll(txt, btn);
        overlay.getChildren().add(box);
        root.getChildren().add(overlay);
    }

    // Oyun bittiğinde kazandın/kaybettin bilgisini gösteren pencere
    public static void showEndGame(StackPane root, String msg, Runnable onRestart) {
        VBox overlay = createOverlay();
        VBox box = createMessageBox("OYUN SONUCU");
        
        Label lbl = new Label(msg);
        lbl.setStyle("-fx-text-fill: white;");
        
        Button btn = new Button("ANA MENÜ");
        btn.setOnAction(e -> {
            root.getChildren().remove(overlay);
            onRestart.run(); // Ana menüye dönmek için gelen fonksiyonu çalıştırır
        });

        box.getChildren().addAll(lbl, btn);
        overlay.getChildren().add(box);
        root.getChildren().add(overlay);
    }

    // Pencerelerin arkasında kalan alanı yarı şeffaf siyah yapan katman
    private static VBox createOverlay() {
        VBox v = new VBox();
        v.setStyle("-fx-background-color: rgba(0,0,0,0.8);"); // %80 opaklık sağlandı
        v.setAlignment(Pos.CENTER);
        return v;
    }

    // Mesaj kutusunun görsel tasarımını (renk, kenarlık, boşluklar) yapan metot
    private static VBox createMessageBox(String title) {
        VBox v = new VBox(15); // Elemanlar arası 15 birim boşluk
       //koyu tema ve yuvarlatılmış köşeler uygulandı
        v.setStyle("-fx-background-color: #2c3e50; -fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        v.setPadding(new Insets(20));
        v.setMaxWidth(400);
        v.setAlignment(Pos.CENTER);
        return v;
    }
}