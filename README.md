###Overview
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

#### Maybe you have to start/restart the services
sudo service salt-minion start    
sudo service salt-master start
#### Accepting the minions on the salt-master
To list all accepted and unaccepted minion keys:   
sudo salt-key -L    
To accept unaccepted minion keys:   
sudo salt-key -A  
#### Before running the top.sls, edit the storm.yaml that will be installed:
change the ip address of the zookeeper and of the nimbus.seeds
/srv/formulas/salt-formula-storm/storm/files/storm.yaml
#### Run the top.sls
cd /srv/formulas
sudo salt '\*' state.apply
#### Change owner of the storm directory on nimbus and supervisor
sudo chown ec2-user:ec2-user -R storm*
#### Start nimbus/ui and the supervisor(s) and launch your jar from the nimbus machine. The necessary commands are:
/opt/storm/bin/storm nimbus   
/opt/storm/bin/storm ui   
/opt/storm/bin/storm supervisor   
/opt/storm/bin/storm jar example.jar package.ExampleMain <program args>   
####Possible issues
1. java_home is not correctly set in a configuration file.   
2. storm will be installed under /opt/storm. This directory is owned by root:root. this can be changed by using: sudo chown ec2-user:ec2-user -R /opt/storm *    
which is necessary in order to let the frontend be able to access the outputfile.



### Launching AWS-Instances
Login - Use the find services searchfield and enter "EC2"    
Click "Launch instance"    
Configure the instance details (I will stick to my configurations)    
#### Choose AMI
Search for either "ami-0cfbf4f6db41068ac" or "Amazon Linux" version one with Java and Python preinstalled
#### Choose Instance Type
As is (t2.micro)
#### Configure Instance
Change "Number of instances" to 3
#### Add Storage
Next
#### Add Tags
Add tag: Key=name and Value=<a descriptive name>
#### Configure Security Group
##### Allowing all outbound traffic
##### Inbound Rules accessible ports from within the security group
2181 Zookeeper   
6700-6703 default supervisor.slot.ports    
6627 nimbus.thrift.port   
4505-4506 salt    
3772-3774 not sure if necessary, some drpc ports   
##### Inbound Rules (open to the world - any IP address (or at least open to the ip address of the frontend application))
22 ssh   
8080 ui.port   
8000 logviewer port   
#### Review
Launch the instances   