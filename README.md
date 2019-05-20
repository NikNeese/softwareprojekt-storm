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