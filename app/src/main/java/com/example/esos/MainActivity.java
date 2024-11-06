package com.example.esos;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText nameInput, phoneInput;
    private Button addButton, showButton, sosButton;
    private ListView contactListView;
    private DatabaseHelper databaseHelper;
    private ArrayList<Contact> contactList;
    private ContactAdapter contactAdapter;
    private Contact selectedContact; // Variable to store the selected contact

    private EditText sosMessageInput;
    // Declare SOS message input


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        addButton = findViewById(R.id.addButton);
        showButton = findViewById(R.id.showButton);
        sosButton = findViewById(R.id.sosButton);
        contactListView = findViewById(R.id.contactListView);

        databaseHelper = new DatabaseHelper(this);
        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, contactList);
        contactListView.setAdapter(contactAdapter);
        sosMessageInput = findViewById(R.id.messageInput);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContacts();
            }
        });

        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedContact = contactList.get(position); // Get the selected contact
                Toast.makeText(MainActivity.this, "Selected: " + selectedContact.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSOS();
            }
        });
    }

    private void addContact() {
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        if (!name.isEmpty() && !phone.isEmpty()) {
            Contact contact = new Contact(name, phone);
            databaseHelper.addContact(contact);
            nameInput.setText("");
            phoneInput.setText("");
            Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter both name and phone", Toast.LENGTH_SHORT).show();
        }
    }

    private void showContacts() {
        contactList.clear();
        contactList.addAll(databaseHelper.getAllContacts());
        contactAdapter.notifyDataSetChanged();
    }

    private void sendSOS() {
        if (selectedContact != null) {
            String message = sosMessageInput.getText().toString();
            if (message.isEmpty()) {
                message = "This is an SOS message."; // Fallback to default if no message is entered
            }

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + selectedContact.getPhone())); // Only SMS apps should handle this
            intent.putExtra("sms_body", message);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent); // Open SMS app
                Toast.makeText(this, "SOS sent to " + selectedContact.getName(), Toast.LENGTH_SHORT).show(); // Alert after sending
            } else {
                // Show alert if no SMS app can handle the intent
                Toast.makeText(this, "Failed to send SOS. No SMS app available.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please select a contact first", Toast.LENGTH_SHORT).show();
        }
    }



}
