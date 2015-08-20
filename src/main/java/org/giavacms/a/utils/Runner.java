package org.giavacms.a.utils;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.io.File;
import java.util.function.Consumer;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Runner
{

   private static final String EXAMPLES_JAVA_DIR = "/src/main/java/";

   public static void run(Class clazz)
   {
      runJavaExample(EXAMPLES_JAVA_DIR, clazz, new VertxOptions().setClustered(false));
   }

   public static void runJavaExample(String prefix, Class clazz, boolean clustered)
   {
      runJavaExample(prefix, clazz, new VertxOptions().setClustered(clustered));
   }

   public static void runJavaExample(String prefix, Class clazz, VertxOptions options)
   {
      String folder = prefix + clazz.getPackage().getName().replace(".", "/");
      runExample(folder, clazz.getName(), options);
   }

   public static void runJavaExample(String prefix, Class clazz, DeploymentOptions deploymentOptions)
   {
      String folder = prefix + clazz.getPackage().getName().replace(".", "/");
      runExample(folder, clazz.getName(), new VertxOptions(), deploymentOptions);
   }


   public static void runExample(String folder, String verticleID, boolean clustered)
   {
      runExample(folder, verticleID, new VertxOptions().setClustered(clustered));
   }

   public static void runExample(String folder, String verticleID, VertxOptions options)
   {
      runExample(folder, verticleID, options, null);
   }

   public static void runExample(String folder, String verticleID, VertxOptions options,
            DeploymentOptions deploymentOptions)
   {
      System.setProperty("vertx.cwd", folder);
      Consumer<Vertx> runner = vertx -> {
         try
         {
            if (deploymentOptions != null)
            {
               vertx.deployVerticle(verticleID, deploymentOptions);
            }
            else
            {
               vertx.deployVerticle(verticleID);
            }
         }
         catch (Throwable t)
         {
            t.printStackTrace();
         }
      };
      if (options.isClustered())
      {
         Vertx.clusteredVertx(options, res -> {
            if (res.succeeded())
            {
               Vertx vertx = res.result();
               runner.accept(vertx);
            }
            else
            {
               res.cause().printStackTrace();
            }
         });
      }
      else
      {
         Vertx vertx = Vertx.vertx(options);
         runner.accept(vertx);
      }
   }

}