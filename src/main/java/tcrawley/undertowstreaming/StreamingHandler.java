package tcrawley.undertowstreaming;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;

class StreamingHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.dispatch();
        exchange.setPersistent(true);
        final Sender sender = exchange.getResponseSender();
        send(sender, "start\n");
        Runnable action = new Runnable() {
            @Override
            public void run() {
                stream(sender);
            }
        };
        if (App.isOnThread()) {
            System.out.println("Streaming from another thread");
            (new Thread(action)).start();
        } else {
            action.run();
        }
    }

    void stream(Sender sender) {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
            send(sender, Integer.toString(i) + "\n" + App.DATA);
        }
        sender.close();
    }

    void send(Sender sender, final String message) {
        sender.send(message, new IoCallback() {
            @Override
            public void onComplete(HttpServerExchange exchange, Sender sender) {
            }

            @Override
            public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
                exception.printStackTrace();
            }
        });
    }
}