package com.example.android.justjava;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends ActionBarActivity {

    int quantity=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        String priceMessage = createOrderSummary(calculatePrice(), getNameString(), getWhippedState(), getChocolateState());
        displayMessage(priceMessage);
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void display(int number) {
        TextView quantityTextView = (TextView) findViewById(
                R.id.quantity_text_view);
        quantityTextView.setText("" + number);
    }

    /**
     * This method calculates price for an order.
     */
    private int calculatePrice() {
        int basePrice = 5;
        if (getWhippedState()) {
            basePrice = basePrice+1;
        }
        if (getChocolateState()) {
            basePrice = basePrice+2;
        }
        int total = quantity*basePrice;
        return total;
    }

    private boolean getWhippedState(){
        CheckBox whipped = (CheckBox) findViewById(R.id.whippedCheck);
        return whipped.isChecked();
    }

    private boolean getChocolateState() {
        CheckBox chocolate = (CheckBox) findViewById(R.id.chocCheck);
        return chocolate.isChecked();
    }

    private String getNameString() {
        EditText nameBox = (EditText) findViewById(R.id.InputName);
        return nameBox.getText().toString();
    }

    public void logCheckState(View view) {
        Log.v("MainActivity", "Checkbox: " + getWhippedState());
    }

    private String createOrderSummary(int price, String inputName, boolean hasWhippedCream, boolean hasChocolate) {
        String output = "Name: " + inputName + "\nAdd whipped cream? " + hasWhippedCream + "\nAdd chocolate? " + hasChocolate + "\nQuantity: " + quantity + "\nTotal: $" + price + "\nThank you!";
        return output;
    }

    /**
     * This method displays the given text on the screen. Casts output of findViewById (View type) into TextView by (TextView)
     */
    private void displayMessage(String message) {
        TextView orderSummaryTextView = (TextView) findViewById(R.id.order_summary_text_view);
        orderSummaryTextView.setText(message);
    }

    /**
     * This method increments the quantity.
     */
    public void increment(View view) {
        if (quantity < 100) {
            quantity = quantity + 1;
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),"I'm sorry, but I have to cut you off at 100 cups...", Toast.LENGTH_SHORT);
            toast.show();
        }
        display(quantity);
    }

    /**
     * This method decrements the quantity.
     */
    public void decrement(View view) {
        if (quantity > 0) {
            quantity = quantity - 1;
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),"You can't order a negative number of coffees, friend!", Toast.LENGTH_SHORT);
            toast.show();
        }
        display(quantity);
    }
}