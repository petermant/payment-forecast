Background notes
----------------

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
--------

#####Dataload
- Code basic entities
- Create Spring Batch setup for processing entities, and add tests
#####Page display
- Create grouped queries on the database to extract summary totals per day per Merchant
- Expose those via DAO using DTO class
- Create page template for data display using Thymeleaf - which avoids any client-side rendering via REST API's for now


DTO classes
-----------
Need a matrix of total value of payments per day, where payments after 4pm go into the next day.

- There seems to be no sort order on the front end example, so assume order is not crucial.
- Some values still to 2d.p. in example, so stick with BigDecimal and format with commas for 1,000's
- But don't display zeroes if rounds to a pound? Add later...
- Possible enhancement: Currency ... display as £ for now as all GDP

For now, use a DTO which is a list with one entry for each table row. Each DTO has a date, and a map of each merchant name to their
amount for that day, calculated within the 4pm bounds.

#####Queries

The following query is a good starting point

```h2
select hour(DUE_EPOC), trunc(due_epoc) + case hour(DUE_EPOC)>=16 when true then 1 else 0 end as modified_epoc, due_epoc from
              (
                select distinct parsedatetime(formatdatetime(due_Epoc, 'yyyy-MM-DD HH:mm'), 'yyyy-MM-DD HH:mm') as due_epoc
                from payment
              )
order by 3 desc;
```

Either side of 4pm that gives

Hour | modified_epoc | due_epoc |
| ---|:-------------:| --------:|
16	|2020-01-**09** 00:00:00.000000000	|2020-01-**08** 16:01:00.000000000|
16	|2020-01-**09** 00:00:00.000000000	|2020-01-**08** 16:00:00.000000000|
15	|2020-01-08 00:00:00.000000000	|2020-01-08 15:59:00.000000000|
15	|2020-01-08 00:00:00.000000000	|2020-01-08 15:58:00.000000000|
15	|2020-01-08 00:00:00.000000000	|2020-01-08 15:57:00.000000000|

So, final query that gives me what I need uses the same logic above but groups by payer and distinct date:


```h2
select forecast.forecast_collected_day, m.MERCHANT_NAME, sum(forecast.AMOUNT) as daily_amount from
  (
    select MERCHANT_ID, amount, formatdatetime(trunc(due_epoc) + case hour(DUE_EPOC)>=16 when true then 1 else 0 end, 'yyyy-MM-DD') as forecast_collected_day
    from payment
  ) forecast
inner join MERCHANT m on m.ID = forecast.MERCHANT_ID
group by forecast_collected_day, MERCHANT_ID
```

[technically I probably ought to be looking more closely at how sum(amount) behaves...any loss of precision?]


Performance
-----
Currently seems OK
- Loading up all rows into the DB takes circa 30 seconds
- Requesting the summary on the web page takes circa 2.5s the first time, thereafter it's circa 20ms as results are cached in the DB.