package com.example.webrtcvideochat.controller;


import com.example.webrtcvideochat.model.Room;
import com.example.webrtcvideochat.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


/**
 * The controller that provides end points
 */
@Slf4j
@Controller
@ControllerAdvice
public class ChatController {
    /**
     * The Room service bean
     */
    private RoomService roomService;

    @Autowired
    public ChatController(RoomService roomService) {
        this.roomService = roomService;
    }


    /**
     * The method to display main page and add some model attributes for it
     * @param id the current session id
     * @param uuid the id of a client
     * @param model the model
     * @return the view main.html
     */
    @GetMapping( "/")
    public String displayMainPage(String id, String uuid, Model model) {
        log.info("Get main page for user with id={} and uuid={}", id, uuid);
        model.addAttribute("id", id);
        model.addAttribute("rooms", roomService.getRooms());
        model.addAttribute("uuid", uuid);

        return "main";
    }

    /**
     * The method to send the process of selection a room
     * @param sid the string id of the room
     * @param uuid the user id
     * @param binding the result to see that there is no errors
     * @param model the model
     * @return the view of the main page with attributes or redirect to the main page without model attributes
     */
    @PostMapping(value = "/room", params = "action=create")
    public String processRoomSelection(@ModelAttribute("id") String sid,
                                       @ModelAttribute("uuid") String uuid,
                                       final BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            log.info("The error during the process of selecting a room was occurred");
            log.info("Redirecting to the main page");
            return "redirect:/";
        }
        log.info("Logging to the room with id={} and user uuis={}", sid, uuid);
        Optional.ofNullable(uuid).ifPresent(name -> roomService.addRoom(new Room(sid)));

        return displayMainPage(sid, uuid, model);
    }

    /**
     * The method to display a selected room on view
     * @param sid the room string id
     * @param uuid the user id
     * @param model the model
     * @return the view with chat_room.html or redirect to the main
     */
    @GetMapping("/room/{sid}/user/{uuid}")
    public String displaySelectedRoom(@PathVariable("sid") String sid, @PathVariable("uuid") String uuid, Model model) {
        String view = "redirect:/";

        if (sid != null) {
            log.info("Finding a room with id={}", sid);
            Room room = roomService.findRoomByStringId(sid).orElse(null);
            if(room != null && uuid != null && !uuid.isEmpty()) {
                model.addAttribute("id", sid);
                model.addAttribute("uuid", uuid);
                log.info("The user with id={} entered to the room with id={}",uuid, sid);
                view = "chat_room";
            }
        }

        return view;
    }

    /**
     * The method to process exit from room command
     * @param sid the id of a room
     * @param uuid the user id
     * @return the view redirect to the main page
     */
    @GetMapping("/room/{sid}/user/{uuid}/exit")
    public String processRoomExit(@PathVariable("sid") String sid, @PathVariable("uuid") String uuid) {
        log.info("The user with id={} left the room with id={}", uuid, sid);
        return "redirect:/";
    }
}
