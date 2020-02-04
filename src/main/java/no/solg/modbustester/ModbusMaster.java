package no.solg.modbustester;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

public class ModbusMaster {
    ModbusTcpMaster master;

    public ModbusMaster(String host, int port) {
        ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder(host).build();
        master = new ModbusTcpMaster(config);
    }

    public CompletableFuture<ModbusTcpMaster> connect() {
        return master.connect();
    }

    public void readHoldingRegisters(int address, int quantity, Consumer<byte[]> handler) {
        CompletableFuture<ReadHoldingRegistersResponse> future = master
                .sendRequest(new ReadHoldingRegistersRequest(address, quantity), 0);
        future.thenAccept(response -> {
            ByteBuf bytes = response.getRegisters();
            byte[] byteArray = new byte[bytes.capacity()];
            bytes.getBytes(0,  byteArray);
            handler.accept(byteArray);
            ReferenceCountUtil.release(response);
        });
        future.whenComplete((response, e) -> {
            if (e != null) {
                e.printStackTrace();
            }
        });
    }
}
