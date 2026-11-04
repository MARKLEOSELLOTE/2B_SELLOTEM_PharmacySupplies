package com.scc.model;

public record ActivityLog(int id, int user_id, String action_type, String description, String timestamp) {
}