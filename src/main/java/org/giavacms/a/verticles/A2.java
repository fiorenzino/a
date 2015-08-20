package org.giavacms.a.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import org.giavacms.a.utils.Runner;

/**
 * Created by fiorenzo on 19/08/15.
 */
public class A2 extends AbstractVerticle
{

   Router router;

   public A2(Router router)
   {
      this.router = router;
   }

   public A2()
   {
   }

   public static void main(String[] args)
   {
      Runner.run(A2.class);
   }


   @Override
   public void start(Future<Void> startFuture) throws Exception
   {
      if (router == null)
      {
         router = Router.router(vertx);
      }
      router.route("/a2").handler(routingContext -> {
         routingContext.response().putHeader("content-type", "text/html").end("Hello World A2!");
      });

      vertx.createHttpServer().requestHandler(router::accept).listen(8080);
   }
}
