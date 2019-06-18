package org.kin.ecosystem.appreciation.options.menu.sample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.kin.ecosystem.appreciation.options.menu.ui.CloseType;
import org.kin.ecosystem.appreciation.options.menu.ui.DialogTheme;
import org.kin.ecosystem.appreciation.options.menu.ui.EventsListener;
import org.kin.ecosystem.appreciation.options.menu.ui.GiftingDialog;


@SuppressLint("Registered")
public class MainActivityJava extends AppCompatActivity {

    private EventsListener eventsListener = new EventsListener() {
        @Override
        public void onDialogOpened() {
            showToast("Dialog opened");
        }

        @Override
        public void onItemSelected(int itemIndex, @NotNull String amount) {
            showToast("onItemSelected -> index: " + itemIndex + " amount: " + amount);
        }

        @Override
        public void onDialogClosed(@NotNull CloseType closeType) {
            showToast("Dialog closed -> type: " + closeType.name());
        }
    };

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button opnDialogButtonLight = findViewById(R.id.button_open_dialog_light);
        opnDialogButtonLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGiftingDialog(200, DialogTheme.LIGHT);
            }
        });

        final Button opnDialogButtonDark = findViewById(R.id.button_open_dialog_dark);
        opnDialogButtonDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGiftingDialog(8, DialogTheme.DARK);
            }
        });
    }

    private void openGiftingDialog(long balance, DialogTheme dialogTheme) {
        new GiftingDialog.Builder(this)
                .balance(balance)
                .theme(dialogTheme)
                .eventsListener(eventsListener)
                .build()
                .show();
    }
}
