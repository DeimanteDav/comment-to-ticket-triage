package com.deimante.pulsedesk.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Comment {
    @Id @GeneratedValue
    private Long id;
    private String title;
    private String category;

}
