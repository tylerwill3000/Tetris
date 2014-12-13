Configuring scores database:

Run score_db_setup.sql against the mysql instance on which you'd like to store the database. Next, open up src/model/DBComm.java and set the final variables to the appropriate values to ensure you are able to connect and log into the database.


Building a .jar file:

From ecplise, right click build.xml -> Run As -> Ant Build. By default, this should create "tetris.jar" on your desktop. If you want to change the save destination, open up build.xml and specify the desired path under the "jar.deploy.path" property.