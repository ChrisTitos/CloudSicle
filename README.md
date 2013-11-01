CloudSicle
==========

A cloud-based implementation of [gifsicle](http://www.lcdf.org/gifsicle/) for the TU Delft IN4392 Cloud Computing course. 
CloudSicle runs on the DAS-4 cloud infrastructure.

#How to run
CloudSicle consists of three runnable jar files that can be found in the [dist](https://github.com/ChrisTitos/CloudSicle/tree/master/dist) directory.


You need to specify your log-in credentials for DAS-4 in a config.txt file. In this file you can also specify communication ports for normal messages and file transfers.

##Master
The master should be deployed by sending the __master.jar__, the
__slave.jar__ and the __config.txt__ to your DAS-4 node. The service
is then started by running the master.jar. 

To stop the service you can simply press the enterkey to clean up all allocated Virtual Machines. Should the application crash or be forcefully terminated, you will have to shut down all the allocated Virtual Machines manually.

##Client
A client can is started by placing the __client.jar__ and __config.txt__ in the same folder and then run the client.jar. This will open a small user interface. Here you can select files, and the DAS-4 server entry point. When the
request has been processed the resulting .tar.gz compressed file will appear in the your folder.

#Report
A PDF and .tex file of our report can be found in the [report](https://github.com/ChrisTitos/CloudSicle/tree/master/report) directory.
