# androidProject

Simple Android project that displays the data of the color sensor of the BBB-BFH-Cape on a graph and pulls the color information frequently

Next to the color sensor is a white led. To be able to use that led it is necessairy to run a few commands first on the BBB:

<code>su</code>

<code>echo 65 > /sys/class/gpio/export</code>

<code>echo out > /sys/class/gpio/gpio65/direction</code>

<code>chmod 0666 /sys/class/gpio/gpio65/value</code>

With that in place the led can be controlled with the taster T1

The Data gets deleted after 100 entries. Taster T4 resets the data immediately.
