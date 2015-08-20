package org.giavacms.a.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import org.giavacms.a.utils.Runner;

/**
 * Created by fiorenzo on 19/08/15.
 */
public class A1 extends AbstractVerticle
{

   Router router;

   public A1()
   {

   }

   public static void main(String[] args)
   {
      Runner.run(A1.class);
   }

   public A1(Router router)
   {
      this.router = router;
   }

   @Override
   public void start(Future<Void> startFuture) throws Exception
   {
      if (router == null)
      {

         router = Router.router(vertx);
      }
      router.route("/a1").handler(routingContext -> {
         routingContext.response().putHeader("content-type", "text/html").end("Hello World A1!");
      });

      vertx.createHttpServer().requestHandler(router::accept).listen(8080);
   }
}
