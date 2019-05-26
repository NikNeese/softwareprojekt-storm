## Project twitterpipeline
## Setting up a Storm-Cluster using Salt and AWS-EC2-instances (amazon linux)
## 1 Launching AWS-Instances
Login - Use the find services searchfield and enter "EC2"    
Click "Launch instance"    
Configure the instance details (I will stick to my configurations)    
#### 1. Choose AMI
Search for either "ami-0cfbf4f6db41068ac" or "Amazon Linux" version one with Java and Python preinstalled
#### 2. Choose Instance Type
As is (t2.micro)
#### 3. Configure Instance
Change "Number of instances" to 3
#### 4. Add Storage
Next
#### 5. Add Tags
Add tag: Key=name and Value=<a descriptive name>
#### 6. Configure Security Group
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
#### 7. Review
Launch the instances   
### 2 Installing Salt 
#### Downloading and installing
sudo yum install https://repo.saltstack.com/yum/amazon/salt-amzn-repo-latest.amzn1.noarch.rpm   
sudo yum clean expire-cache   
sudo yum install salt-master   
sudo yum install salt-minion   
sudo yum install salt-ssh   
#### Maybe you have to start/restart the services
sudo service salt-minion start    
sudo service salt-master start
#### Accepting the minions on the salt-master
To list all accepted and unaccepted minion keys:   
sudo salt-key -L    
To accept unaccepted minion keys:   
sudo salt-key -A   



