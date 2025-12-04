package eu.darkcode.client.overlay;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimpleWebSocketServer {

    private final int port;
    private final List<Socket> clients = new CopyOnWriteArrayList<>();
    private volatile boolean running = true;

    public SimpleWebSocketServer(int port) {
        this.port = port;
    }

    private static String generateAccept(String key) {
        try {
            String concat = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(concat.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Very small encoder: only handles payload length < 126
    private static byte[] encodeTextFrame(String msg) {
        byte[] payload = msg.getBytes();
        int len = payload.length;
        int headerLen = 2;
        byte[] frame;
        if (len <= 125) {
            frame = new byte[headerLen + len];
            frame[0] = (byte) 0x81; // FIN + text
            frame[1] = (byte) len;
            System.arraycopy(payload, 0, frame, headerLen, len);
        } else {
            // fallback for longer messages (simple)
            frame = ByteBuffer.allocate(4 + len)
                    .put((byte) 0x81)
                    .put((byte) 126)
                    .putShort((short) len)
                    .put(payload)
                    .array();
        }
        return frame;
    }

    public void start() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try (ServerSocket server = new ServerSocket(port)) {
                // accept loop
                while (running) {
                    Socket client = server.accept();
                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            if (doHandshake(client)) {
                                clients.add(client);
                                System.out.println("Client connected: " + client.getRemoteSocketAddress());
                                // keep reading to detect closure (simple)
                                InputStream in = client.getInputStream();
                                byte[] buf = new byte[2];
                                while (running) {
                                    int r = in.read(buf);
                                    if (r == -1) break;
                                    // ignore incoming data
                                }
                            }
                        } catch (Exception e) {
                            // ignore
                        } finally {
                            clients.remove(client);
                            try {
                                client.close();
                            } catch (IOException ignored) {
                            }
                            System.out.println("Client disconnected");
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // broadcaster: every 2 seconds send a JSON update
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            String payload = "{\"ts\":" + System.currentTimeMillis() + ",\"message\":\"update\"}";
            broadcast(payload);
        }, 1, 2, TimeUnit.SECONDS);
    }

    private boolean doHandshake(Socket client) throws IOException {
        InputStream in = client.getInputStream();
        StringBuilder req = new StringBuilder();
        byte[] buffer = new byte[1024];
        int read = in.read(buffer);
        if (read <= 0) return false;
        req.append(new String(buffer, 0, read));
        String request = req.toString();
        // Extract Sec-WebSocket-Key
        String key = null;
        for (String line : request.split("\r\n")) {
            if (line.toLowerCase().startsWith("sec-websocket-key:")) {
                key = line.substring(line.indexOf(':') + 1).trim();
                break;
            }
        }
        if (key == null) return false;
        String accept = generateAccept(key);
        String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: " + accept + "\r\n\r\n";
        client.getOutputStream().write(response.getBytes());
        client.getOutputStream().flush();
        return true;
    }

    private void broadcast(String text) {
        byte[] frame = encodeTextFrame(text);
        for (Socket s : clients) {
            try {
                OutputStream out = s.getOutputStream();
                out.write(frame);
                out.flush();
            } catch (IOException e) {
                clients.remove(s);
                try {
                    s.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void stop() {
        running = false;
        for (Socket s : clients) {
            try {
                s.close();
            } catch (IOException ignored) {
            }
        }
    }
}