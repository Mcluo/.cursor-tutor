package com.example.ecommerce.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    private Long id;
    private String username;
    private String email;
    // 其他字段, getters 和 setters
}