package org.giavacms.a.old;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;

/**
 * Created by fiorenzo on 19/08/15.
 */
public class AnotherVerticolo extends AbstractVerticle
{

   public void start(Future<Void> startFuture)
   {
      Router router = Router.router(vertx);
      startServer1(router);
      startServer2(router);
   }

   public void startServer1(Router router)
   {

      router.route("/set1").handler(routingContext -> {
         routingContext.response().putHeader("content-type", "text/html").end("Hello World 1!");
      });

      vertx.createHttpServer().requestHandler(router::accept).listen(8080);
   }

   public void startServer2(Router router)
   {

      router.route("/set2").handler(routingContext -> {
         routingContext.response().putHeader("content-type", "text/html").end("Hello World 2!");
      });

      vertx.createHttpServer().requestHandler(router::accept).listen(8080);
   }
}
