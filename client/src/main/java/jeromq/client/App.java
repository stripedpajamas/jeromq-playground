package jeromq.client;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.msgpack.core.*;

public class App {
    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {
            System.out.println("Connecting to server");

            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("tcp://localhost:5555");

            ThreadLocalRandom.current().ints(0, Integer.MAX_VALUE - 1).forEach(x -> {
                // request
                MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
                try {
                    packer.packInt(x);
                } catch (IOException e) {
                    System.out.println("Unable to pack+send " + x);
                    return;
                }
                System.out.println("Requesting factors of: " + x);
                socket.send(packer.toByteArray(), 0);

                // response
                MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(socket.recv(0));
                int len = 0;
                try {
                    len = unpacker.unpackArrayHeader();
                } catch (IOException e) {
                    System.out.println("Unable to parse array header from response for " + x);
                    return;
                }
                Set<Integer> factors = new HashSet<Integer>(len);
                for (int i = 0; i < len; i++) {
                    try {
                        factors.add(unpacker.unpackInt());
                    } catch (IOException e) {
                        System.out.println("Unable to unpack factor from response for " + x);
                        return;
                    }
                }
                System.out.println(
                    "Received factors for " + x + " : " + factors
                );
            });
        }
    }
}
