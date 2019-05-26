####Overview
This repository is a conglomerate of 4 parts of the project.   
Firstly there are the twitterpipeline and the frontend which both represent parts of the programming work done.  
Secondly there are 2 repositories which contain saltstack formulas which are easy to run once salt is installed.  
####twitterpipeline
twitterpipeline is the backend part which will run on a storm production cluster (hosted on AWS). It will do the calcutlations and data transformations that were needed to process the data   
https://bitbucket.org/kingpfogel/softwareprojekt-storm/src/master/twitterpipeline/
####frontend
the frontend project is containing code that will run on a local machine to fetch the transformed data from the backend and creating a live-graph.   
https://bitbucket.org/kingpfogel/softwareprojekt-storm/src/master/frontend/   
frontend needs a hosts.txt which holds the information of all supervisor. this information is needed such that the frontend knows where to look for the gathered data
####Saltstack repositories
Those two saltstack formulas were adjusted to instantly work with "Amazon Linux AMI 2018.03.0 (ami-03a71cec707bfc3d7)" that have a lot of programs preinstalled (java, python and other features).
The only preparational work that needs to be done is to follow the instructions to install salt and to configure salt and run it with the given top file
####Salt-master (can also have a second identity as a minion)  
Run the prepare.sh to configure the salt-master.
####Salt-minion
Run the following commands:    
sudo yum update   
sudo yum install https://repo.saltstack.com/yum/amazon/salt-amzn-repo-latest.amzn1.noarch.rpm   
sudo yum clean expire-cache   
sudo yum install salt-minion   

For every minion there is one file to edit that is the '/etc/salt/minion', where you have to insert the following:      
master: "ip of the salt master"   
####Possible issues
1. java_home is not correctly set in a configuration file.   
2. storm will be installed under /opt/storm. This directory is owned by root:root. this can be changed by using: sudo chown ec2-user:ec2-user -R /opt/storm *    
which is necessary in order to let the frontend be able to access the outputfile.