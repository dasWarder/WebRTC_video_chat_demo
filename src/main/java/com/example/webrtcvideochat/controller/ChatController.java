package com.example.webrtcvideochat.controller;


import com.example.webrtcvideochat.model.Room;
import com.example.webrtcvideochat.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Controller
@ControllerAdvice
public class ChatController {

    private RoomService roomService;

    @Autowired
    public ChatController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping( "/")
    public String displayMainPage(String id, String uuid, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("rooms", roomService.getRooms());
        model.addAttribute("uuid", uuid);

        return "main";
    }

    @PostMapping(value = "/room", params = "action=create")
    public String processRoomSelection(@ModelAttribute("id") String sid,
                                       @ModelAttribute("uuid") String uuid,
                                       final BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            return "redirect:/";
        }

        Optional.ofNullable(uuid).ifPresent(name -> roomService.addRoom(new Room(sid)));

        return displayMainPage(sid, uuid, model);
    }

    @GetMapping("/room/{sid}/user/{uuid}")
    public String displaySelectedRoom(@PathVariable("sid") String sid, @PathVariable("uuid") String uuid, Model model) {
        String view = "redirect:/";

        if (sid != null) {
            Room room = roomService.findRoomByStringId(sid).orElse(null);
            if(room != null && uuid != null && !uuid.isEmpty()) {
                model.addAttribute("id", sid);
                model.addAttribute("uuid", uuid);

                view = "chat_room";
            }
        }

        return view;
    }

    @GetMapping("/room/{sid}/user/{uuid}/exit")
    public String processRoomExit(@PathVariable("sid") final String sid, @PathVariable("uuid") final String uuid) {
        return "redirect:/";
    }
}
