package com.deimante.pulsedesk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Comment comment;
    private Ticket ticket;
}