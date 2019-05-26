sudo yum update 
sudo yum install https://repo.saltstack.com/yum/amazon/salt-amzn-repo-latest.amzn1.noarch.rpm 
sudo yum clean expire-cache 
sudo yum install salt-master 
sudo yum install salt-minion
sudo yum install git
git clone https://kingpfogel@bitbucket.org/kingpfogel/softwareprojekt-storm.git
sudo mkdir -p /srv/{formulas,pillar,salt}
sudo cp softwareprojekt-storm/zookeeper-formula /srv/formulas/
sudo cp softwareprojekt-storm/salt-formula-storm /srv/formulas/
sudo cp softwareprojekt-storm/top.sls /srv/formulas/
sudo mv /srv/formulas/zookeeper-formula/pillar.example /srv/pillar/
cat <<EOT >> master
file_roots:
  base:
    - /srv/salt/
    - /srv/formulas
    - /srv/formulas/zookeeper-formula
    - /srv/formulas/salt-formula-storm
pillar_roots:
  base:
    - /srv/pillar
EOT
sudo rm /etc/salt/master
sudo mv master /etc/salt/

