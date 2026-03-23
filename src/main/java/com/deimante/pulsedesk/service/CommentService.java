package com.deimante.pulsedesk.service;

import com.deimante.pulsedesk.model.Comment;
import com.deimante.pulsedesk.model.Ticket;
import com.deimante.pulsedesk.repository.CommentRepository;
import com.deimante.pulsedesk.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final HuggingFaceService huggingFaceService;

    public CommentService(CommentRepository commentRepository, TicketRepository ticketRepository, HuggingFaceService huggingFaceService) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.huggingFaceService = huggingFaceService;
    }

    public Comment processComment(String text) {
        Comment comment = new Comment(text);
        commentRepository.save(comment);

        if (huggingFaceService.shouldCreateTicket(text)) {
            String[] ticketData = huggingFaceService.generateTicketData(text);

            Ticket ticket = new Ticket();
            ticket.setTitle(ticketData[0]);
            ticket.setCategory(ticketData[1]);
            ticket.setPriority(ticketData[2]);
            ticket.setSummary(ticketData[3]);
            ticketRepository.save(ticket);
        }

        return comment;
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}