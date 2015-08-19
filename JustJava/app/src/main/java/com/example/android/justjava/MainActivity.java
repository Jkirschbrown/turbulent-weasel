package com.example.android.justjava;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

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
        String priceMessage = createOrderSummary(calculatePrice());
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
        int total = quantity*5;
        return total;
    }

    private String createOrderSummary(int price) {
        String output = "Name: Justin Kirschbrown\nQuantity: " + quantity + "\nTotal: $" + price + "\nThank you!";
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
        quantity = quantity+1;
        display(quantity);
    }

    /**
     * This method decrements the quantity.
     */
    public void decrement(View view) {
        quantity = quantity-1;
        display(quantity);
    }
}