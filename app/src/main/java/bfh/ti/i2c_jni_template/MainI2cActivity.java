/*
 ***************************************************************************
 * \brief   Embedded Android I2C Exercise 5.2
 *	        This sample program shows how to use the I2C library.
 *			The program reads the temperature from the MCP9802 sensor
 *			and show the value on the display  
 *
 *	        Only a minimal error handling is implemented.
 * \file    MainI2cActivity.java
 * \version 1.0
 * \date    06.03.2014
 * \author  Martin Aebersold
 *
 * \remark  Last Modifications:
 * \remark  V1.0, AOM1, 06.03.2014
 ***************************************************************************
 */

package bfh.ti.i2c_jni_template;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainI2cActivity extends Activity {

	/* MCP9800 Register pointers */
	private static final char MCP9800_TEMP = 0x00;      /* Ambient Temperature Register */
	private static final char MCP9800_CONFIG = 0x01;    /* Sensor Configuration Register */

	/* Sensor Configuration Register Bits */
	private static final char MCP9800_12_BIT = 0x60;

	/* i2c Address of MCP9802 device */
	private static final char MCP9800_I2C_ADDR = 0x48;

	/* i2c device file name */
	private static final String MCP9800_FILE_NAME = "/dev/i2c-3";


	I2C i2c;
	int[] i2cCommBuffer = new int[16];
	int fileHandle;
	Timer timer;
	double TempC;
	int Temperature;

	double green;
	double red;
	double blue;
	double clear;

	final ShellExecGPIO gpio = new ShellExecGPIO();

	MyTimerTask myTimerTask;
	/* Define widgets */
	TextView textViewTemperature;

	/* Temperature Degrees Celsius text symbol */
	private static final String DEGREE_SYMBOL = "\u2103";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_i2c);
		textViewTemperature = (TextView) findViewById(R.id.textViewTemperature);
	  
	 /* Instantiate the new i2c device */
		i2c = new I2C();

	 /* Open the i2c device */
		fileHandle = i2c.open(MCP9800_FILE_NAME);

	 /* Set the I2C slave address for all subsequent I2C device transfers */
		i2c.SetSlaveAddress(fileHandle, MCP9800_I2C_ADDR);

	 /* Setup i2c buffer for the configuration register */
		i2cCommBuffer[0] = MCP9800_CONFIG;
		i2cCommBuffer[1] = MCP9800_12_BIT;
		i2c.write(fileHandle, i2cCommBuffer, 2);

		i2c.close(fileHandle);

		timer = new Timer();
		myTimerTask = new MyTimerTask();

		timer.schedule(myTimerTask, 0, 100);
		gpio.export("65");
		gpio.gpio_set_direction_out("65");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_i2c, menu);
		return true;
	}

	/*
     * 	(non-Javadoc)
     * @see android.app.Activity#onStop()
     */
	protected void onStop() {
		android.os.Process.killProcess(android.os.Process.myPid());
		finish();
		super.onStop();
	}

	private class MyTimerTask extends TimerTask {
		int channel(int cIndex){
			switch (cIndex){
				case 1:
					i2cCommBuffer[0] = 0xB0;
					break;
				case 2:
					i2cCommBuffer[0] = 0xB2;
					break;
				case 3:
					i2cCommBuffer[0] = 0xB4;
					break;
				case 4:
					i2cCommBuffer[0] = 0xB8;
					break;
				default:
					i2cCommBuffer[0] = 0xB8;
					break;
			}
			i2c.write(fileHandle, i2cCommBuffer, 1);

     /* Read the current temperature from the mcp9800 device */
			i2c.read(fileHandle, i2cCommBuffer, 2);
			return ((256 * i2cCommBuffer[1]) + i2cCommBuffer[0]);
		}
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					i2c = new I2C();

	 /* Open the i2c device */
					fileHandle = i2c.open(MCP9800_FILE_NAME);
					i2c.SetSlaveAddress(fileHandle, 0x39);

     /* Setup i2c buffer for the configuration register */

					red=channel(1);
					double bigest=red;
					green=channel(2);
					if(green>bigest){
						bigest=green;
					}
					blue=channel(3);
					if(blue>bigest){
						bigest=blue;
					}
					clear=channel(4);
					if(clear>bigest){
						bigest=clear;
					}

					int normalizedRed=(int)(red/bigest*256);
					int normalizedGreen=(int)(green/bigest*256);
					int normalizedBlue=(int)(blue/bigest*256);

					findViewById(R.id.BtnColor).setBackgroundColor(Color.rgb(normalizedRed, normalizedGreen, normalizedBlue));
					((Button)findViewById(R.id.BtnColor)).setTextColor(Color.rgb(255 - normalizedRed, 255 - normalizedGreen, 255 - normalizedBlue));
	 /* Close the i2c file */
					i2c.close(fileHandle);
					String taster=gpio.read_value("49");
					if(taster.contains("1")){
						gpio.write_value("65", '0');
					} else {
						gpio.write_value("65",'1');
					}

					/*
					*  add data to a fancy graph bleuer
					* */

				}
			});
		}
	}
}


