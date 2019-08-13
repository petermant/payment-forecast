Background notes
-----

SHA-256 - first row with the listed columns from the PDF, without commas, becomes
KKkhBNSD6pIad48vG4ErlsOGMOVFH8kEGDnt1uM97922750157826880164.40


Put that into https://www.movable-type.co.uk/scripts/sha256.html and you get
7d1116866e9dbed9a0df5aef46c24743e0f9e79a7563f33674430f4547cf6a14

...which matches the value in the SHA column. So the check needs to read precise text from the file, and hash that.

The input sheet contains 130,000 rows - plus one header row.

Assume that as part of writing out parsing exceptions, we'll want access to the whole row - including SHA-256 hash. 
Have spotted one row at least which has GBP9.78 together with no column so hash is in wrong place. Another row where two commas after the PayerID which puts all other columns out by 1.
Apart from those two, all other rows have Received UTC prior to Due UTC - so Received UTC must be the time of receiving the instruction to collect the payment.

Due UTC dates span a range from 2020-01-06 to 2020-01-06 with the exception of one payment on 2020-01-34 (!!)

Merchants appear to be repeated. Quick view in Excel reveals only 6 actual merchants

Merchants have
	- ID
	- Name
	- Public key

Payers are also repeated - up to 9 times, with approx 85k payers. Payers have
	- PayerId
	- PayerPubKey


Debit Permission IDs also repeat, although with a different frequency. Again, it appears the debit permission ID links multiple payers to a given merchant.
For a given debit permission ID, e.g. 7626297 - which has 4 rows - there are 4 distinct received UTC values, and 4 distinct Due UTC values. Due amounts also vary.

It's not immediately obvious how to group this further - so sticking with key entities of Merchant and Payer as described above, plus Payment which will have the rest of the attributes, i.e.

- Payment ID (as there is no existing primary key here)
- Merchant ID (FK)
- Payer ID (FK)
- Received UTC
- Debit Permission Id
- Due Epoc (Validate they match? Or discard and just use UTC? In one instance Due UTC wasn't a valid date so might need to investigate 2020-01-34T04:41:09Z)
- Currency
- Amount
- SHA256 hash (transient)


Approach
-----

#####Dataload
- Code basic entities
- Create Spring Batch setup for processing entities, and add tests
#####Page display
- Create grouped queries on the database to extract summary totals per day per Merchant
- Expose those via DAO using DTO class
- Create page template for data display using Thymeleaf - which avoids any client-side rendering via REST API's for now