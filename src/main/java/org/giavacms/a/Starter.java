package org.giavacms.a;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import org.giavacms.a.utils.Runner;
import org.giavacms.a.verticles.A1;
import org.giavacms.a.verticles.A2;
import org.giavacms.a.verticles.A3RestWithMysql;
import org.giavacms.a.verticles.A4StaticFiles;

/**
 * Created by fiorenzo on 19/08/15.
 */
public class Starter extends AbstractVerticle
{

   Logger logger = LoggerFactory.getLogger(Starter.class.getName());

   public void start(Future<Void> startFuture) throws Exception
   {
      logger.info("start");
      Router router = Router.router(vertx);
      A1 a1 = new A1(router);
      A2 a2 = new A2(router);
      A3RestWithMysql a3 = new A3RestWithMysql(router);
      A4StaticFiles a4 = new A4StaticFiles(router);
      vertx.deployVerticle(a1);
      vertx.deployVerticle(a2);
      vertx.deployVerticle(a3);
      vertx.deployVerticle(a4);
   }

   @Override
   public void stop(Future<Void> stopFuture) throws Exception
   {
      logger.info("stop");
   }

   public static void main(String[] args)
   {
      Runner.run(Starter.class);
   }

}
