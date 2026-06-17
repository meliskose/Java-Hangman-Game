import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    // Oyun mantığını yöneten sınıf ve ana panel katmanımız
    private GameLogic game;
    private StackPane mainRoot;
    
    // Ekranda sürekli güncellenecek olan etiketler ve giriş alanı
    private final Label wordLabel = new Label();
    private final Label statsLabel = new Label();
    private final Label timerLabel = new Label();
    private final Label categoryLabel = new Label();
    private final TextField inputField = new TextField();
    private final Canvas canvas = new Canvas(1024, 320); // Çöp adamın çizileceği alan
    
    private int timeLeft = 150; // Oyun süresi (saniye)
    private Timer timer;

    @Override // JavaFX uygulama başlangıç metodunun özelleştirilmesi
    public void start(Stage primaryStage) {
        mainRoot = new StackPane();
        mainRoot.setAlignment(Pos.CENTER);
        
        // İlk açılışta arka planı ve giriş menüsünü yüklüyoruz
        setupBackground();
        showIndexContent();

        // Pencere boyutlarını ayarlıyoruz
        Scene scene = new Scene(mainRoot, 1024, 576); 
        primaryStage.setScene(scene);
        primaryStage.setTitle("Adam Asmaca");
        primaryStage.show();
    }

    // Arka plan görselini yükleyen metot
    // eğer dosya eksikse hata vermemesi için önlem alıyoruz
    private void setupBackground() {
        try {
            // Proje klasöründeki resmi yüklemeye çalışır
            Image img = new Image("file:arkaplan.jpg");
            BackgroundImage bImg = new BackgroundImage(img, 
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
                BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, true));
            mainRoot.setBackground(new Background(bImg));
        } catch (Exception e) {
            // Eğer resim dosyası bulunamazsa oyunun çökmemesi için düz renk atıyoruz
            // Exception handling kullanımı projenin güvenliği için eklenmiştir
            mainRoot.setStyle("-fx-background-color: #34495e;");
        }
    }

    // Ana giriş ekranı: Başlat ve Nasıl Oynanır butonları
    private void showIndexContent() {
        mainRoot.getChildren().clear(); 
        HBox menuButtonBox = new HBox(40);
        menuButtonBox.setAlignment(Pos.BOTTOM_CENTER);
        // Butonları arka plandaki görselin üzerine binmemesi için aşağıya hizaladık
        menuButtonBox.setPadding(new Insets(0, 0, 65, 0)); 

        Button startBtn = createStyledButton("OYUNA BAŞLA", "#e67e22");
        startBtn.setOnAction(e -> showGameContent());

        Button howToPlayBtn = createStyledButton("NASIL OYNANIR?", "#9b59b6");
        howToPlayBtn.setOnAction(e -> showHowToPlayPopup());

        menuButtonBox.getChildren().addAll(startBtn, howToPlayBtn);
        mainRoot.getChildren().add(menuButtonBox);
    }

    // Kullanıcıya oyun kurallarını gösteren şeffaf pencere
    private void showHowToPlayPopup() {
        VBox overlay = createOverlay();
        VBox messageBox = createMessageBox("🎮 NASIL OYNANIR?");
        Label rulesLabel = new Label("1. Harf veya kelimenin tamamını yazarak tahmin edebilirsiniz.\n2. Her hatalı tahminde çöp adam tahtaya çizilir.\n3. 6 canınız var ve süre 150 saniyedir.");
        rulesLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        Button closeBtn = createStyledButton("ANLADIM", "#9b59b6");
        // Popup mantığı: Overlay katmanını ana root'tan kaldırarak pencereyi kapatıyoruz
        closeBtn.setOnAction(e -> mainRoot.getChildren().remove(overlay));
        
        messageBox.getChildren().addAll(rulesLabel, closeBtn);
        overlay.getChildren().add(messageBox);
        mainRoot.getChildren().add(overlay);
    }

    // Oyunun oynandığı ana ekran tasarımını oluşturuyoruz
    private void showGameContent() {
        mainRoot.getChildren().clear(); 
        game = new GameLogic(); // Her yeni oyunda mantık sınıfını sıfırdan oluşturuyoruz
        timeLeft = 150;
        
        VBox boardLayout = new VBox(10); 
        boardLayout.setAlignment(Pos.CENTER);
        
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(150, 0, 0, 0));
        
        // Bilgi etiketlerinin renklerini ve yazı tiplerini ayarlıyoruz
        categoryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;"); 
        statsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;"); 
        timerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;"); 
        
        infoBox.getChildren().addAll(categoryLabel, statsLabel, timerLabel);
        wordLabel.setFont(new Font("Monospaced Bold", 34)); // Harf boşluklarının sabit kalması için Monospaced font seçildi
        wordLabel.setStyle("-fx-text-fill: #ffffff;");
        
        inputField.setPromptText("Harf/Kelime Gir...");
        inputField.setMaxWidth(200);

        // Enter tuşuna basıldığında tahmin işlemini tetikler
        inputField.setOnAction(e -> handleGuess());
        
        Button guessBtn = createStyledButton("TAHMİN ET", "#2ecc71");
        guessBtn.setOnAction(e -> handleGuess());
        
        HBox inputBox = new HBox(15, inputField, guessBtn);
        inputBox.setAlignment(Pos.CENTER);
        
        boardLayout.getChildren().addAll(infoBox, canvas, wordLabel, inputBox);
        mainRoot.getChildren().add(boardLayout);
        
        updateUI(); // Oyun açıldığında otomatik olarak başlangıç bilgileri ekrana gelir
        startTimer(); // Geri sayımı başlatıyoruz
    }

    // Tahmin butonuna basıldığında çalışan ana kontrol merkezi
    private void handleGuess() {
        String input = inputField.getText().trim().toUpperCase();
        if (input.isEmpty()) return;

        // Tek harf girildiyse harf kontrolü yapılır kelimeyi yazılırsa kelime kontrolü yapıyoruz
        if (input.length() == 1) { 
            char guess = input.charAt(0);
            
            // Aynı harf tekrar girildiyse uyarı verip çıkıyoruz
            if (game.isAlreadyGuessed(guess)) {
                showWarningPopup("'" + guess + "' harfini zaten denediniz!");
                inputField.clear();
                return;
            }
            
            game.guessLetter(guess);
            if (game.isWon()) endGame("TEBRİKLER!\nKelime: " + game.getCurrentWord());
            else if (game.isLost()) endGame("MAALESEF! Hakkınız Bitti\nKelime: " + game.getCurrentWord());
        } else { 
            // Kelime tahmininin doğru olup olmadığına bakıyoruz (String Karşılaştırma)
            if (input.equals(game.getCurrentWord())) {
                game.completeWord();
                endGame("TEBRİKLER!\nKelime: " + input);
            } else {
                game.reduceHealth();
                if (game.isLost()) endGame("OYUN BİTTİ!\nKelime: " + game.getCurrentWord());
            }
        }
        inputField.clear();
        updateUI(); // Tahmin sonrası ekranı güncelliyoruz
    }

    // Kullanıcıyı yönlendirmek için kullandığımız uyarı kutusu
    private void showWarningPopup(String msg) {
        VBox overlay = createOverlay();
        VBox messageBox = createMessageBox("⚠️ UYARI");
        Label msgLabel = new Label(msg);
        msgLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        Button closeBtn = createStyledButton("TAMAM", "#e74c3c");
        closeBtn.setOnAction(e -> mainRoot.getChildren().remove(overlay));
        
        messageBox.getChildren().addAll(msgLabel, closeBtn);
        overlay.getChildren().add(messageBox);
        mainRoot.getChildren().add(overlay);
    }

    // Ekranda görünen kelimeyi ve can bilgisini yenileyen metod
    private void updateUI() {
        wordLabel.setText(game.getDisplayWord());
        statsLabel.setText("KALAN CAN: " + game.getHealth());
        categoryLabel.setText("KATEGORİ: " + game.getCurrentCategory());
        // Çöp adamı güncel can sayısına göre yeniden çizdiriyoruz
        // Canvas temizlenip her seferinde cana göre yeni parçalar ekleniyor
        HangmanPainter.draw(canvas.getGraphicsContext2D(), game.getHealth());
    }

    // Oyun bittiğinde zamanlayıcıyı durdurup sonuç ekranını açıyoruz
    private void endGame(String msg) {
        if (timer != null) timer.cancel(); // Bellek sızıntısını önlemek için timer durdurulur
        Platform.runLater(() -> {
            VBox overlay = createOverlay();
            VBox messageBox = createMessageBox("OYUN SONUCU");
            Label msgLabel = new Label(msg);
            msgLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-text-align: center;");
            
            Button menuBtn = createStyledButton("ANA MENÜ", "#e67e22");
            menuBtn.setOnAction(e -> showIndexContent());
            
            messageBox.getChildren().addAll(msgLabel, menuBtn);
            overlay.getChildren().add(messageBox);
            mainRoot.getChildren().add(overlay);
        });
    }

    // Uygulama genelindeki buton tasarımlarını standart hale getiriyoruz
    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefSize(180, 40);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        return btn;
    }

    // Arka planı karartan katman (pop-uplar için)
    private VBox createOverlay() {
        VBox v = new VBox();
        v.setAlignment(Pos.CENTER);
        v.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
        return v;
    }

    // Mesaj pencerelerinin kutu tasarımı
    private VBox createMessageBox(String title) {
        VBox v = new VBox(20);
        v.setAlignment(Pos.CENTER);
        v.setMaxWidth(450);
        v.setPadding(new Insets(30));
        v.setStyle("-fx-background-color: #2c3e50; -fx-border-color: white; -fx-border-radius: 15; -fx-background-radius: 15;");
        return v;
    }

    //geri sayım mekanizması
    private void startTimer() {
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (timeLeft > 0) {
                        timeLeft--;
                        timerLabel.setText("SÜRE: " + timeLeft + " Saniye");
                    } else { endGame("SÜRE BİTTİ!"); }
                });
            }
        }, 1000, 1000);
    }

    public static void main(String[] args) { launch(args); }
}