package com.tradingengine;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TradingSimulation {
    public static void main(String[] args) {
        OrderBook exchange = new OrderBook();
        ExecutorService executor = Executors.newFixedThreadPool(5);// 5 Brokers
        Random random = new Random();

        String[] tickers = {"AAPL", "GOOGL", "AMZN", "MSFT", "TSLA"};

        for (int i = 0; i < 20; i++) {
            //  Ensure each thread gets its own copy of order parameters
            Order.OrderType type = random.nextBoolean() ? Order.OrderType.BUY : Order.OrderType.SELL;
            String ticker = tickers[random.nextInt(tickers.length)];
            int quantity = random.nextInt(10) + 1;
            double price = 100 + random.nextDouble() * 50;

            //  Pass immutable order object into executor to avoid race conditions
            Order order = new Order(type, ticker, quantity, price);
            executor.execute(() -> {
                synchronized (exchange) { //  Prevent race conditions when adding orders
                    exchange.addOrder(order);
                }
            });
        }

        //  Ensure all threads finish before proceeding
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("  Timeout reached! Forcing shutdown.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println(" Thread interrupted while waiting for termination.");
            executor.shutdownNow();
        }

        // delay to allow any last orders to process before final matching
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.err.println(" Thread interrupted during delay.");
        }

        //  Run a final check to match any remaining orders
        System.out.println("\n Running final match check for all tickers...");
        for (String ticker : exchange.getAllTickers()) {
            exchange.matchOrders(ticker);
        }

        //  Display final order book (ensure correct method name: `printOrders()`)
        System.out.println("\n Final Order Book:");

        exchange.printOrders(); //  Ensure `printOrders()` is the correct method name
    }
}
