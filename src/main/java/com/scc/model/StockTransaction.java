package com.scc.model;

public record StockTransaction(int transaction_id, int supply_id, String supply_name, int user_id,
                               String transaction_type, int quantity, String customer, double total,
                               String transaction_date, String remarks, boolean is_voided) {
}