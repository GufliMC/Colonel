package com.guflimc.colonel.minecraft.spigot.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.common.suggestion.Suggestion;
import com.guflimc.colonel.minecraft.spigot.SpigotColonel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpigotArgumentTests {

    private ServerMock server;
    private SpigotColonel colonel;

    @BeforeEach
    public void init() {
        server = MockBukkit.mock();
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        colonel = new SpigotColonel(plugin);
    }

    @AfterEach
    public void shutdown() {
        MockBukkit.unmock();
    }

    //

    @Test
    public void testPlayerCompleter() {
        server.addPlayer("john");
        server.addPlayer("alice");

        colonel.registerAll(new Object() {
            @Command("tp")
            public void tp(Player player) {}
        });

        assertEquals(List.of(new Suggestion("john"), new Suggestion("alice")),
                colonel.suggestions(null, "tp "));
    }

    @Test
    public void testPlayerParser() {
        PlayerMock john = server.addPlayer("john");
        server.addPlayer("alice");

        colonel.registerAll(new Object() {
            @Command("tp")
            public void tp(Player player) {
                assertEquals(john, player);
            }
        });

        colonel.dispatch(null, "tp john");
    }

    @Test
    public void commandTest() {
        AtomicReference<Boolean> ref = new AtomicReference<>(false);
        colonel.registerAll(new Object() {
            @Command("send")
            public void send() {
                ref.set(true);
            }
        });

        PlayerMock john = server.addPlayer("john");
        server.getCommandMap().dispatch(john, "send");

        assertTrue(ref.get());
    }

}
