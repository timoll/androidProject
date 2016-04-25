# androidProject
##Description
Simple Android project that displays the data of the color sensor of the BBB-BFH-Cape on a graph and pulls the color information frequently.

The graph displays the last 29 recordings and scales acording to the highest and lowest value.

##Enable white LED
Next to the color sensor is a white led. To be able to use that led it is necessairy to run a few commands first on the BBB:

<code>su</code>

<code>echo 65 > /sys/class/gpio/export</code>

<code>echo out > /sys/class/gpio/gpio65/direction</code>

<code>chmod 0666 /sys/class/gpio/gpio65/value</code>

##Usage

The white led can be controlled with the taster T1, additionaly the LED L1 mirrors the value of the white should have(if you don't followed the previous steps)

The Taster T2 toggles the recording if pressed longer than 100ms.

The Taster T4 deletes all recorded values if pressed longer than 100ms.

for best formatting read the README on https://github.com/timoll/androidProject
