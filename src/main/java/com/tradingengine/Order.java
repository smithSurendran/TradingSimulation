package com.tradingengine;

import java.util.Objects;

public class Order implements Comparable<Order> {
    public enum OrderType { BUY, SELL }
    private final OrderType orderType;
    private final String tickerSymbol;
    private int quantity;
    private final double price;

    public Order(OrderType orderType, String tickerSymbol, int quantity, double price) {
        this.orderType = orderType;
        this.tickerSymbol = tickerSymbol;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderType getOrderType() { return orderType; }
    public String getTickerSymbol() { return tickerSymbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public int compareTo(Order other) {
        // Higher price comes first for BUY orders, lower price first for SELL orders
        return Double.compare(this.price, other.price);
    }
    @Override
    public String toString() {
        return orderType + " " + quantity + " of " + tickerSymbol + " at $" + price;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Order order = (Order) obj;
        return Double.compare(order.price, price) == 0 &&
                quantity == order.quantity &&
                orderType == order.orderType &&
                tickerSymbol.equals(order.tickerSymbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderType, tickerSymbol, quantity, price);
    }

}
