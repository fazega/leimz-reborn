/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.core;

/**
 *
 * @author Utilisateur
 */
public class ServerSingleton {
    private static Server instance;

    private static void createAnInstance()
    {
          if(null == instance)
          {
                    instance = new Server();
          }
    }

    public static Server getInstance()
    {
          if(null == instance)
          {
                    createAnInstance();
          }
          return instance;
    }
}
