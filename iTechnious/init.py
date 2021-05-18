import pymysql

from statics import config as conf


def init_db():
    connection = pymysql.connect(
        host=conf.DB.host,
        port=conf.DB.port,
        user=conf.DB.user,
        password=conf.DB.password
    )

    with connection.cursor() as cursor:
        cursor.execute("CREATE DATABASE IF NOT EXISTS %s" % conf.DB.db)
        connection.commit()
        connection.close()

    connection = pymysql.connect(
        host=conf.DB.host,
        port=conf.DB.port,
        user=conf.DB.user,
        password=conf.DB.password,
        db=conf.DB.db
    )
    with connection.cursor() as cursor:
        cursor.execute(f"CREATE TABLE IF NOT EXISTS `competitions` ("
                       "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                       "guild_id VARCHAR(100),"
                       "description TEXT,"
                       "custom_fields TEXT,"
                       "manager_role VARCHAR(100),"
                       "manager_chat VARCHAR(100)"
                       ")")

        # cursor.execute(f"CREATE TABLE IF NOT EXISTS `joins` ("
        #                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"
        #                "user_id VARCHAR(255),"
        #                "location TEXT,"
        #                "type VARCHAR(50),"
        #                "permissions TEXT"
        #                ")")

        cursor.execute(f"CREATE TABLE IF NOT EXISTS `teams` ("
                       "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                       "name VARCHAR(255),"
                       "description TEXT,"
                       "guild_id VARCHAR(100),"
                       "members TEXT,"
                       "contact VARCHAR(255),"
                       "status VARCHAR(50),"
                       "joins TEXT"
                       ")")

        print()
        connection.commit()
        connection.close()

    return True
