# Undertow Streaming examples

This provides streaming from an `HttpHandler` and a `Servlet`, both
from the request handling thread and from a separate thread.

To run:

    mvn -Prun test

That will run `StreamingHandler`, and will stream from the request
thread. Visiting http://localhost:8080 should give you streamed content.

To stream from a separate thread, do:

    mvn -Pon-thread,run test

Unfortunately, that doesn't work - accessing the `Sender` from another
thread results in:

    java.nio.channels.ClosedChannelException
          at io.undertow.channels.DetachableStreamSinkChannel.write(DetachableStreamSinkChannel.java:184)
          at io.undertow.server.HttpServerExchange$WriteDispatchChannel.write(HttpServerExchange.java:1822)
          at io.undertow.io.AsyncSenderImpl.send(AsyncSenderImpl.java:217)
          at io.undertow.io.AsyncSenderImpl.send(AsyncSenderImpl.java:294)
          at io.undertow.io.AsyncSenderImpl.send(AsyncSenderImpl.java:270)
          at tcrawley.undertowstreaming.StreamingHandler.send(StreamingHandler.java:41)
          at tcrawley.undertowstreaming.StreamingHandler.stream(StreamingHandler.java:35)
          at tcrawley.undertowstreaming.StreamingHandler$1.run(StreamingHandler.java:19)
          at java.lang.Thread.run(Thread.java:745)

To run as a servlet, streaming from the request thread:

    mvn -Pservlet,run test

To run as a servlet, streaming from a separate thread (which works for a servlet):

    mvn -Pon-thread,servlet,run test
