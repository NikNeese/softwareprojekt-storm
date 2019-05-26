mkdir -p /srv/{formulas,pillar,salt}
cd /srv/formulas
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
