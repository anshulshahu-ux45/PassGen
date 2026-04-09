package com.example.passwordgenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText passwordEditText;
    private SeekBar lengthSeekBar;
    private TextView lengthTextView;
    private Button generateButton;
    private Button copyButton;
    private TextView strengthTextView;

    // Character sets
    private final String lowercase = "abcdefghijklmnopqrstuvwxyz";
    private final String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String numbers = "0123456789";
    private final String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
    }

    private void initViews() {
        passwordEditText = findViewById(R.id.passwordEditText);
        lengthSeekBar = findViewById(R.id.lengthSeekBar);
        lengthTextView = findViewById(R.id.lengthTextView);
        generateButton = findViewById(R.id.generateButton);
        copyButton = findViewById(R.id.copyButton);
        strengthTextView = findViewById(R.id.strengthTextView);

        // Set initial length
        lengthSeekBar.setMax(30);
        lengthSeekBar.setProgress(12);
        lengthTextView.setText("Length: 12");
    }

    private void setupListeners() {
        // Length SeekBar
        lengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lengthTextView.setText("Length: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Generate Button
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePassword();
            }
        });

        // Copy Button
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard();
            }
        });
    }

    private void generatePassword() {
        int length = lengthSeekBar.getProgress();
        CheckBox lowercaseCheckBox = findViewById(R.id.lowercaseCheckBox);
        CheckBox uppercaseCheckBox = findViewById(R.id.uppercaseCheckBox);
        CheckBox numbersCheckBox = findViewById(R.id.numbersCheckBox);
        CheckBox specialCheckBox = findViewById(R.id.specialCheckBox);

        boolean useLowercase = lowercaseCheckBox.isChecked();
        boolean useUppercase = uppercaseCheckBox.isChecked();
        boolean useNumbers = numbersCheckBox.isChecked();
        boolean useSpecial = specialCheckBox.isChecked();

        if (!useLowercase && !useUppercase && !useNumbers && !useSpecial) {
            Toast.makeText(this, "Please select at least one character type", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder availableChars = new StringBuilder();
        if (useLowercase) availableChars.append(lowercase);
        if (useUppercase) availableChars.append(uppercase);
        if (useNumbers) availableChars.append(numbers);
        if (useSpecial) availableChars.append(specialChars);

        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(availableChars.length());
            password.append(availableChars.charAt(randomIndex));
        }

        passwordEditText.setText(password.toString());
        updatePasswordStrength(password.toString());
    }

    private void updatePasswordStrength(String password) {
        int strength = calculateStrength(password);
        String strengthText;
        int color;

        if (strength <= 1) {
            strengthText = "Weak";
            color = ContextCompat.getColor(this, R.color.strength_weak);
        } else if (strength == 2) {
            strengthText = "Fair";
            color = ContextCompat.getColor(this, R.color.strength_fair);
        } else if (strength == 3) {
            strengthText = "Good";
            color = ContextCompat.getColor(this, R.color.strength_good);
        } else {
            strengthText = "Strong";
            color = ContextCompat.getColor(this, R.color.strength_strong);
        }

        strengthTextView.setText("Strength: " + strengthText);
        strengthTextView.setTextColor(color);
    }

    private int calculateStrength(String password) {
        int score = 0;
        if (hasLowercase(password)) score++;
        if (hasUppercase(password)) score++;
        if (hasNumbers(password)) score++;
        if (hasSpecialChars(password)) score++;
        if (password.length() >= 12) score++;
        return Math.min(score, 5);
    }

    private boolean hasLowercase(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) return true;
        }
        return false;
    }

    private boolean hasUppercase(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) return true;
        }
        return false;
    }

    private boolean hasNumbers(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) return true;
        }
        return false;
    }

    private boolean hasSpecialChars(String password) {
        return password.matches(".*[!@#$%^&*()_+-=\\$\\${}|;:,.<>?].*");
    }

    private void copyToClipboard() {
        String password = passwordEditText.getText().toString();
        if (!password.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Password", password);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Password copied to clipboard!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No password to copy", Toast.LENGTH_SHORT).show();
        }
    }
}
