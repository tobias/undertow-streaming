package tcrawley.undertowstreaming;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

import javax.servlet.ServletException;

public class App {

    public final static String DATA = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris. Maecenas congue ligula ac quam viverra nec consectetur ante hendrerit. Donec et mollis dolor. Praesent et diam eget libero egestas mattis sit amet vitae augue. Nam tincidunt congue enim, ut porta lorem lacinia consectetur. Donec ut libero sed arcu vehicula ultricies a non tortor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean ut gravida lorem. Ut turpis felis, pulvinar a semper sed, adipiscing id dolor. Pellentesque auctor nisi id magna consequat sagittis. Curabitur dapibus enim sit amet elit pharetra tincidunt feugiat nisl imperdiet. Ut convallis libero in urna ultrices accumsan. Donec sed odio eros. Donec viverra mi quis quam pulvinar at malesuada arcu rhoncus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. In rutrum accumsan ultricies. Mauris vitae nisi at sem facilisis semper ac in est.\n";

    public static void main(String[] args) throws Exception {
        new App().run();
    }

    public static boolean isServlet() {
        return "true".equals(System.getProperty("servlet"));
    }

    public static boolean isOnThread() {
        return "true".equals(System.getProperty("onThread"));
    }

    public void run() throws ServletException {

        Undertow.Builder undertow = Undertow.builder()
                .addHttpListener(8080, "localhost");

        if (isServlet()) {
            System.out.println("Deploying servlet");
            DeploymentInfo servletBuilder = Servlets.deployment()
                    .setClassLoader(App.class.getClassLoader())
                    .setContextPath("/")
                    .setDeploymentName("stream.war")
                    .setIgnoreFlush(false)
                    .addServlets(
                            Servlets.servlet("StreamingServlet", StreamingServlet.class)
                                    .setAsyncSupported(true)
                                    .addMapping("/*"));

            DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
            manager.deploy();
            PathHandler path = Handlers.path(Handlers.redirect("/"))
                    .addPrefixPath("/", manager.start());

            undertow.setHandler(path);
        } else {
            System.out.println("Deploying handler");
            undertow.setHandler(new StreamingHandler());
        }

        undertow.build().start();

        System.out.println("Undertow started on localhost:8080");
    }



}
