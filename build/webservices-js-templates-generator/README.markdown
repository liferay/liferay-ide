1.Install the liferay ide 2.2
2.Copy your own pom file to root folder of this plugin.
3.Import this plugin project as a maven project
4.You may need to change the server profile following your own server config in pom.xml
5.Start your server
6.Right click on this project and select Liferay->Maven->liferay:deploy
7.Open the page: http://localhost:8080/jsonws-js-templates-generator in browser and the console will show the message "success:${yourserverhome}\webapps\jsonws-js-templates-generator\jsonws-js-templates.xml"
8.Go to this location and copy the xml file to ide plugins folder