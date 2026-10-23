package com.scc.model;

public record Supply(int supply_id, String name, String category, int quantity, double price, String expiry_date, int reorder_level) {
}