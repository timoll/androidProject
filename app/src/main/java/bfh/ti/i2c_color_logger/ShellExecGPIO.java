package bfh.ti.i2c_color_logger;

/*
 ***************************************************************************
 * \brief   Embedded-Android (BTE5484)
 *	    	GPIO Exercise
 *          Java Class to access the GPIOs by a shell process.
 *
 * \file    ShellExecGPIO.java
 * \version 1.0
 * \date    24.01.2014
 * \author  Martin Aebersold
 *
 * \remark  Last Modifications:
 * \remark  V1.0, AOM1, 24.01.2014   Initial release
 ***************************************************************************
 */

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStreamReader;

/*
 * Java class for accessing the GPIOs via sysfs
 */

public class ShellExecGPIO
{
    public String gpio;
    public String value;

    /*
     *  Export a gpio
     */
    public boolean export(String gpio)
    {
        String[] shellCmd = {"/system/bin/sh","-c", String.format("echo %s > /sys/class/gpio/export", gpio)};
        try
        {
            Runtime.getRuntime().exec(shellCmd);
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    /*
     * Unexport a gpio
     */
    public boolean unexport(String gpio)
    {
        String[] shellCmd = {"/system/bin/sh","-c", String.format("echo %s > /sys/class/gpio/unexport", gpio)};
        try
        {
            Runtime.getRuntime().exec(shellCmd);
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    /*
     * Set gpio direction to output
     */
    public boolean gpio_set_direction_out(String gpio)
    {
        String[] shellCmd = {"/system/bin/sh","-c", String.format("echo out > /sys/class/gpio/gpio%s/direction", gpio)};
        try
        {
            Runtime.getRuntime().exec(shellCmd);
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    /*
     * Set gpio direction to input
     */
    public boolean set_direction_in(String gpio)
    {
        String[] shellCmd = {"/system/bin/sh","-c", String.format("echo in > /sys/class/gpio/gpio%s/direction", gpio)};
        try
        {
            Runtime.getRuntime().exec(shellCmd);
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    /*
     * Write gpio value
     */
    public boolean write_value(String gpio, char value)
    {
        String[] shellCmd = {"/system/bin/sh","-c", String.format("echo %c > /sys/class/gpio/gpio%s/value", value, gpio)};
        try
        {
            Runtime.getRuntime().exec(shellCmd);
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    /*
     * Read gpio value
     */
    public String read_value(String gpio)
    {
        Process p;
        String[] shellCmd = {"/system/bin/sh","-c", String.format("cat /sys/class/gpio/gpio%s/value", gpio)};
        try
        {
            p = Runtime.getRuntime().exec(shellCmd);
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder text = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
            {
                text.append(line);
            }
            return text.toString();
        }
        catch (IOException e)
        {
            return "";
        }
    }
}