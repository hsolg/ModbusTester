package no.solg.modbustester;

import java.util.Arrays;
import java.util.function.Consumer;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;

public class ConfigDialog {
    MultiWindowTextGUI gui;
    Consumer<ModbusConfig> handler;

    public ConfigDialog(MultiWindowTextGUI gui, Consumer<ModbusConfig> handler) {
        this.gui = gui;
        this.handler = handler;
    }

    public void show() {
        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.CENTERED));

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        panel.addComponent(new Label("IP address"));
        TextBox addressTextBox = new TextBox("127.0.0.1");
        panel.addComponent(addressTextBox);

        panel.addComponent(new Label("Port"));
        TextBox portTextBox = new TextBox("502");
        panel.addComponent(portTextBox);

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Button("Connect", new Runnable() {
            @Override
            public void run() {
                ModbusConfig modbusConfig = new ModbusConfig();
                modbusConfig.host = addressTextBox.getText();
                modbusConfig.port = Integer.parseInt(portTextBox.getText());
                handler.accept(modbusConfig);
                gui.removeWindow(window);
            }
        }));

        window.setComponent(panel);

        gui.addWindowAndWait(window);
    }
}
