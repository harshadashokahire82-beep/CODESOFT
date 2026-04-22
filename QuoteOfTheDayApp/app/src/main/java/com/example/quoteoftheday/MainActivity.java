package com.example.quoteoftheday;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView textViewQuote, textViewAuthor, textViewDate;
    Button buttonNew, buttonFavorite, buttonShare, buttonViewFavorites, buttonCopy;
    Button btnEnglish, btnHindi, btnMarathi;
    SharedPreferences sharedPreferences;

    String currentLanguage = "English";
    String currentQuote = "";
    String currentAuthor = "";

    // English Quotes
    String[] englishQuotes = {
            "The only way to do great work is to love what you do.",
            "In the middle of every difficulty lies opportunity.",
            "Believe you can and you are halfway there.",
            "It always seems impossible until it is done.",
            "Dream big and dare to fail.",
            "Start where you are. Use what you have. Do what you can.",
            "You miss 100% of the shots you do not take.",
            "Success is not final, failure is not fatal.",
            "The future belongs to those who believe in their dreams.",
            "Hardships prepare ordinary people for extraordinary destiny."
    };

    String[] englishAuthors = {
            "Steve Jobs",
            "Albert Einstein",
            "Theodore Roosevelt",
            "Nelson Mandela",
            "Norman Vaughan",
            "Arthur Ashe",
            "Wayne Gretzky",
            "Winston Churchill",
            "Eleanor Roosevelt",
            "C.S. Lewis"
    };

    // Hindi Quotes
    String[] hindiQuotes = {
            "जो व्यक्ति अपने काम से प्यार करता है वही महान बनता है।",
            "कठिनाइयाँ व्यक्ति को महान बनाती हैं।",
            "सपने वो नहीं जो नींद में आते हैं, सपने वो हैं जो नींद नहीं आने देते।",
            "असफलता सफलता की पहली सीढ़ी है।",
            "मेहनत करने वालों की कभी हार नहीं होती।",
            "जीवन में आगे बढ़ने के लिए हिम्मत चाहिए।",
            "खुद पर भरोसा रखो, सफलता जरूर मिलेगी।",
            "हर दिन एक नया अवसर लेकर आता है।",
            "संघर्ष ही जीवन है, इससे मत भागो।",
            "बड़े सपने देखो और उन्हें पूरा करने की कोशिश करो।"
    };

    String[] hindiAuthors = {
            "अज्ञात",
            "अज्ञात",
            "डॉ. ए.पी.जे. अब्दुल कलाम",
            "अज्ञात",
            "अज्ञात",
            "अज्ञात",
            "अज्ञात",
            "अज्ञात",
            "अज्ञात",
            "अज्ञात"
    };

    // Marathi Quotes
    String[] marathiQuotes = {
            "यशाचा मार्ग कठीण असतो, पण तो अशक्य नसतो।",
            "स्वप्न पाहा आणि ती पूर्ण करण्यासाठी झटा।",
            "मेहनत आणि चिकाटी यशाची गुरुकिल्ली आहे।",
            "अपयश हे यशाचे पहिले पाऊल आहे।",
            "जीवनात संघर्ष असेल तरच यश मिळते।",
            "स्वतःवर विश्वास ठेवा, यश नक्की मिळेल।",
            "प्रत्येक दिवस नवीन संधी घेऊन येतो।",
            "मोठी स्वप्ने पाहा आणि धाडसाने पुढे जा।",
            "कठीण परिश्रम कधीही वाया जात नाहीत।",
            "जो धाडसाने पुढे जातो तोच यशस्वी होतो।"
    };

    String[] marathiAuthors = {
            "अज्ञात",
            "अज्ञात",
            "अज्ञात",
            "अज्ञात",
            "अज्ञात",
            "अज्ञात",
            "अज्ञात",
            "अज्ञात",
            "अज्ञात",
            "अज्ञात"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewQuote = findViewById(R.id.textViewQuote);
        textViewAuthor = findViewById(R.id.textViewAuthor);
        textViewDate = findViewById(R.id.textViewDate);
        buttonNew = findViewById(R.id.buttonNew);
        buttonFavorite = findViewById(R.id.buttonFavorite);
        buttonShare = findViewById(R.id.buttonShare);
        buttonViewFavorites = findViewById(R.id.buttonViewFavorites);
        buttonCopy = findViewById(R.id.buttonCopy);
        btnEnglish = findViewById(R.id.btnEnglish);
        btnHindi = findViewById(R.id.btnHindi);
        btnMarathi = findViewById(R.id.btnMarathi);

        sharedPreferences = getSharedPreferences("QuoteApp", MODE_PRIVATE);

        String date = new SimpleDateFormat("EEEE, dd MMMM yyyy",
                Locale.getDefault()).format(new Date());
        textViewDate.setText(date);

        showRandomQuote();

        // Language Buttons
        btnEnglish.setOnClickListener(v -> {
            currentLanguage = "English";
            updateLanguageButtons();
            showRandomQuote();
        });

        btnHindi.setOnClickListener(v -> {
            currentLanguage = "Hindi";
            updateLanguageButtons();
            showRandomQuote();
        });

        btnMarathi.setOnClickListener(v -> {
            currentLanguage = "Marathi";
            updateLanguageButtons();
            showRandomQuote();
        });

        buttonNew.setOnClickListener(v -> showRandomQuote());

        buttonFavorite.setOnClickListener(v -> {
            Set<String> favorites = new HashSet<>(
                    sharedPreferences.getStringSet("favorites", new HashSet<>()));
            String entry = currentQuote + " - " + currentAuthor;
            if (favorites.contains(entry)) {
                Toast.makeText(this, "Already in Favorites!", Toast.LENGTH_SHORT).show();
            } else {
                favorites.add(entry);
                sharedPreferences.edit().putStringSet("favorites", favorites).apply();
                Toast.makeText(this, "Added to Favorites! ❤️", Toast.LENGTH_SHORT).show();
            }
        });

        buttonShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    currentQuote + "\n\n- " + currentAuthor);
            startActivity(Intent.createChooser(shareIntent, "Share Quote"));
        });

        buttonCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Quote",
                    currentQuote + " - " + currentAuthor);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Quote copied! 📋", Toast.LENGTH_SHORT).show();
        });

        buttonViewFavorites.setOnClickListener(v -> {
            Set<String> favorites = sharedPreferences
                    .getStringSet("favorites", new HashSet<>());
            if (favorites.isEmpty()) {
                Toast.makeText(this, "No favorites yet!", Toast.LENGTH_SHORT).show();
            } else {
                StringBuilder sb = new StringBuilder();
                int i = 1;
                for (String fav : favorites) {
                    sb.append(i++).append(". ").append(fav).append("\n\n");
                }
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("⭐ My Favorite Quotes")
                        .setMessage(sb.toString())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private void updateLanguageButtons() {
        // Reset all buttons
        btnEnglish.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(Color.parseColor("#13132B")));
        btnEnglish.setTextColor(Color.parseColor("#8888CC"));

        btnHindi.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(Color.parseColor("#13132B")));
        btnHindi.setTextColor(Color.parseColor("#8888CC"));

        btnMarathi.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(Color.parseColor("#13132B")));
        btnMarathi.setTextColor(Color.parseColor("#8888CC"));

        // Highlight selected
        switch (currentLanguage) {
            case "English":
                btnEnglish.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#4A4AFF")));
                btnEnglish.setTextColor(Color.WHITE);
                break;
            case "Hindi":
                btnHindi.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#4A4AFF")));
                btnHindi.setTextColor(Color.WHITE);
                break;
            case "Marathi":
                btnMarathi.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#4A4AFF")));
                btnMarathi.setTextColor(Color.WHITE);
                break;
        }
    }

    private void showRandomQuote() {
        Random random = new Random();
        String[] quotes;
        String[] authors;

        switch (currentLanguage) {
            case "Hindi":
                quotes = hindiQuotes;
                authors = hindiAuthors;
                break;
            case "Marathi":
                quotes = marathiQuotes;
                authors = marathiAuthors;
                break;
            default:
                quotes = englishQuotes;
                authors = englishAuthors;
                break;
        }

        int index = random.nextInt(quotes.length);
        currentQuote = quotes[index];
        currentAuthor = authors[index];
        textViewQuote.setText(currentQuote);
        textViewAuthor.setText("- " + currentAuthor);
    }
}