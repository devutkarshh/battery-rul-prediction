import mysql.connector

conn = mysql.connector.connect(
    host='localhost',
    user='root',
    password='root123'
)
cursor = conn.cursor()

# Drop existing database
cursor.execute("DROP DATABASE IF EXISTS job_portal")
print("✅ Dropped existing database")

# Read and execute init script
with open(r'c:\Users\User\CODING\job\database\init.sql', 'r') as f:
    script = f.read()
    for statement in script.split(';'):
        if statement.strip():
            try:
                cursor.execute(statement)
            except Exception as e:
                print(f"Statement error: {e}")

conn.commit()
cursor.close()
conn.close()
print("✅ Database initialized successfully!")
