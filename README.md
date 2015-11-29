# SwitchISPMaven

This application monitors the ISP connection and switches to a backup ISP automatically if the primary ISP is not reachable for a set period of time. 
An e-mail is send when a switch-over occurs. This application must be implemented as a Windows service. It has a remote interface using JConsole.

The jar must be deployed on an always-on multi homed PC or Windows server. Connect to the app with a browser.
This app is developed in Java and uses the Java JMX monitor as the interface.

