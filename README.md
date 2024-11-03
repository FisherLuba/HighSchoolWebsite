This is a website I made for my High School's National Honors Society (NHS). It allowed students to post questions and members of NHS or faculty could answer them.

The reason I have a javascript-compiled and javascript-uncompiled is because I was using JSX, so I compiled the JSX into JavaScript and used that. I definitely could have done something better.

This branch is just if you want to look at the website on your own computer with localhost. Some of the stuff doesn't work such as oauth, so only one account can be used and you cannot log out/in. 
Some things are most likely broken because of this.

# Notes / Personal Reminders

Setting up on Oracle VPS

*cp = command prompt

change directory = cd / then cd ssh-keys

(1st cp) connect to ssh =  ssh -i <key-file> opc@<ip>

(2nd cp) connect to sftp =  sftp -i <key-file> opc@<ip>

(1st cp) remove previous file = sudo rm <file name>

(2nd cp) upload file = put <jar file>

(1st cp) list processes = ps aux | grep java

(1st cp) then do sudo kill <id> (one of the numbers that pops up)

(1st cp) run file = sudo nohup java -jar <jar file>

https://stackoverflow.com/questions/17385794/how-to-get-the-process-id-to-kill-a-nohup-process

https://yoursunny.com/t/2020/OracleCloud-website/

https://spring.io/guides/tutorials/spring-boot-oauth2/

https://www.atlantic.net/dedicated-server-hosting/how-to-install-and-secure-postgresql-server-on-oracle-linux-8/

Oracle VPS Ip: <IP>

# psql -U postgres -W  -h localhost
# \c schooldb - connect to schooldb