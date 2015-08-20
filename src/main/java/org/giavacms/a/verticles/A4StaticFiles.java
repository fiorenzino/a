package org.giavacms.a.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.giavacms.a.utils.Runner;

/**
 * Created by fiorenzo on 20/08/15.
 */
public class A4StaticFiles extends AbstractVerticle
{
   Logger logger = LoggerFactory.getLogger(A4StaticFiles.class.getName());
   Router router;

   public A4StaticFiles()
   {

   }

   public static void main(String[] args)
   {
      // We set this property to prevent Vert.x caching files loaded from the classpath on disk
      // This means if you edit the static files in your IDE then the next time they are served the new ones will
      // be served without you having to restart the main()
      // This is only useful for development - do not use this in a production server
      System.setProperty("vertx.disableFileCaching", "true");
      System.setProperty("cwd", "/Users/fiorenzo/a/src/main/resources");
      Runner.run(A4StaticFiles.class);
   }

   public A4StaticFiles(Router router)
   {
      this.router = router;
   }

   @Override
   public void start()
   {

      if (router == null)
      {

         router = Router.router(vertx);
      }

      // Serve the static pages
      router.route("/a4/*").handler(StaticHandler.create());

      vertx.createHttpServer().requestHandler(router::accept).listen(8080);

      System.out.println("static Server is started");

   }

}
