package com.server.utils;

public class Chrono
{
    private long time = 0, time_init = 0;
    private Thread t;

    public Chrono()
    {
        //En mode chrono
        t = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while(true)
                {
                    time = System.currentTimeMillis()-time_init;
                }
            }
        });
    }

    public Chrono(long time_delay)
    {
        //En mode minuteur
        time = time_delay;
        t = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while(time >= 0)
                {
                    time = time_init-System.currentTimeMillis();
                }
            }
        });
    }

    public void start()
    {
        time_init = System.currentTimeMillis()+time;
        t.start();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


}
