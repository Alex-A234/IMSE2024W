# OnlineShop


1. Run databases
```bash
$ docker compose up
```

2. Connect to MySQL inside the image to create the database.

```bash
$ docker exec -it online-shop-mysql bash
```

3. Connect to database
```bash
$ mysql -p
> enter password `guest`
```

4. Connect to mongodb

```bash
$ docker exec -it online-shop-mongodb bash
```

5. Connect to db

```bash
$ mongo -u root -p guest --authenticationDatabase admin
```