package com.deimante.pulsedesk.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Ticket {
    @Id @GeneratedValue
    Long id;
    private String text;
}
