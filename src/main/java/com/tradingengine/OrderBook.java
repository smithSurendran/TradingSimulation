package com.tradingengine;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OrderBook {
    private final ConcurrentHashMap<String, Skiplist> buyOrders = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Skiplist> sellOrders = new ConcurrentHashMap<>();

    private Skiplist getBuyOrders(String ticker) {
        return buyOrders.computeIfAbsent(ticker, k -> new Skiplist());
    }

    private Skiplist getSellOrders(String ticker) {
        return sellOrders.computeIfAbsent(ticker, k -> new Skiplist());
    }

    public void addOrder(Order order) {
        synchronized (this) {
            if (order.getOrderType() == Order.OrderType.BUY) {
                getBuyOrders(order.getTickerSymbol()).add(order, false); //  Pass 'false' for BUY
            } else {
                getSellOrders(order.getTickerSymbol()).add(order, true);  //  Pass 'true' for SELL
            }
        }
        matchOrders(order.getTickerSymbol());
    }


    public void matchOrders(String ticker) {
        Skiplist buyList = getBuyOrders(ticker);
        Skiplist sellList = getSellOrders(ticker);

        boolean matchedAtLeastOnce;

        do {
            matchedAtLeastOnce = false; // Reset before checking matches

            Skiplist.Node buyNode = buyList.getLowest();
            Skiplist.Node sellNode = sellList.getLowest();

            if (buyNode == null || sellNode == null) {
                return; // No more matching possible
            }

            Order buyOrder = buyNode.order;
            Order sellOrder = sellNode.order;

            // Ensure orders match within the same ticker
            if (!buyOrder.getTickerSymbol().equals(sellOrder.getTickerSymbol())) {
                return;
            }

            // Skip if an order has zero quantity
            if (buyOrder.getQuantity() == 0) {
                buyList.remove(buyNode);
                continue;
            }
            if (sellOrder.getQuantity() == 0) {
                sellList.remove(sellNode);
                continue;
            }

            // Execute match if conditions met
            if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                int tradeQty = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());

                if (tradeQty <= 0) {
                    return; // Prevent negative trades
                }

                System.out.println(" Matched " + tradeQty + " " + ticker + " at $" + sellOrder.getPrice());

                buyOrder.setQuantity(buyOrder.getQuantity() - tradeQty);
                sellOrder.setQuantity(sellOrder.getQuantity() - tradeQty);

                matchedAtLeastOnce = true;

                // Remove fully matched orders
                if (buyOrder.getQuantity() == 0) {
                    buyList.remove(buyNode);
                }
                if (sellOrder.getQuantity() == 0) {
                    sellList.remove(sellNode);
                }
            }
        } while (matchedAtLeastOnce); // Loop ensures no matches are skipped
    }

    public Set<String> getAllTickers() {
        Set<String> tickers = new HashSet<>();
        tickers.addAll(buyOrders.keySet()); //  Directly add buy tickers
        tickers.addAll(sellOrders.keySet()); //  Directly add sell tickers
        return tickers;
    }

    public void printOrders() {
        System.out.println("\n Current Order Book:");
        Set<String> tickers = getAllTickers(); // Get all tickers
        for (String ticker : tickers) {
            System.out.println("\n🔹 Ticker: " + ticker);
            System.out.println("🟢 Buy Orders:");
            getBuyOrders(ticker).printOrders();
            System.out.println("🔴 Sell Orders:");
            getSellOrders(ticker).printOrders();
        }
    }
}
