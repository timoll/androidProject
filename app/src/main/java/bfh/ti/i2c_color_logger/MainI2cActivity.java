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

package bfh.ti.i2c_color_logger;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class MainI2cActivity extends Activity {
	LineChart chart;// = (LineChart) findViewById(R.id.chart);
	int[] mColors = ColorTemplate.VORDIPLOM_COLORS;
	/*ArrayList<ILineDataSet> dataSets=new ArrayList<ILineDataSet>();
	dataSets.add(setComp1);
	dataSets.add(setComp2);

	ArrayList<String> xVals = new ArrayList<String>();
	xVals.add("1.Q"); xVals.add("2.Q"); xVals.add("3.Q"); xVals.add("4.Q");
	LineData data = new LineData(xVals, dataSets);
	/* MCP9800 Register pointers */

	private static final char MCP9800_CONFIG = 0x01;    /* Sensor Configuration Register */



	private static final char COMMAND_BYTE = 0x80;    /* Sensor Configuration Register */


	/* Sensor Configuration Register Bits */
	private static final char MCP9800_12_BIT = 0x60;

	/* i2c Address of MCP9802 device */
	private static final char MCP9800_I2C_ADDR = 0x39;

	/* i2c device file name */
	private static final String MCP9800_FILE_NAME = "/dev/i2c-3";

	int counter1;
	I2C i2c;
	int[] i2cCommBuffer = new int[16];
	int fileHandle;
	Timer timer;
	double TempC;
	int Temperature;
	int entryCount;
	double green;
	double red;
	double blue;
	double clear;
	boolean recording=true;
	boolean oldT2=false;
	double xyz;
	Queue<Double> greenList;
	boolean reset=false;

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
	  
	 /* Instantiate the new i2c device */
		i2c = new I2C();

	 /* Open the i2c device */
		fileHandle = i2c.open(MCP9800_FILE_NAME);

	 /* Set the I2C slave address for all subsequent I2C device transfers */
		i2c.SetSlaveAddress(fileHandle, MCP9800_I2C_ADDR);
		i2cCommBuffer[0]=0x03;
		i2c.write(fileHandle,i2cCommBuffer,1);
	 /* Setup i2c buffer for the configuration register */
		i2cCommBuffer[0] = MCP9800_CONFIG;
		i2cCommBuffer[1] = MCP9800_12_BIT;
		i2c.write(fileHandle, i2cCommBuffer, 2);

		i2c.close(fileHandle);

		timer = new Timer();
		myTimerTask = new MyTimerTask();

		timer.schedule(myTimerTask, 0, 100);
		String[] shellCmd = {"/system/bin/sh","-c", String.format("/data/initWhiteLed")};
		try
		{
			Runtime.getRuntime().exec(shellCmd);

		}
		catch (IOException e)
		{
		}
	/* CHARTSTUFF*/
		chart=(LineChart) findViewById(R.id.chart);
		//chart = (LineChart) findViewById(R.id.chart1);
		//chart.setOnChartValueSelectedListener(this);
		chart.setDrawGridBackground(false);
		chart.setDescription("RGB");
		chart.setData(new LineData());
		chart.invalidate();

		addDataSet("red");
		addDataSet("green");
		addDataSet("blue");

		i2c = new I2C();
		fileHandle = i2c.open(MCP9800_FILE_NAME);
		i2c.SetSlaveAddress(fileHandle, MCP9800_I2C_ADDR);
		i2cCommBuffer[0] = COMMAND_BYTE;
		i2cCommBuffer[1] = 0x03;
		i2c.write(fileHandle, i2cCommBuffer, 2);


	}


	private void addEntry(double red, double green,double blue) {
		LineData data = chart.getData();
		if(reset){
			chart.setData(new LineData());
			addDataSet("red");
			addDataSet("green");
			addDataSet("blue");
			entryCount=0;
			reset=false;
		}
		if (data != null) {

			ILineDataSet set0 = data.getDataSetByIndex(0);
			ILineDataSet set1 = data.getDataSetByIndex(1);
			ILineDataSet set2 = data.getDataSetByIndex(2);
			data.addXValue(Integer.toString(entryCount));


			data.addEntry(new Entry((float) red, entryCount), 0);
			data.addEntry(new Entry((float) green, entryCount), 1);
			data.addEntry(new Entry((float) blue, entryCount), 2);

			if(set0.getEntryCount()>30) {
				set0.removeFirst();
				set1.removeFirst();
				set2.removeFirst();

			}

		    chart.setVisibleXRangeMaximum(28);
			chart.moveViewTo(data.getXValCount() - 15, 50f, AxisDependency.RIGHT);
			YAxis yAxis=chart.getAxisLeft();
			XAxis xAxis =chart.getXAxis();
			data.calcMinMax(0,30);
			yAxis.setAxisMaxValue(data.getYMax() + 1);
			yAxis.setAxisMinValue(data.getYMin() - 1);
			xAxis.setAxisMinValue((float) entryCount - 30);
			xAxis.setAxisMaxValue((float)entryCount);

			chart.notifyDataSetChanged();

			chart.invalidate();

			entryCount++;

		}
	}
	private void removeLastEntry(int index) {

		LineData data = chart.getData();

		if(data != null) {

			ILineDataSet set = data.getDataSetByIndex(index);

			if (set != null) {

				Entry e = set.getEntryForXIndex(set.getEntryCount() - 1);

				data.removeEntry(e, 0);
				// or remove by index
				// mData.removeEntry(xIndex, dataSetIndex);

			}
		}
	}

	private void addDataSet(String dataset) {

		LineData data = chart.getData();

		if(data != null) {

			// create 10 y-vals
			ArrayList<Entry> yVals = new ArrayList<Entry>();

			for (int i = 0; i < data.getXValCount(); i++) {
				yVals.add(new Entry(0, i));
			}

			LineDataSet set = new LineDataSet(yVals, dataset);
			set.setLineWidth(2.5f);
			set.setCircleRadius(4.5f);

			switch (dataset){
				case "red":
					set.setColor(Color.RED);
					set.setCircleColor(Color.RED);
					set.setHighLightColor(Color.RED);
					set.setValueTextSize(10f);
					set.setValueTextColor(Color.RED);
					set.setLabel("Red");

					break;
				case "blue":
					set.setColor(Color.BLUE);
					set.setCircleColor(Color.BLUE);
					set.setHighLightColor(Color.BLUE);
					set.setValueTextSize(10f);
					set.setValueTextColor(Color.BLUE);
					set.setLabel("Blue");
					break;
				case "green":
					set.setColor(Color.GREEN);
					set.setCircleColor(Color.GREEN);
					set.setHighLightColor(Color.GREEN);
					set.setValueTextSize(10f);
					set.setValueTextColor(Color.GREEN);
					set.setLabel("Green");
					break;
			}



			data.addDataSet(set);
			chart.notifyDataSetChanged();
			chart.invalidate();
		}
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
		i2c.close(fileHandle);
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

		/* Open the i2c device */

		@Override
		public void run() {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {


					counter1++;


     /* Setup i2c buffer for the configuration register */

					red = channel(2);
					double bigest = red;
					green = channel(1);
					if (green > bigest) {
						bigest = green;
					}
					blue = channel(3);
					if (blue > bigest) {
						bigest = blue;
					}
					clear = channel(4);
					if (clear > bigest) {
						bigest = clear;
					}

					int normalizedRed = (int) (red / bigest * 256);
					int normalizedGreen = (int) (green / bigest * 256);
					int normalizedBlue = (int) (blue / bigest * 256);

	  /* Close the i2c file */

					String taster = gpio.read_value("49");
					if (taster.contains("1")) {
						gpio.write_value("65", '0');
						gpio.write_value("61", '1');
					} else {
						gpio.write_value("65", '1');
						gpio.write_value("61", '0');
					}
					taster = gpio.read_value("7");
					if(taster.contains("0")){
						reset=true;
					}
					taster =gpio.read_value("112");
					if(taster.contains("0")){
						if(!oldT2){
							oldT2=true;
							recording=!recording;
						}
					} else {
						if(oldT2){
							oldT2=false;
						}
					}
					if (counter1 >= 10 && recording) {
						counter1 = 0;
						addEntry(red, green, blue);

					}

				}
			});
		}
	}
}

