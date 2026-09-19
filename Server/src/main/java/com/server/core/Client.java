package com.server.core ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * @author fazega
 * @author kratisto
 */
public class Client {

    private Socket s;
    private BufferedReader br;
    private PrintWriter pw;
    private int noresponse=0, liste=0;
    private Account compte;

    public Client(Socket player, int liste) throws IOException
    {
        this.liste=liste;
        s = player;
        br = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
        pw = new PrintWriter(s.getOutputStream());
        compte = new Account();
    }

    public String receiveFromClient() throws IOException
    {
        String tmp = null;
        try
        {
            this.getS().setSoTimeout(1);
            tmp = this.br.readLine();
            noresponse = 0;
        }

        catch (SocketTimeoutException ex)
        {
            // Si on chope l'erreur comme quoi le socket n'a pas repondu
            if(noresponse<10000)
            {
                //On incremente un compteur
                noresponse++;
            }
            else
            {
                System.out.println("Le client "+compte.getName()+" ne repond plus !");
                this.disconnect();
            }
        }
        catch (IOException ex)
        {
            this.disconnect();
        }
        return tmp;
    }

    public void sendMessage(String message)
    {
        pw.println(message);
        pw.flush();
    }

    public void disconnect()
    {
        try {
            ServerSingleton.getInstance().deconnexion(this);
            this.br.close();
            this.pw.close();
            this.s.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public Socket getS() {
        return s;
    }

    public void setS(Socket s) {
        this.s = s;
    }

    public BufferedReader getBr() {
        return br;
    }

    public void setBr(BufferedReader br) {
        this.br = br;
    }

    public PrintWriter getPw() {
        return pw;
    }

    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }

    public Account getCompte() {
        return compte;
    }

    public void setCompte(Account compte) {
        this.compte = compte;
    }

    public int getListe() {
        return liste;
    }
}
