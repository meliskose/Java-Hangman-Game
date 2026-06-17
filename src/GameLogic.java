import java.util.Random;
import java.util.ArrayList; 
import java.util.List;

// Oyunun temel kurallarını ve kelime işlemlerini yürüten mantık sınıfı
public class GameLogic {
    private String currentWord; // O an seçili olan gizli kelime
    private String currentCategory; // Kelimenin hangi kategoriye ait olduğu
    private StringBuilder displayWord; // Ekranda görünecek olan (____)  harf giriş yapısı
    private int health = 6; // Başlangıç can sayısı
    //private int score = 0; // PUANLAMA SİSTEMİ GELİŞTİRİLEBİLLİR 
    private List<Character> guessedLetters = new ArrayList<>(); // Tahmin edilen harfleri tutan liste

    // Kelime havuzumuz:
    private String[][] categories = {
        {"HAYVANLAR", "Kedi", "Köpek", "Aslan", "Kuş", "At", "Ayı", "Tavşan", "Balık", "Zürafa", "Kaplan", "Maymun", "Yılan", "Fil", "Kurt", "Fare"},
        {"YEMEKLER", "Pizza", "Pasta", "Pilav", "Çorba", "Ekmek", "Köfte", "Mantı", "Elma", "Armut", "Muz", "Peynir", "Süt", "Börek", "Salata", "Döner"},
        {"ÜLKELER", "Türkiye", "Almanya", "Fransa", "İtalya", "Rusya", "Çin", "Japonya", "Mısır", "Yunanistan", "İspanya", "Brezilya", "Kanada", "İran", "Irak", "Kore"},
        {"SPORLAR", "Futbol", "Tenis", "Basket", "Voleybol", "Koşu", "Yüzme", "Boks", "Güreş", "Kayak", "Okçuluk", "Golf", "Halter", "Judo", "Eskrim", "Hentbol"},
        {"FİLM & DİZİ", "Batman", "Joker", "Shrek", "Avatar", "Matrix", "Titanic", "Tarzan", "Maske", "BuzDevri", "Yüzük", "Hulk", "Thor", "Superman", "Spiderman", "Ironman"},
        {"ARAÇLAR", "Araba", "Otobüs", "Gemi", "Uçak", "Tren", "Taksi", "Kamyon", "Bisiklet", "Motor", "Jet", "Vapur", "Metro", "Traktör", "Kaykay", "Helikopter"},
        {"OYUNLAR", "Tetris", "Pacman", "Mario", "Roblox", "Snake", "Zelda", "Sims", "Chess", "Dama", "Tavla", "Okey", "Sudoku", "Satranç", "Bingo", "Puan"},
        {"UZAY", "Güneş", "Dünya", "Yıldız", "Ay", "Mars", "Roket", "Uydu", "Gezegen", "Uzay", "Kuyruk", "Işık", "Venüs", "Jüpiter", "Samanyolu", "Boşluk"},
        {"MÜZİK", "Gitar", "Piyano", "Davul", "Flüt", "Keman", "Saz", "Zil", "Nota", "Şarkı", "Ses", "Koro", "Konser", "Beste", "Ritim", "Melodi"}
    };
    
    public GameLogic() {
        // Oyun başladığında rastgele bir kategori ve o kategoriden bir kelime seçiyoruz
        Random rand = new Random();
        int catIndex = rand.nextInt(categories.length);
        // Dizinin ilk elemanı kategori adı olduğu için index'i 1'den başlatıyoruz
        int wordIndex = rand.nextInt(categories[catIndex].length - 1) + 1;

        this.currentCategory = categories[catIndex][0];
        this.currentWord = categories[catIndex][wordIndex].toUpperCase();
        
        // Seçilen kelimenin uzunluğu kadar ekrana alt tire ekliyoruz
        this.displayWord = new StringBuilder();
        for (int i = 0; i < currentWord.length(); i++) {
            displayWord.append("_");
        }
    }

    // Bir harfin daha önce girilip girilmediğini kontrol eden yardımcı metot
    public boolean isAlreadyGuessed(char letter) {
        return guessedLetters.contains(Character.toUpperCase(letter));
    }

    // Harf tahmini yapıldığında çalışan ana döngü
    public boolean guessLetter(char letter) {
        letter = Character.toUpperCase(letter);
        // Tahmin edilen harfi listeye ekliyoruz ki tekrar kontrol edebilelim
        if (!guessedLetters.contains(letter)) {
            guessedLetters.add(letter);
        }

        boolean found = false;
        // Kelimeyi harf harf tarayıp tahminle eşleşen var mı bakıyoruz
        for (int i = 0; i < currentWord.length(); i++) {
            if (currentWord.charAt(i) == letter) {
                // Eğer harf doğruysa ve daha önce açılmadıysa yerleştir ve devam et
                if (displayWord.charAt(i) == '_') {
                    displayWord.setCharAt(i, letter);
                    
                }
                found = true;
            }
        }
        // Eğer harf kelimede hiç yoksa canı bir azaltıyoruz
        if (!found) health--;
        return found;
    }

    // Kelime doğru tahmin edildiğinde tüm harfleri görünür yapar
    public void completeWord() {
        this.displayWord = new StringBuilder(currentWord);
        
    }

    // Getter metotları: UI kısmının verilere erişmesini sağlar
    public void reduceHealth() { this.health--; }
    public String getCurrentWord() { return currentWord; }
    public String getCurrentCategory() { return currentCategory; }
    public String getDisplayWord() { return displayWord.toString(); }
    public int getHealth() { return health; }


    // Oyunun kazanılma veya kaybedilme durumlarını kontrol eden mantıksal metotlar
    public boolean isWon() { return displayWord.toString().equals(currentWord); }
    public boolean isLost() { return health <= 0; }
}