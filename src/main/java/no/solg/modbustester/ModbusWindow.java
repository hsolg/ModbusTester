package no.solg.modbustester;

import java.io.StringWriter;
import java.util.Arrays;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;

public class ModbusWindow {
    ModbusConfig modbusConfig;
    MultiWindowTextGUI gui;
    ModbusMaster modbusMaster;

    public ModbusWindow(ModbusConfig modbusConfig, MultiWindowTextGUI gui) {
        this.modbusConfig = modbusConfig;
        this.gui = gui;
    }

    public void show() {
        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        Panel widgetPanel = new Panel();
        widgetPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        contentPanel.setPreferredSize(new TerminalSize(1000, 1000));

        mainPanel.addComponent(widgetPanel);
        mainPanel.addComponent(contentPanel.withBorder(Borders.singleLine("Response")));

        widgetPanel.addComponent(new Label("Address"));
        TextBox addressTextBox = new TextBox("0");
        widgetPanel.addComponent(addressTextBox);

        widgetPanel.addComponent(new Label("Quantity"));
        TextBox quantityTextBox = new TextBox("10");
        widgetPanel.addComponent(quantityTextBox);

        Label responseLabel = new Label("");

        Button readButton = new Button("Read", new Runnable() {
            @Override
            public void run() {
                int address = Integer.parseInt(addressTextBox.getText());
                int quantity = Integer.parseInt(quantityTextBox.getText());
                modbusMaster.readHoldingRegisters(address, quantity, bytes -> {
                    responseLabel.setText(hexDump(bytes));
                });
            }
        });
        readButton.setEnabled(false);
        widgetPanel.addComponent(readButton);

        contentPanel.addComponent(responseLabel);

        window.setComponent(mainPanel);

        modbusMaster = new ModbusMaster(modbusConfig.host, modbusConfig.port);
        modbusMaster.connect().thenAccept(what -> {
            readButton.setEnabled(true);
        });

        gui.addWindowAndWait(window);
    }

    // Simple formatting.
    public static String hexDump(byte[] bytes) {
        StringWriter writer = new StringWriter();
        for (int i=0; i<bytes.length; i++) {
            if ((i % 16) > 0) {
                writer.write(" ");
            }
            writer.write(String.format("%02x", bytes[i]));
            if ((i % 16) == 15) {
                writer.write("\n");
            }
        }
        return writer.toString();
    }
}
