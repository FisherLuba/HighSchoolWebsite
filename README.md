This is a website I made for my High School's National Honors Society (NHS). It allowed students to post questions and members of NHS or faculty could answer them.

The reason I have a javascript-compiled and javascript-uncompiled is because I was using JSX, so I compiled the JSX into JavaScript and used that.

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

![submit-question-page](https://github.com/user-attachments/assets/221a2f12-300c-4554-8b69-b854b0ec06e7)
![question-response-page](https://github.com/user-attachments/assets/0ecfaf92-2bb6-4db1-af2f-e53dcc1a117f)
![deleting-and-editing](https://github.com/user-attachments/assets/bb84899d-2440-489d-ad0a-dfbf4998af04)
![view-topic-page](https://github.com/user-attachments/assets/1a220edc-65f4-4e0f-b3ca-2af915a9bab7)
