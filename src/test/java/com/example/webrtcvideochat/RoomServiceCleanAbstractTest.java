package com.example.webrtcvideochat;

import com.example.webrtcvideochat.model.Room;
import com.example.webrtcvideochat.service.RoomService;
import com.example.webrtcvideochat.service.RoomServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;


@SpringBootTest
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
public abstract class RoomServiceCleanAbstractTest {

    @Autowired
    public RoomService roomService;

    @BeforeEach
    public void init() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method setRooms = RoomServiceImpl.class
                .getMethod("setRooms", Set.class);
        setRooms.invoke(roomService, new TreeSet<>(Comparator.comparing(Room::getId)));
    }
}
