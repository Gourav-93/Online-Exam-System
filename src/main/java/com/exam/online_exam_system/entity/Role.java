package com.exam.online_exam_system.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ROLE_STUDENT,
    ROLE_ADMIN;

    @JsonCreator
    public static Role fromString(String value) {
        if (value == null) {
            return null;
        }
        String upper = value.toUpperCase();
        if (upper.equals("STUDENT")) {
            return ROLE_STUDENT;
        } else if (upper.equals("ADMIN")) {
            return ROLE_ADMIN;
        }
        return Role.valueOf(upper);
    }
}
