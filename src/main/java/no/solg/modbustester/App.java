package no.solg.modbustester;

import java.io.IOException;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class App {

    public static Screen initTerminal() {
        Screen screen = null;
        try {
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();
        } catch (IOException e) {
            System.out.println("Unable to setup terminal");
        }
        return screen;
    }

    public static void main(String[] args) {
        Screen screen = initTerminal();

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
                new EmptySpace(TextColor.ANSI.BLUE));

        ConfigDialog configDialog = new ConfigDialog(gui, modbusConfig -> {
            ModbusWindow modbusWindow = new ModbusWindow(modbusConfig, gui);
            modbusWindow.show();
        });

        configDialog.show();
    }
}
