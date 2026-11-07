package com.technexo.chat.controller;

import com.technexo.chat.dto.ChatPeticion;
import com.technexo.chat.dto.ChatRespuesta;
import com.technexo.chat.dto.ConversacionDto;
import com.technexo.chat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatApiController {

    private final ChatService chatService;

    public ChatApiController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatRespuesta responder(@Valid @RequestBody ChatPeticion peticion) {
        return chatService.responder(peticion);
    }

    @GetMapping("/{id}")
    public ConversacionDto historial(@PathVariable Long id, @RequestParam(required = false) String sesion) {
        return chatService.historial(id, sesion);
    }

    @PostMapping("/{id}/cerrar")
    public void cerrar(@PathVariable Long id, @RequestParam(required = false) String sesion) {
        chatService.nuevaConversacion(id, sesion);
    }
}
