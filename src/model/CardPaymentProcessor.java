package model;

public class CardPaymentProcessor {
    private int balance;

    public CardPaymentProcessor(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public boolean processPayment(int amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        } else {
            return false;
        }
    }
}
