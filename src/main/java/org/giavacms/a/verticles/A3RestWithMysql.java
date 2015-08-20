package org.giavacms.a.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.giavacms.a.utils.Runner;

/**
 * Created by fiorenzo on 20/08/15.
 */
public class A3RestWithMysql extends AbstractVerticle
{

   Router router;
   JDBCClient client;

   public static void main(String[] args)
   {
      Runner.run(A3RestWithMysql.class);
   }

   public A3RestWithMysql(Router router)
   {
      this.router = router;
   }

   public A3RestWithMysql()
   {

   }

   @Override public void start() throws Exception
   {
      if (router == null)
      {
         this.router = Router.router(vertx);
      }
      System.out.println("start start vertx.createHttpServer");
      client = JDBCClient.createShared(vertx, new JsonObject()
               .put("url", "jdbc:mysql://localhost/test")
               .put("driver_class", "com.mysql.jdbc.Driver")
               .put("user", "root")
               .put("password", "flower"));

      router.route().handler(BodyHandler.create());

      // in order to minimize the nesting of call backs we can put the JDBC connection on the context for all routes
      // that match /products
      // this should really be encapsulated in a reusable JDBC handler that uses can just add to their app
      router.route("/products*").handler(routingContext -> client.getConnection(res -> {
         if (res.failed())
         {
            routingContext.fail(res.cause());
         }
         else
         {
            SQLConnection conn = res.result();

            // save the connection on the context
            routingContext.put("conn", conn);

            // we need to return the connection back to the jdbc pool. In order to do that we need to close it, to keep
            // the remaining code readable one can add a headers end handler to close the connection. The reason to
            // choose the headers end is that if the close of the connection or say for example end of transaction
            // results in an error, it is still possible to return back to the client an error code and message.
            routingContext.addHeadersEndHandler(done -> conn.close(close -> {
               if (close.failed())
               {
                  done.fail(close.cause());
               }
               else
               {
                  done.complete();
               }
            }));

            routingContext.next();
         }
      })).failureHandler(routingContext -> {
         SQLConnection conn = routingContext.get("conn");
         if (conn != null)
         {
            conn.close(v -> {
            });
         }
      });

      router.get("/products/:productID").handler(this::handleGetProduct);
      router.post("/products").handler(this::handleAddProduct);
      router.get("/products").handler(this::handleListProducts);

      vertx.createHttpServer().requestHandler(router::accept).listen(8080);
      System.out.println("end start vertx.createHttpServer");
   }

   private void handleGetProduct(RoutingContext routingContext)
   {
      System.out.println("start handleGetProduct");
      String productID = routingContext.request().getParam("productID");
      HttpServerResponse response = routingContext.response();
      if (productID == null)
      {
         sendError(400, response);
      }
      else
      {
         SQLConnection conn = routingContext.get("conn");

         conn.queryWithParams("SELECT id, name, price, weight FROM products where id = ?",
                  new JsonArray().add(Integer.parseInt(productID)), query -> {
                     if (query.failed())
                     {
                        sendError(500, response);
                     }
                     else
                     {
                        if (query.result().getNumRows() == 0)
                        {
                           sendError(404, response);
                        }
                        else
                        {
                           response.putHeader("content-type", "application/json")
                                    .end(query.result().getRows().get(0).encode());
                        }
                     }
                  });
      }
      System.out.println("end handleGetProduct");
   }

   private void handleAddProduct(RoutingContext routingContext)
   {
      System.out.println("start handleAddProduct");
      HttpServerResponse response = routingContext.response();

      SQLConnection conn = routingContext.get("conn");
      JsonObject product = routingContext.getBodyAsJson();

      conn.updateWithParams("INSERT INTO products (name, price, weight) VALUES (?, ?, ?)",
               new JsonArray().add(product.getString("name")).add(product.getFloat("price"))
                        .add(product.getInteger("weight")), query -> {
                  if (query.failed())
                  {
                     sendError(500, response);
                  }
                  else
                  {
                     response.end();
                  }
               });
      System.out.println("end handleAddProduct");
   }

   private void handleListProducts(RoutingContext routingContext)
   {
      System.out.println("start handleListProducts");
      HttpServerResponse response = routingContext.response();
      SQLConnection conn = routingContext.get("conn");
      System.out.println("start conn");
      if (conn == null)
      {
         System.out.println("query handleListProducts: sendError");
         sendError(500, response);
      }
      conn.query("SELECT id, name, price, weight FROM products", query -> {
         System.out.println("query handleListProducts:" + query.toString());
         if (query.failed())
         {
            System.out.println("query handleListProducts: sendError");
            sendError(500, response);
         }
         else
         {
            JsonArray arr = new JsonArray();
            query.result().getRows().forEach(arr::add);
            System.out.println("query handleListProducts: Ok");
            routingContext.response().putHeader("content-type", "application/json").end(arr.encode());
         }
      });
      System.out.println("exit handleListProducts");
   }

   private void sendError(int statusCode, HttpServerResponse response)
   {
      response.setStatusCode(statusCode).end();
   }

}
