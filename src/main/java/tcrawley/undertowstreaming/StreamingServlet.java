package tcrawley.undertowstreaming;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(asyncSupported = true)
public class StreamingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.startAsync();
        final PrintWriter writer = res.getWriter();
        writer.write("start\n");
        writer.flush();
        Runnable action = new Runnable() {
            @Override
            public void run() {
                stream(writer);
            }
        };
        if (App.isOnThread()) {
            System.out.println("Streaming from another thread");
            (new Thread(action)).start();
        } else {
            action.run();
        }
    }

    void stream(PrintWriter stream) {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
            stream.write(Integer.toString(i) + "\n" + App.DATA);
            stream.flush();
        }
        stream.close();
    }
}
