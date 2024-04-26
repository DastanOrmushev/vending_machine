import enums.ActionLetter;
import model.*;
import util.UniversalArray;
import util.UniversalArrayImpl;

import java.util.Scanner;

public class AppRunner {

    private final UniversalArray<Product> products = new UniversalArrayImpl<>();

    private final CoinAcceptor coinAcceptor;

    private static boolean isExit = false;
    private final CardPaymentProcessor cardPaymentProcessor;
    private String paymentMethod;

    private AppRunner() {
        products.addAll(new Product[]{
                new Water(ActionLetter.B, 20),
                new CocaCola(ActionLetter.C, 50),
                new Soda(ActionLetter.D, 30),
                new Snickers(ActionLetter.E, 80),
                new Mars(ActionLetter.F, 80),
                new Pistachios(ActionLetter.G, 130)
        });
        coinAcceptor = new CoinAcceptor(100);
        cardPaymentProcessor = new CardPaymentProcessor(500);
    }

    public static void run() {
        AppRunner app = new AppRunner();
        while (!isExit) {
            app.startSimulation();
        }
    }

    private void startSimulation() {
        print("Выберите метод оплаты: ");
        print("1 - Монеты");
        print("2 - Банковская карта");
        paymentMethod = fromConsole();

        switch (paymentMethod) {
            case "1":
                handleCoinPayment();
                break;
            case "2":
                handleCardPayment();
                break;
            default:
                print("Недопустимый выбор. Попробуйте еще раз.");
        }
    }

    private UniversalArray<Product> getAllowedProducts() {
        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        for (int i = 0; i < products.size(); i++) {
            if (coinAcceptor.getAmount() >= products.get(i).getPrice()) {
                allowProducts.add(products.get(i));
            }
        }
        return allowProducts;
    }

    private void handleCoinPayment() {
        print("В автомате доступны:");
        showProducts(products);
        print("Монет на сумму: " + coinAcceptor.getAmount());
        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        allowProducts.addAll(getAllowedProducts().toArray());
        chooseAction(allowProducts);
    }

    private void handleCardPayment() {
        print("В автомате доступны:");
        showProducts(products);
        print("Баланс на карте: " + cardPaymentProcessor.getBalance());
        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        allowProducts.addAll(getAllowedProducts().toArray());
        chooseAction(allowProducts);
        print("Обновленный баланс на карте: " + cardPaymentProcessor.getBalance());
    }

    private void chooseAction(UniversalArray<Product> products) {
        print(" a - Пополнить баланс");
        showActions(products);
        print(" h - Выйти");
        String action = fromConsole().substring(0, 1);
        if ("a".equalsIgnoreCase(action)) {
            coinAcceptor.setAmount(coinAcceptor.getAmount() + 10);
            print("Вы пополнили баланс на 10");
            return;
        }
        try {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getActionLetter().equals(ActionLetter.valueOf(action.toUpperCase()))) {
                    if ("1".equals(paymentMethod)) {
                        if (coinAcceptor.getAmount() >= products.get(i).getPrice()) {
                            coinAcceptor.setAmount(coinAcceptor.getAmount() - products.get(i).getPrice());
                            print("Вы купили " + products.get(i).getName());
                        } else {
                            print("Недостаточно средств.");
                        }
                    } else if ("2".equals(paymentMethod)) {
                        if (cardPaymentProcessor.processPayment(products.get(i).getPrice())) {
                            print("Оплата прошла успешно. Баланс на карте: " + cardPaymentProcessor.getBalance());
                            print("Вы купили " + products.get(i).getName());
                        } else {
                            print("Недостаточно средств на карте.");
                        }
                    }
                    break;
                }
            }
        } catch (IllegalArgumentException e) {
            if ("h".equalsIgnoreCase(action)) {
                isExit = true;
            } else {
                print("Недопустимая буква. Попрбуйте еще раз.");
                chooseAction(products);
            }
        }
    }

    private void showActions(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(String.format(" %s - %s", products.get(i).getActionLetter().getValue(), products.get(i).getName()));
        }
    }

    private String fromConsole() {
        return new Scanner(System.in).nextLine();
    }

    private void showProducts(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(products.get(i).toString());
        }
    }

    private void print(String msg) {
        System.out.println(msg);
    }
}
