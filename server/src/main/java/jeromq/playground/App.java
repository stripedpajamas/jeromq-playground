package jeromq.playground;

import java.util.*;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.msgpack.core.*;

public class App {
    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {
                MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(socket.recv(0));
                int n = unpacker.unpackInt();
                System.out.println("Received: [" + n + "]");

                // do the math
                Set<Integer> factors = Factor.getFactors(n);
                // craft response
                MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
                packer.packArrayHeader(factors.size());
                for (int x: factors) {
                    packer.packInt(x);
                }
                packer.close();
                socket.send(packer.toByteArray(), 0);
            }
        }
    }
}
