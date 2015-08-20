package org.giavacms.a.old;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by fiorenzo on 09/08/15.
 */
public class MyFirstVerticle extends AbstractVerticle
{

   // Store our product
   private Map<Integer, Whisky> products = new LinkedHashMap<>();

   // Create some product
   private void createSomeData()
   {
      Whisky bowmore = new Whisky("Bowmore 15 Years Laimrig", "Scotland, Islay");
      products.put(bowmore.getId(), bowmore);
      Whisky talisker = new Whisky("Talisker 57° North", "Scotland, Island");
      products.put(talisker.getId(), talisker);
   }

   private void getAll(RoutingContext routingContext)
   {
      routingContext.response()
               .putHeader("content-type", "application/json; charset=utf-8")
               .end(Json.encodePrettily(products.values()));
   }

   private void addOne(RoutingContext routingContext)
   {
      final Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(),
               Whisky.class);
      products.put(whisky.getId(), whisky);
      routingContext.response()
               .setStatusCode(201)
               .putHeader("content-type", "application/json; charset=utf-8")
               .end(Json.encodePrettily(whisky));
   }

   @Override
   public void start(Future<Void> future)
   {

      createSomeData();

      Router router = Router.router(vertx);

      // Bind "/" to our hello message - so we are still compatible.
      router.route("/").handler(routingContext -> {
         HttpServerResponse response = routingContext.response();
         response
                  .putHeader("content-type", "text/html")
                  .end("<h1>Hello from my first Vert.x 3 application</h1>");
      });

      router.get("/api/whiskies").handler(this::getAll);

      // Create the HTTP server and pass the "accept" method to the request handler.
      vertx
               .createHttpServer()
               .requestHandler(router::accept)
               .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                           if (result.succeeded())
                           {
                              future.complete();
                           }
                           else
                           {
                              future.fail(result.cause());
                           }
                        }
               );

   }

   @Override
   public void stop(Future<Void> future) throws Exception
   {
      super.stop(future);
   }
}