# Assuming CSV columns: name, dob(YYYY-MM-DD), phone, address
# Format each line into an INSERT statement:
awk -F, 'NR>1 {print "INSERT INTO Customer (name,dob,phone,address) VALUES (\x27"$1"\x27, TO_DATE(\x27"$2"\x27,\x27YYYY-MM-DD\x27), \x27"$3"\x27, \x27"$4"\x27);"}' customers.csv > insert_customers.sql
