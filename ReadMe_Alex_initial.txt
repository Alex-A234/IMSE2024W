.examples and .examples.zip contain old projects from 2 years ago - so its likely that some changes in the requirements were made in the meantime.

### OnlineShop is my favorite of the examples:
- only example that can be run with just `docker compose up -d` and nothing else
- uses a self-signed certificate for HTTPS
- it has different kinds of users
- a nice GUI
- already uses MongoDB (and MySQL instead of MariaDB)

### run:
`docker compose up -d`

### Web interface:
'http://localhost:80'


### Work distribution proposal:
- (Person 1) run MariaDB instead of MySQL (so that single INSERT statements can be run)
- (Person 1) application of an automated data generator & updating the "fill database" script
- (Person 2) designing & implementing the noSQL data structure
- (Person 1/2?) updating the SQL -> noSQL migration
- (Person 2) updating the Web interface

### Individual work (later):
- if working on SQL: implement personal use case in SQL
- if working on noSQL: implement personal use case in noSQL
then:
- implement personal use case in the other database


### final work:
- refactoring, updating used program versions, (changing) the image structure to truly make this our project and definitely not a case of plagiarism
