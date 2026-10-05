package com.example.calculadora;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private TextView tvOperation;

    private String currentInput = "";
    private Double operand1 = null;
    private String operator = null;
    private boolean isNewOperation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialització de les pantalles
        tvDisplay = findViewById(R.id.tvDisplay);
        tvOperation = findViewById(R.id.tvOperation);

        // Configuració dels botons
        setupNumberButtons();
        setupOperatorButtons();
        setupTrigButtons();
    }

    private void setupNumberButtons() {
        int[] numberButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : numberButtonIds) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> {
                String digit = ((Button) v).getText().toString();
                if (isNewOperation) {
                    currentInput = "";
                    isNewOperation = false;
                }
                currentInput += digit;
                tvDisplay.setText(currentInput);
            });
        }
    }

    private void setupOperatorButtons() {
        // Suma, Resta, Multiplicació, Divisió
        findViewById(R.id.btnAdd).setOnClickListener(v -> handleOperator("+"));
        findViewById(R.id.btnSubtract).setOnClickListener(v -> handleOperator("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> handleOperator("×"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> handleOperator("÷"));

        // Igual (=)
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());

        // Netejar (C)
        findViewById(R.id.btnClear).setOnClickListener(v -> clearAll());
    }

    private void handleOperator(String op) {
        if (!currentInput.isEmpty()) {
            try {
                operand1 = Double.parseDouble(currentInput);
                operator = op;
                tvOperation.setText(formatResult(operand1) + " " + op);
                isNewOperation = true;
            } catch (NumberFormatException ignored) {}
        }
    }

    private void setupTrigButtons() {
        // Sinus
        findViewById(R.id.btnSin).setOnClickListener(v -> handleTrig("sin"));

        // Cosinus
        findViewById(R.id.btnCos).setOnClickListener(v -> handleTrig("cos"));

        // Tangent
        findViewById(R.id.btnTan).setOnClickListener(v -> handleTrig("tan"));
    }

    private void handleTrig(String func) {
        Double value = null;
        if (!currentInput.isEmpty()) {
            try {
                value = Double.parseDouble(currentInput);
            } catch (NumberFormatException ignored) {}
        } else if (operand1 != null) {
            value = operand1;
        }

        if (value != null) {
            // Conversió de graus a radians
            double radians = Math.toRadians(value);
            double result = 0.0;

            switch (func) {
                case "sin":
                    result = Math.sin(radians);
                    break;
                case "cos":
                    result = Math.cos(radians);
                    break;
                case "tan":
                    result = Math.tan(radians);
                    break;
            }

            tvOperation.setText(func + "(" + formatResult(value) + "°)");
            tvDisplay.setText(formatResult(result));
            currentInput = String.valueOf(result);
            isNewOperation = true;
        }
    }

    private void calculateResult() {
        if (operand1 != null && operator != null && !currentInput.isEmpty()) {
            try {
                double operand2 = Double.parseDouble(currentInput);
                double result;

                switch (operator) {
                    case "+":
                        result = operand1 + operand2;
                        break;
                    case "-":
                        result = operand1 - operand2;
                        break;
                    case "×":
                        result = operand1 * operand2;
                        break;
                    case "÷":
                        if (operand2 == 0) {
                            tvDisplay.setText("Error");
                            operand1 = null;
                            operator = null;
                            isNewOperation = true;
                            return;
                        }
                        result = operand1 / operand2;
                        break;
                    default:
                        return;
                }

                tvOperation.setText(formatResult(operand1) + " " + operator + " " + formatResult(operand2) + " =");
                tvDisplay.setText(formatResult(result));
                currentInput = String.valueOf(result);
                operand1 = null;
                operator = null;
                isNewOperation = true;

            } catch (NumberFormatException ignored) {}
        }
    }

    private void clearAll() {
        currentInput = "";
        operand1 = null;
        operator = null;
        isNewOperation = true;
        tvDisplay.setText("0");
        tvOperation.setText("");
    }

    private String formatResult(double num) {
        if (Double.isNaN(num) || Double.isInfinite(num)) {
            return "Error";
        }
        // Si és enter exacte, mostrem sense decimals
        if (num == (long) num) {
            return String.valueOf((long) num);
        } else {
            // Limitem a 4 decimals significatius i traiem zeros sobrants
            return String.format(Locale.US, "%.4f", num)
                    .replaceAll("0+$", "")
                    .replaceAll("\\.$", "");
        }
    }
}