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

	 /* Setup mcp9800 register to read the temperature */
		i2cCommBuffer[0] = MCP9800_TEMP;
		i2c.write(fileHandle, i2cCommBuffer, 1);

	 /* Read the current temperature from the mcp9800 device */
		i2c.read(fileHandle, i2cCommBuffer, 2);

	 /* Assemble the temperature values */
		Temperature = ((i2cCommBuffer[0] << 8) | i2cCommBuffer[1]);
		Temperature = Temperature >> 4;

	 /* Convert current temperature to float */
		TempC = 1.0 * Temperature * 0.0625;

     /* Display actual temperature */
		textViewTemperature.setText("Temperature: " + String.format("%3.2f", TempC) + DEGREE_SYMBOL);
		//Color

		i2c.SetSlaveAddress(fileHandle, 0x39);

     /* Setup i2c buffer for the configuration register */
		i2cCommBuffer[0] = 0x03;

		i2c.write(fileHandle, i2cCommBuffer, 1);

		//Green
     /* Setup mcp9800 register to read the temperature */
		i2cCommBuffer[0] = 0xB0;
		i2c.write(fileHandle, i2cCommBuffer, 1);

     /* Read the current temperature from the mcp9800 device */
		i2c.read(fileHandle, i2cCommBuffer, 2);

     /* Assemble the temperature values */
		green = ((256 * i2cCommBuffer[1]) + i2cCommBuffer[0]);

		//Blue
             /* Setup mcp9800 register to read the temperature */
		i2cCommBuffer[0] = 0xB2;
		i2c.write(fileHandle, i2cCommBuffer, 1);

     /* Read the current temperature from the mcp9800 device */
		i2c.read(fileHandle, i2cCommBuffer, 2);

     /* Assemble the temperature values */
		blue = ((256 * i2cCommBuffer[1]) + i2cCommBuffer[0]);

		//Red
             /* Setup mcp9800 register to read the temperature */
		i2cCommBuffer[0] = 0xB4;
		i2c.write(fileHandle, i2cCommBuffer, 1);

     /* Read the current temperature from the mcp9800 device */
		i2c.read(fileHandle, i2cCommBuffer, 2);

     /* Assemble the temperature values */
		red = ((256 * i2cCommBuffer[1]) + i2cCommBuffer[0]);

		findViewById(R.id.BtnColor).setBackgroundColor(Color.rgb(Red, Green, Blue));
		((Button)findViewById(R.id.BtnColor)).setTextColor(Color.rgb(255 - Red, 255 - Green, 255 - Blue));
	 /* Close the i2c file */
		i2c.close(fileHandle);

		timer = new Timer();
		myTimerTask = new MyTimerTask();

		timer.schedule(myTimerTask, 0, 500);

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
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					i2c = new I2C();

	 /* Open the i2c device */
					fileHandle = i2c.open(MCP9800_FILE_NAME);

	 /* Set the I2C slave address for all subsequent I2C device transfers */
					i2c.SetSlaveAddress(fileHandle, MCP9800_I2C_ADDR);

	 /* Setup i2c buffer for the configuration register */
					i2cCommBuffer[0] = MCP9800_CONFIG;
					i2cCommBuffer[1] = MCP9800_12_BIT;
					i2c.write(fileHandle, i2cCommBuffer, 2);

	 /* Setup mcp9800 register to read the temperature */
					i2cCommBuffer[0] = MCP9800_TEMP;
					i2c.write(fileHandle, i2cCommBuffer, 1);

	 /* Read the current temperature from the mcp9800 device */
					i2c.read(fileHandle, i2cCommBuffer, 2);

	 /* Assemble the temperature values */
					Temperature = ((i2cCommBuffer[0] << 8) | i2cCommBuffer[1]);
					Temperature = Temperature >> 4;

	 /* Convert current temperature to float */
					TempC = 1.0 * Temperature * 0.0625;

     /* Display actual temperature */
					textViewTemperature.setText("Temperature: " + String.format("%3.2f", TempC) + DEGREE_SYMBOL);
					//Color

					i2c.SetSlaveAddress(fileHandle, 0x39);

     /* Setup i2c buffer for the configuration register */
					i2cCommBuffer[0] = 0x03;

					i2c.write(fileHandle, i2cCommBuffer, 1);

					//Green
     /* Setup mcp9800 register to read the temperature */
					i2cCommBuffer[0] = 0xB0;
					i2c.write(fileHandle, i2cCommBuffer, 1);

     /* Read the current temperature from the mcp9800 device */
					i2c.read(fileHandle, i2cCommBuffer, 2);

     /* Assemble the temperature values */
					red = ((256 * i2cCommBuffer[0]) + i2cCommBuffer[1]);

					//Blue
             /* Setup mcp9800 register to read the temperature */
					i2cCommBuffer[0] = 0xB2;
					i2c.write(fileHandle, i2cCommBuffer, 1);

     /* Read the current temperature from the mcp9800 device */
					i2c.read(fileHandle, i2cCommBuffer, 2);

     /* Assemble the temperature values */
					green = ((256 * i2cCommBuffer[0]) + i2cCommBuffer[1]);

					//Red
             /* Setup mcp9800 register to read the temperature */
					i2cCommBuffer[0] = 0xB4;
					i2c.write(fileHandle, i2cCommBuffer, 1);

     /* Read the current temperature from the mcp9800 device */
					i2c.read(fileHandle, i2cCommBuffer, 2);

     /* Assemble the temperature values */
					blue = ((256 * i2cCommBuffer[0]) + i2cCommBuffer[1]);
					if(red<=256){
						red=255;
					}
					if(green<=256){
						green=255;
					}
					if(blue<=256){
						blue=255;
					}
					findViewById(R.id.BtnColor).setBackgroundColor(Color.rgb((int)red, (int)green, (int)blue));
					((Button)findViewById(R.id.BtnColor)).setTextColor(Color.rgb(255 - red, 255 - green, 255 - Blue));
	 /* Close the i2c file */
					i2c.close(fileHandle);

				}
			});
		}
	}
}


