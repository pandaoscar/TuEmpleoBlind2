package com.example.tuempleoblind;

import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class UtilCommandModel {
    public static class ComponentResult {
        private EditText emptyEditText;
        private Spinner emptySpinner;

        public ComponentResult(EditText emptyEditText, Spinner emptySpinner) {
            this.emptyEditText = emptyEditText;
            this.emptySpinner = emptySpinner;
        }

        public EditText getEmptyEditText() {
            return emptyEditText;
        }

        public Spinner getEmptySpinner() {
            return emptySpinner;
        }
    }

    public static ComponentResult checkComponents(List<EditText> editTexts, List<Spinner> spinners) {
        EditText emptyEditText = null;
        Spinner emptySpinner = null;

        // Check for empty EditText
        for (EditText editText : editTexts) {
            if (editText.getText().toString().trim().isEmpty()) {
                emptyEditText = editText;
                break;
            }
        }
        if (spinners != null) {
            for (Spinner spinner : spinners) {
                if (spinner.getSelectedItem().toString().trim().equals("Selecciona una opción")) {
                    emptySpinner = spinner;
                    break;
                }
            }
        }
        // Check for unselected Spinner

        return new ComponentResult(emptyEditText, emptySpinner);
    }
}
